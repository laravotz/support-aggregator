package com.intuit.support.hub.crm.client;

import com.intuit.support.hub.crm.client.entities.CRM;
import com.intuit.support.hub.crm.internal.CRMRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CRMClientTest {

    @Autowired
    CRMClient client;

    @Autowired
    CRMRepository repo;

    @Test
    public void getCRMsForRefresh() {
        CRM crm1 = new CRM();
        crm1.setEndpoint("/bla");
        crm1.setName("test1");

        repo.save(crm1);

        List<CRM> crms = client.getCRMsForRefresh();
        Assert.assertEquals(crm1, crms.get(0));
    }

}
