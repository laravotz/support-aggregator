package com.intuit.support.hub.refresh.internal;

import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import com.intuit.support.hub.aggregate.client.entities.AggregationResultKey;
import com.intuit.support.hub.aggregate.internal.AggregationManager;
import com.intuit.support.hub.fetch.client.FetchClient;
import com.intuit.support.hub.fetch.client.entities.SupportCase;
import com.intuit.support.hub.fetch.client.responses.RefreshCRMResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collection;

@SpringBootTest
public class RefreshManagerTest {
    @Autowired
    RefreshManager manager;
    @MockBean
    FetchClient fetchClient;

    @Autowired
    AggregationManager aggregationManager;

    @Test
    public void performRefresh() {
        prepareFetchResponse();
        manager.performRefresh();
        Collection<AggregationResult> actual = aggregationManager.getAggregationResult();
        AggregationResult expected = new AggregationResult();
        expected.setId(new AggregationResultKey("1", "ProductA"));
        expected.setCases(Arrays.asList("crm1:1", "crm2:2"));

        Assert.assertEquals(expected, actual.iterator().next());
    }

    private void prepareFetchResponse() {
        SupportCase expected1 = new SupportCase();
        expected1.setCrm("crm1");
        expected1.setCaseId("1");
        expected1.setStatus("Open");
        expected1.setErrorCode("1");
        expected1.setProductName("ProductA");

        SupportCase expected2 = new SupportCase();
        expected2.setCrm("crm2");
        expected2.setCaseId("2");
        expected2.setStatus("Open");
        expected2.setErrorCode("1");
        expected2.setProductName("ProductA");

        SupportCase expected3 = new SupportCase();
        expected3.setCrm("crm2");
        expected3.setCaseId("2");
        expected3.setStatus("Closed");
        expected3.setErrorCode("2");
        expected3.setProductName("ProductA");

        RefreshCRMResult res1 = new RefreshCRMResult();
        res1.setCrmName("crm1");
        res1.setCases(Arrays.asList(expected1));

        RefreshCRMResult res2 = new RefreshCRMResult();
        res2.setCrmName("crm2");
        res2.setCases(Arrays.asList(expected2, expected3));

        Mockito.when(fetchClient.refreshAll()).thenReturn(Arrays.asList(res1, res2));
    }
}
