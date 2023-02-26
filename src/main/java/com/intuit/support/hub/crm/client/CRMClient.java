package com.intuit.support.hub.crm.client;

import com.intuit.support.hub.crm.client.entities.CRM;
import com.intuit.support.hub.crm.internal.CRMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CRMClient {
     @Autowired
     CRMRepository repo;

    /**
     * Returns CRMs for refresh
     */
    public List<CRM> getCRMsForRefresh() {
        return repo.findAll();
    }
}
