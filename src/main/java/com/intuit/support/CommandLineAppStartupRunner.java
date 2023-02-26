package com.intuit.support;

import com.intuit.support.hub.aggregate.client.AggregateClient;
import com.intuit.support.hub.aggregate.client.entities.AggregationResult;
import com.intuit.support.hub.crm.client.entities.CRM;
import com.intuit.support.hub.crm.internal.CRMRepository;
import com.intuit.support.hub.fetch.client.entities.SupportCase;
import com.intuit.support.hub.fetch.internal.SupportCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    @Autowired
    CRMRepository crmRepo;

    @Autowired
    AggregateClient aggregateClient;

    @Autowired
    SupportCaseRepository supportCaseRepo;

    @Override
    public void run(String...args) throws Exception {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return;
            }
        }

        CRM banana = new CRM(1, "banana", "");
        crmRepo.save(banana);
        CRM strawberry = new CRM(2, "strawberry", "");
        crmRepo.save(strawberry);

        SupportCase caseA = createCase("1", "Product A", "1", "crmA","Open", "1");
        SupportCase caseB = createCase("1", "Product A", "1", "crmB","Open", "2");

        SupportCase caseC = createCase("1", "Product B", "2", "crmA", "Open", "1");
        SupportCase caseD = createCase("2", "Product A", "3", "crmA","Open","3");
        SupportCase caseE = createCase("2", "Product A", "4", "crmA","Close", "1");

        supportCaseRepo.saveAll(Arrays.asList(caseA, caseB, caseC, caseD, caseE));
        aggregateClient.updateAggregation(Arrays.asList(caseA, caseB, caseC, caseD));

    }

    private SupportCase createCase(String err, String product, String caseId, String crm, String status, String provider) {
        SupportCase sc = new SupportCase();
        sc.setErrorCode(err);
        sc.setProductName(product);
        sc.setStatus(status);
        sc.setCaseId(caseId);
        sc.setCrm(crm);
        sc.setProvider(provider);
        return sc;
    }


}
