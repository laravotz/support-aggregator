package com.intuit.support.hub.gateway.client;

import com.intuit.support.hub.aggregate.client.AggregateClient;
import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import com.intuit.support.hub.fetch.client.entities.SupportCase;
import com.intuit.support.hub.refresh.client.responses.RefreshRequestResponse;
import com.intuit.support.hub.refresh.internal.RefreshManager;
import com.intuit.support.hub.search.client.SearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
/**
 * REST controller that 'merges' all external client apis to be used by clients of the service
 */
public class GatewayClient {
    @Autowired
    private RefreshManager refreshClient;

    @Autowired
    private AggregateClient aggregateClient;

    @Autowired
    private SearchClient searchClient;

    @PutMapping("/refresh")
    public RefreshRequestResponse refresh() {
        return refreshClient.userRefresh();
    }

    @GetMapping("/aggregated")
    public Collection<AggregationResult> getAggregatedData() {
        return aggregateClient.getAggregationResult();
    }

    @GetMapping("/searchByError")
    public List<SupportCase> findByErrorCode(@RequestParam String error) {
        return searchClient.findByErrorCode(error);
    }

    @GetMapping("/searchByProvider")
    public List<SupportCase> findByProvider(@RequestParam String provider) {
        return searchClient.findByProvider(provider);
    }

    @GetMapping("/searchByStatus")
    public List<SupportCase> findByStatus(@RequestParam String status) {
        return searchClient.findByStatus(status);
    }

}
