package com.intuit.support.hub.refresh.client.responses;

import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequestResponse {
    long durationToWait;
    String info;
    Collection<AggregationResult> lastResult;
}
