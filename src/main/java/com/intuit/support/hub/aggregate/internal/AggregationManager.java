package com.intuit.support.hub.aggregate.internal;

import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import com.intuit.support.hub.aggregate.client.entities.AggregationResultKey;
import com.intuit.support.hub.fetch.client.entities.SupportCase;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AggregationManager {
    public Collection<AggregationResult> aggregate(List<SupportCase> updatedCases) {
        Map<AggregationResultKey, AggregationResult> results = new HashMap<>();
        updatedCases.stream().filter(x -> x.getStatus().equals("Open")).forEach(c -> {
                AggregationResultKey key = new AggregationResultKey(c.getErrorCode(), c.getProductName());
                AggregationResult res = results.getOrDefault(key, new AggregationResult());
                res.setId(key);
                res.getCases().add(c.getCrm() + ":" + c.getCaseId());
                results.put(key, res);
        });
        return results.values();
    }
}
