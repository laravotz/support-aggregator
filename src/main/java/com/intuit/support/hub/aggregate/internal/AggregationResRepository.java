package com.intuit.support.hub.aggregate.internal;

import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import com.intuit.support.hub.aggregate.client.entities.AggregationResultKey;
import org.springframework.data.repository.ListCrudRepository;

interface AggregationResRepository extends ListCrudRepository<AggregationResult, AggregationResultKey> {
}

