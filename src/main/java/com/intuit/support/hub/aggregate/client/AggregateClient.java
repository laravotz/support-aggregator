package com.intuit.support.hub.aggregate.client;

import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import com.intuit.support.hub.aggregate.internal.AggregationManager;
import com.intuit.support.hub.fetch.client.entities.SupportCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class AggregateClient {
    @Autowired
    AggregationManager manager;

    public Collection<AggregationResult> updateAggregation(List<SupportCase> cases) {
        return manager.updateAggregation(cases);
    }

    public Collection<AggregationResult> getAggregationResult() {
        return manager.getAggregationResult();
    }
}
