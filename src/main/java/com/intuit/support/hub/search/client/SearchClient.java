package com.intuit.support.hub.search.client;

import com.intuit.support.hub.fetch.client.entities.SupportCase;
import com.intuit.support.hub.search.internal.SearchSupportCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchClient {
    @Value("${search.query.limit}")
    private int searchResultLimit;

    @Autowired
    private SearchSupportCaseRepository supportCaseRepository;

    public List<SupportCase> findByErrorCode(String error) {
        return supportCaseRepository.findByErrorCode(searchResultLimit, error);
    }

    public List<SupportCase> findByProvider(String provider) {
        return supportCaseRepository.findByProviderName(searchResultLimit, provider);
    }

    public List<SupportCase> findByStatus(String status) {
        return supportCaseRepository.findByStatus(searchResultLimit, status);
    }
}
