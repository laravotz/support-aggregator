package com.intuit.support.hub.fetch.client.responses;

import com.intuit.support.hub.fetch.client.entities.SupportCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RefreshCRMResult {
    private String crmName;
    private String error;
    private List<SupportCase> cases;
}
