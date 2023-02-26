package com.intuit.support.hub.fetch.internal;

import com.intuit.support.hub.fetch.client.entities.SupportCase;
import org.springframework.data.repository.ListCrudRepository;

public interface SupportCaseRepository extends ListCrudRepository<SupportCase, Long> {
}
