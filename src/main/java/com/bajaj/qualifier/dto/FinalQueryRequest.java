package com.bajaj.qualifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FinalQueryRequest(@JsonProperty("finalQuery") String finalQuery) {
}
