package com.intuit.support.hub.fetch.client.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@IdClass(SystemCaseId.class)
public class SupportCase {
    @JsonProperty("Case ID")
    @Id
    String caseId;

    @Id
    String crm;

    @JsonProperty("Customer_ID")
    String customerId;

    @JsonProperty("Provider")
    String provider;

    @JsonProperty("CREATED_ERROR_CODE")
    String errorCode;

    @JsonProperty("STATUS")
    String status;

    @JsonProperty("LAST_MODIFIED_DATE")
    @JsonFormat(pattern = "M/dd/yyyy HH:mm")
    LocalDateTime lastModified;

    @JsonProperty("TICKET_CREATION_DATE")
    @JsonFormat(pattern = "M/dd/yyyy HH:mm")
    LocalDateTime creationTime;

    @JsonProperty("PRODUCT_NAME")
    String productName;
}
