package com.intuit.support.hub.fetch.client;

import com.intuit.support.hub.crm.client.CRMClient;
import com.intuit.support.hub.crm.client.entities.CRM;
import com.intuit.support.hub.fetch.client.entities.SupportCase;
import com.intuit.support.hub.fetch.client.responses.RefreshCRMResult;
import com.intuit.support.hub.fetch.internal.GetCasesResponse;
import com.intuit.support.hub.fetch.internal.SupportCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
/**
 * Service used to fetch and persist support cases from the needed {@link CRM} systems
 */
public class FetchClient {
    @Autowired
    private CRMClient crmClient;

    @Autowired
    private SupportCaseRepository caseRepo;

    final RestTemplate restTemplate;

    public FetchClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    /**
     * Fetches tickets from all the CRMs system and saves it to the DB
     * @return List of {@link RefreshCRMResult} containing the result of each CRM refresh
     */
    public List<RefreshCRMResult> refreshAll() {
        List<CRM> crms = crmClient.getCRMsForRefresh();
        return crms.parallelStream().map(crm -> {
            RefreshCRMResult res = new RefreshCRMResult();
            res.setCrmName(crm.getName());

            try {
                List<SupportCase> cases = fetchForCrm(crm);
                caseRepo.saveAll(cases);
                res.setCases(cases);
            } catch (Exception e) {
                res.setError(String.format("Failed to refresh CRM (name: %s, id %d) with exception %s",crm.getName(), crm.getId(), e));
            }

            return res;

        }).collect(Collectors.toList());
    }

    /**
     *
     * @param crm CRM system to fetch cases for
     * @return List of {@link SupportCase} fetched from the system
     */
     List<SupportCase> fetchForCrm(CRM crm) {
        String serviceUrl = crm.getEndpoint();
        if (serviceUrl.equals("")) return Collections.emptyList();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GetCasesResponse casesResponse = restTemplate.getForObject(serviceUrl, GetCasesResponse.class);
        List<SupportCase> cases = casesResponse.getData();
        cases.forEach( x -> x.setCrm(crm.getName()));
        return casesResponse.getData();


    }

}
