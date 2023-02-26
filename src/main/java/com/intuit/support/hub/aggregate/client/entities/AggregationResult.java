package com.intuit.support.hub.aggregate.client.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Entity
@Data
public class AggregationResult {
    @EmbeddedId
    private AggregationResultKey id;
    private List<String> cases = new LinkedList<>();
}
