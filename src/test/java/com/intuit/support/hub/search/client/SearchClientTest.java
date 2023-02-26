package com.intuit.support.hub.search.client;

import com.intuit.support.hub.fetch.client.entities.SupportCase;
import com.intuit.support.hub.fetch.internal.SupportCaseRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SearchClientTest {
    @Autowired
    SearchClient client;

    @Autowired
    SupportCaseRepository supportCaseRepository;

    @Test
    public void search() {
        String err = "344";
        String status = "Open";
        String provider = "Bla";

        SupportCase expected1 = new SupportCase();
        expected1.setCrm("crm1");
        expected1.setCaseId("1");
        expected1.setStatus(status);
        expected1.setErrorCode(err);
        expected1.setProvider(provider);

        supportCaseRepository.save(expected1);

        Assert.assertEquals(expected1, client.findByErrorCode(err).get(0));
        Assert.assertEquals(expected1, client.findByProvider(provider).get(0));
        Assert.assertEquals(expected1, client.findByStatus(status).get(0));
        Assert.assertEquals(0, client.findByStatus("bla").size());
    }

}
