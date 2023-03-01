package com.intuit.support.hub.demo;

import com.intuit.support.hub.fetch.client.entities.SupportCase;
import com.intuit.support.hub.fetch.internal.GetCasesResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
/**
 * REST controller that 'merges' all external client apis to be used by clients of the service
 */
public class DemoController {

    @GetMapping("/banana")
    public GetCasesResponse banana() {
        SupportCase caseA = createCase("1", "Product A", "1", "banana","Open", "1");
        SupportCase caseC = createCase("1", "Product B", "2", "banana", "Open", "1");
        SupportCase caseD = createCase("2", "Product A", "3", "banana","Open","3");
        SupportCase caseE = createCase("2", "Product A", "4", "banana","Close", "1");
        return new GetCasesResponse(Arrays.asList(caseA, caseC,caseD,caseE));
    }

    @GetMapping("/strawberry")
    public GetCasesResponse strawberry() {
        return new GetCasesResponse(Arrays.asList(createCase("1", "Product A", "1", "strawberry","Open", "2")));
    }

    @PutMapping("/failure")
    public GetCasesResponse failure() {
        throw new RuntimeException("failure");
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
