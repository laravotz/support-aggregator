package com.intuit.support.hub.aggregate.internal;

import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import com.intuit.support.hub.aggregate.client.entities.AggregationResultKey;
import com.intuit.support.hub.fetch.client.entities.SupportCase;
import com.intuit.support.hub.utils.Cache;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("singleton")
public class AggregationManager {
    private static Cache<AggregationResultKey, AggregationResult> cache = new Cache<>();

    public Collection<AggregationResult> getAggregationResult() {
        return new LinkedList<>(cache.getAll());
    }

    public Collection<AggregationResult> updateAggregation(List<SupportCase> updatedCases) {
        List<AggregationResult> updated = new LinkedList<>();
        cache.clear();
        updatedCases.stream().filter(x -> x.getStatus().equals("Open")).forEach(c -> {
                AggregationResultKey key = new AggregationResultKey(c.getErrorCode(), c.getProductName());
                AggregationResult res = cache.getOrDefault(key, new AggregationResult());
                res.setId(key);
                res.getCases().add(c.getCrm() + ":" + c.getCaseId());
                cache.put(key, res);
                updated.add(res);
        });
        return getAggregationResult();
    }
}
