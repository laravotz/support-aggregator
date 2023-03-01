package com.intuit.support;

import com.intuit.support.hub.aggregate.client.AggregateClient;
import com.intuit.support.hub.crm.client.entities.CRM;
import com.intuit.support.hub.crm.internal.CRMRepository;
import com.intuit.support.hub.fetch.client.entities.SupportCase;
import com.intuit.support.hub.fetch.internal.SupportCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    @Autowired
    CRMRepository crmRepo;

    @Override
    public void run(String...args) throws Exception {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return;
            }
        }

        CRM banana = new CRM(1, "banana", "http://localhost:8080//banana");
        CRM strawberry = new CRM(2, "strawberry", "http://localhost:8080//strawberry");
        CRM failure = new CRM(3, "failure", "http://localhost:8080/failure");
        crmRepo.saveAll(Arrays.asList(banana,strawberry,failure));
    }




}
