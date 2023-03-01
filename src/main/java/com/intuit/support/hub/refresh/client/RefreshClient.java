package com.intuit.support.hub.refresh.client;

import com.intuit.support.hub.refresh.client.responses.RefreshRequestResponse;
import com.intuit.support.hub.refresh.client.responses.RefreshResult;
import com.intuit.support.hub.refresh.internal.RefreshManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Client that handles triggered refresh requests from the user
 */
@Service
public class RefreshClient {
    @Autowired
    RefreshManager manager;

    public RefreshRequestResponse userRefresh() {
        return manager.userRefresh();
    }

    public RefreshResult getLastRefreshResult() {
        return manager.getLastRefreshResult();
    }

}
