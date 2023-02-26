package com.intuit.support.hub.crm.internal;

import com.intuit.support.hub.crm.client.entities.CRM;
import org.springframework.data.repository.ListCrudRepository;

public interface CRMRepository extends ListCrudRepository<CRM, Long> {
}
