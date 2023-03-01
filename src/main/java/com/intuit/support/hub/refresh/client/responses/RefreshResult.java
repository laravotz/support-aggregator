package com.intuit.support.hub.refresh.client.responses;

import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshResult {
    Collection<AggregationResult> aggResult;
    List<String> errors;
}
