package com.intuit.support.hub.fetch.internal;

import com.intuit.support.hub.fetch.client.entities.SupportCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetCasesResponse {
    private List<SupportCase> data;
}
