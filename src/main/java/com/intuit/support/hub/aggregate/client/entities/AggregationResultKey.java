package com.intuit.support.hub.aggregate.client.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AggregationResultKey {
    private String error;
    private String product;
}
