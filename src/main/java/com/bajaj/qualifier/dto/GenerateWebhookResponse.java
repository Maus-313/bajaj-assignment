package com.bajaj.qualifier.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GenerateWebhookResponse(
        @JsonProperty("webhook") String webhook,
        @JsonProperty("accessToken") String accessToken
) {
}
