package com.intuit.support.hub.refresh.internal;

import com.intuit.support.hub.aggregate.client.AggregateClient;
import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import com.intuit.support.hub.fetch.client.FetchClient;
import com.intuit.support.hub.fetch.client.responses.RefreshCRMResult;
import com.intuit.support.hub.refresh.client.responses.RefreshRequestResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Log4j2
@Scope("singleton")
/**
 * Class that handles refreshes (either by users or timed by the system).
 * In order to avoid concurrent refreshes (race conditions) uses a lock.
 */
public class RefreshManager {
    private AtomicReference<Collection<AggregationResult>> aggResult = new AtomicReference<>();
    private AtomicReference<LocalDateTime> lastUpdateTime = new AtomicReference<>();

    private AtomicReference<LocalDateTime> currUpdateStartTime = new AtomicReference<>();

    private Lock updateLock = new ReentrantLock();

    @Value("${user.interval}")
    private long userInterval;

    @Value("${wait.duration.multiplier}")
    private long waitDurationMultiplier;

    @Autowired
    private AggregateClient aggregateClient;
    @Autowired
    private FetchClient fetchClient;

    private AtomicLong lastUpdateDuration = new AtomicLong(10);

    @PostConstruct
    public void init() {
        performRefresh();
    }

     @Scheduled(fixedDelayString = "${refresh.interval}")
     /**
      * performs a refresh by fetching the cases and then aggregate it.
      * In order to avoid concurrent refreshes, uses a lock and returns if the lock can't be acquired.
      */
     boolean performRefresh() {
         log.debug("Attempting to perform scheduled refresh");
        if (updateLock.tryLock()) {
            try {
                LocalDateTime start = LocalDateTime.now();
                currUpdateStartTime.set(start);
                List<RefreshCRMResult> refreshed = fetchClient.refreshAll();
                aggregateClient.updateAggregation(refreshed.stream().filter(x -> x.getError() == null).flatMap(x -> x.getCases().stream()).toList());
                LocalDateTime endTime = LocalDateTime.now();
                lastUpdateTime.set(endTime);
                lastUpdateDuration.set(ChronoUnit.SECONDS.between(start, endTime));
            } catch (Exception e) {
                log.error("Scheduled refresh failed with: ", e);
                return false;
            } finally {
                updateLock.unlock();
            }
            return true;
        }
        return false;
    }

    /**
     * performs a user triggered refresh.
     * in case of an ongoing refresh contains time for the client to wait before trying to fetch cases
     * (so it'll receive updated list) to avoid waiting clients and open sockets.
     * @return
     */
    public RefreshRequestResponse userRefresh() {
        RefreshRequestResponse response = new RefreshRequestResponse();
        LocalDateTime curr = LocalDateTime.now();
        long diff = ChronoUnit.SECONDS.between(curr, lastUpdateTime.get());
        if (diff < userInterval) {
            log.debug("User request update is less than {} seconds from the previous successful update, skipping", userInterval);
            response.setLastResult(aggResult.get());
            response.setInfo(String.format("Data was refreshed recently, please wait %d minutes and try again", (userInterval - diff) / 60 + 1));
            return response;
        }

        if (performRefresh()) {
            response.setLastResult(aggregateClient.getAggregationResult());
        } else {
            // time to wait before attempting to fetch fresh results
            long timeSinceStart = ChronoUnit.SECONDS.between(currUpdateStartTime.get(), curr) * waitDurationMultiplier;
            long diffFromLastDuration = lastUpdateDuration.get() - timeSinceStart;
            long withBuffer = diffFromLastDuration * waitDurationMultiplier;
            response.setDurationToWait(withBuffer);
        }

        return response;
    }

}
