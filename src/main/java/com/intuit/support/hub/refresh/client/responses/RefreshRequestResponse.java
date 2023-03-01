package com.intuit.support.hub.refresh.client.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequestResponse {
    long durationToWait;
    String furtherInfo;
    RefreshResult refreshResult;
}
