package com.intuit.support.hub.search.internal;

import com.intuit.support.hub.fetch.client.entities.SupportCase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchSupportCaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<SupportCase> findByProviderName(int limit, String provider) {
        TypedQuery<SupportCase> query = entityManager.createQuery("SELECT sc FROM SupportCase sc WHERE sc.provider = :provider ORDER BY  sc.lastModified", SupportCase.class);
        query.setParameter("provider", provider);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<SupportCase> findByErrorCode(int limit, String error) {
        TypedQuery<SupportCase> query = entityManager.createQuery("SELECT sc FROM SupportCase sc WHERE sc.errorCode = :error ORDER BY sc.lastModified", SupportCase.class);
        query.setParameter("error", error);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<SupportCase> findByStatus(int limit, String status) {
        TypedQuery<SupportCase> query = entityManager.createQuery("SELECT sc FROM SupportCase sc WHERE sc.status = :status ORDER BY sc.lastModified", SupportCase.class);
        query.setParameter("status", status);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
