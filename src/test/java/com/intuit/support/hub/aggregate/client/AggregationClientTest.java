package com.intuit.support.hub.aggregate.client;

import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import com.intuit.support.hub.aggregate.client.entities.AggregationResultKey;
import com.intuit.support.hub.fetch.client.entities.SupportCase;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SpringBootTest
public class AggregationClientTest {
    @Autowired
    AggregateClient aggregateClient;

    @Test
    public void multipleCases() {
        SupportCase caseA = createCase("1", "Product A", "1", "crmA","Open");
        SupportCase caseB = createCase("1", "Product A", "1", "crmB","Open");

        SupportCase caseC = createCase("1", "Product B", "1", "crmA", "Open");
        SupportCase caseD = createCase("2", "Product A", "1", "crmA","Open");

        Collection<AggregationResult> res =  aggregateClient.updateAggregation(Arrays.asList(caseA, caseB, caseC, caseD));

        AggregationResultKey expectedProductAErr1 = new AggregationResultKey("1", "Product A");
        AggregationResultKey expectedProductBErr1 = new AggregationResultKey("1", "Product B");
        AggregationResultKey expectedProductAErr2 = new AggregationResultKey("2", "Product A");

        validate(res, expectedProductAErr1, 2);
        validate(res, expectedProductBErr1, 1);
        validate(res, expectedProductAErr2, 1);
    }

    @Test
    public void closeCase() {
        SupportCase caseA = createCase("1", "Product A", "1", "crmA","Close");
        Collection<AggregationResult> res =  aggregateClient.updateAggregation(Arrays.asList(caseA));
        Assert.assertTrue("Aggregation result should be empty", res.isEmpty());
    }

    @Test
    public void multipleRefreshes() {
        SupportCase caseA = createCase("1", "Product A", "1", "crmA","Open");
        aggregateClient.updateAggregation(Arrays.asList(caseA));
        SupportCase caseB = createCase("2", "Product B", "1", "crmA","Open");
        Collection<AggregationResult> res =  aggregateClient.updateAggregation(Arrays.asList(caseB));
        Assert.assertTrue("Aggregation result should have one item", res.size() == 1);
        Assert.assertEquals(res.iterator().next().getId().getProduct(), "Product B");
    }

    private void validate(Collection<AggregationResult> res, AggregationResultKey id, int size) {
        List<AggregationResult> filtered = res.stream().filter(x -> x.getId().equals(id)).toList();
        Assert.assertEquals(1, filtered.size(), 1);
        AggregationResult aggRes = filtered.get(0);
        Assert.assertEquals(size, aggRes.getCases().size());
    }

    private SupportCase createCase(String err, String product, String caseId, String crm, String status) {
        SupportCase sc = new SupportCase();
        sc.setErrorCode(err);
        sc.setProductName(product);
        sc.setStatus(status);
        sc.setCaseId(caseId);
        sc.setCrm(crm);
        return sc;
    }
}
