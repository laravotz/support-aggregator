package com.intuit.support.hub.fetch.client;

import com.intuit.support.hub.crm.client.CRMClient;
import com.intuit.support.hub.crm.client.entities.CRM;
import com.intuit.support.hub.fetch.client.entities.SupportCase;
import com.intuit.support.hub.fetch.client.responses.RefreshCRMResult;
import com.intuit.support.hub.fetch.internal.SupportCaseRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
public class FetchClientTest {

    @SpyBean
    FetchClient fetchClient;

    @MockBean
    CRMClient crmClient;

    @Autowired
    SupportCaseRepository repo;

    private MockRestServiceServer mockServer;

    @Test
    public void refreshAll() throws Exception {
        CRM crm1 = new CRM();
        crm1.setName("crm1");
        CRM crm2 = new CRM();
        crm2.setName("crm2");

        CRM crm3 = new CRM();
        crm3.setName("crm3");

        Mockito.when(crmClient.getCRMsForRefresh()).thenReturn(Arrays.asList(crm1, crm2, crm3));
        SupportCase expected1 = new SupportCase();
        expected1.setCrm("crm1");
        expected1.setCaseId("1");
        expected1.setErrorCode("1");
        expected1.setStatus("Open");

        SupportCase expected2 = new SupportCase();
        expected2.setCrm("crm1");
        expected2.setCaseId("2");
        expected2.setErrorCode("2");
        expected2.setStatus("Closed");

        SupportCase expected3 = new SupportCase();
        expected3.setCrm("crm2");
        expected3.setCaseId("2");
        expected3.setErrorCode("1");
        expected3.setStatus("Closed");


        Mockito.doReturn(Arrays.asList(expected1, expected2)).when(fetchClient).fetchForCrm(crm1);
        Mockito.doReturn(Arrays.asList(expected3)).when(fetchClient).fetchForCrm(crm2);
        Mockito.doThrow(new RuntimeException("bla")).when(fetchClient).fetchForCrm(crm3);

        List<RefreshCRMResult> res = fetchClient.refreshAll();
        Assert.assertEquals(3, res.size());
        RefreshCRMResult crm1Res = res.stream().filter(x -> x.getCrmName().equals("crm1")).findFirst().get();
        Assert.assertTrue(crm1Res.getCases().contains(expected1));
        Assert.assertTrue(crm1Res.getCases().contains(expected2));


        RefreshCRMResult crm2Res = res.stream().filter(x -> x.getCrmName().equals("crm2")).findFirst().get();
        Assert.assertTrue(crm2Res.getCases().contains(expected3));

        RefreshCRMResult crm3Res = res.stream().filter(x -> x.getCrmName().equals("crm3")).findFirst().get();
        Assert.assertNotNull(crm3Res.getError());

        // Test that all were saved to DB
        repo.findAll().containsAll(Arrays.asList(expected1, expected2, expected3));
    }
    @Test
    public void fetchForCrm() throws Exception {
        RestTemplate template = fetchClient.restTemplate;
        mockServer = MockRestServiceServer.createServer(template);
        CRM crm1 = new CRM();
        crm1.setEndpoint("http://localhost:8080/bla");
        crm1.setName("test1");


        String response = """
                { "data": [
                {
                "Case ID": 1,
                "Customer_ID": 818591,
                "Provider": 6111,
                "CREATED_ERROR_CODE": 324,
                "STATUS": "Open",
                "TICKET_CREATION_DATE": "3/14/2019 23:45",
                "LAST_MODIFIED_DATE": "3/17/2019 21:34", 
                "PRODUCT_NAME": "BLUE"
                },
                         {
                "Case ID": 2,
                "Customer_ID": 2828,
                "Provider": 6111,
                "CREATED_ERROR_CODE": 122,
                "STATUS": "Open",
                "TICKET_CREATION_DATE": "3/14/2019 23:45",
                "LAST_MODIFIED_DATE": "3/17/2019 21:34", 
                "PRODUCT_NAME": "BLUE"
                }
                ]
                }
                """;
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://localhost:8080/bla")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response)
                );

        List<SupportCase> cases = fetchClient.fetchForCrm(crm1);

        SupportCase actual = cases.get(0);
        SupportCase expected = new SupportCase();
        expected.setCrm("test1");
        expected.setCaseId("1");
        expected.setCustomerId("818591");
        expected.setErrorCode("324");
        expected.setProvider("6111");
        expected.setStatus("Open");
        expected.setProductName("BLUE");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy HH:mm");
        expected.setCreationTime(LocalDateTime.parse("3/14/2019 23:45", formatter));
        expected.setLastModified(LocalDateTime.parse("3/17/2019 21:34", formatter));
        Assert.assertEquals(expected, actual);


    }

}
