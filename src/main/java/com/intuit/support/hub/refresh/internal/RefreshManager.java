package com.intuit.support.hub.refresh.internal;

import com.intuit.support.hub.aggregate.client.AggregateClient;
import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import com.intuit.support.hub.fetch.client.FetchClient;
import com.intuit.support.hub.fetch.client.responses.RefreshCRMResult;
import com.intuit.support.hub.refresh.client.responses.RefreshRequestResponse;
import com.intuit.support.hub.refresh.client.responses.RefreshResult;
import com.intuit.support.hub.utils.Cache;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    private final String lastRefreshResultKey = "lastRefreshResult";
    private final String lastUpdateTimeKey = "lastUpdateTime";
    private final String currUpdateStartTimeKey = "currUpdateStartTime";
    private final String lastUpdateDurationKey = "lastUpdateDuration";

    private static Cache<String, Object> cache = new Cache<>();

    private RefreshManager() {
        RefreshResult refreshResult = new RefreshResult(Collections.emptyList(), Arrays.asList("No successful refresh after service start yet"));
        cache.put(lastRefreshResultKey, new AtomicReference<RefreshResult>(refreshResult));
        cache.put(lastUpdateTimeKey, new AtomicReference<>(LocalDateTime.of(1970,1,1,1,1)));
        cache.put(currUpdateStartTimeKey, new AtomicReference<>(LocalDateTime.of(1970,1,1,1,1)));
        cache.put(lastUpdateDurationKey, new AtomicLong(1));
    }

    private Lock updateLock = new ReentrantLock();

    @Value("${user.interval.seconds}")
    private long userIntervalSeconds;

    @Value("${wait.duration.multiplier}")
    private long waitDurationMultiplier;

    @Autowired
    private AggregateClient aggregateClient;
    @Autowired
    private FetchClient fetchClient;

    @PostConstruct
    public void init() {
        log.debug("starting to perform refresh on startup");
        performRefresh();
    }

     /**
      * performs a refresh by fetching the cases and then aggregate it.
      * In order to avoid concurrent refreshes, uses a lock and returns if the lock can't be acquired.
      */
     @Scheduled(fixedDelayString = "${refresh.interval.minutes}", timeUnit =  TimeUnit.MINUTES)
     boolean performRefresh() {
         log.debug("Attempting to perform scheduled refresh");
        if (updateLock.tryLock()) {
            try {
                LocalDateTime start = setCacheTime(currUpdateStartTimeKey);

                // refresh all systeams
                List<RefreshCRMResult> refreshed = fetchClient.refreshAll();

                // create aggregated results
                Collection<AggregationResult> aggRes = aggregateClient.aggregate(refreshed.stream().filter(x -> x.getError() == null).flatMap(x -> x.getCases().stream()).toList());

                RefreshResult refreshResult = new RefreshResult();
                refreshResult.setAggResult(aggRes);
                refreshResult.setErrors(refreshed.stream().filter(x -> x.getError() != null).map(x -> x.getError()).toList());

                cache.put(lastRefreshResultKey, refreshResult);

                LocalDateTime endTime = setCacheTime(lastUpdateTimeKey);

                AtomicLong lastUpdateDuration = (AtomicLong)cache.get(lastUpdateDurationKey);
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
        LocalDateTime lastUpdateTime = getCacheTime(lastUpdateTimeKey);
        long diff = ChronoUnit.SECONDS.between(lastUpdateTime, curr);

        if (diff < userIntervalSeconds) {
            log.debug("User request update is less than {} seconds from the previous successful update, skipping", userIntervalSeconds);
            response.setRefreshResult(getLastRefreshResult());
            response.setFurtherInfo(String.format("Data was refreshed recently, please wait %d minutes and try again", (userIntervalSeconds - diff) / 60 + 1));
            return response;
        }

        if (performRefresh()) {
            response.setRefreshResult(getLastRefreshResult());
        } else {
            // time to wait before attempting to fetch fresh results
            long timeSinceStart = ChronoUnit.SECONDS.between(getCacheTime(currUpdateStartTimeKey), curr) * waitDurationMultiplier;
            long diffFromLastDuration = getLastUpdateDuration() - timeSinceStart;
            long withBuffer = diffFromLastDuration * waitDurationMultiplier;
            response.setDurationToWait(withBuffer);
        }

        return response;
    }

    private LocalDateTime setCacheTime(String key) {
        LocalDateTime time = LocalDateTime.now();
        AtomicReference<LocalDateTime> currTime = (AtomicReference<LocalDateTime>)cache.get(key);
        currTime.set(time);
        return time;
    }

    private LocalDateTime getCacheTime(String key) {
        return ((AtomicReference<LocalDateTime>)cache.get(key)).get();
    }

    private long getLastUpdateDuration() {
        return ((AtomicLong)cache.get(lastUpdateDurationKey)).get();
    }

    public RefreshResult getLastRefreshResult() {
        return ((RefreshResult) cache.get(lastRefreshResultKey));
    }
}
