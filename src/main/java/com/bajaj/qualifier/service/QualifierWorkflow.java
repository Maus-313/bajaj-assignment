package com.bajaj.qualifier.service;

import com.bajaj.qualifier.dto.FinalQueryRequest;
import com.bajaj.qualifier.dto.GenerateWebhookRequest;
import com.bajaj.qualifier.dto.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QualifierWorkflow implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(QualifierWorkflow.class);
    private static final String WEBHOOK_GENERATOR_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    private final RestTemplate restTemplate;
    private final SqlQueryProvider sqlQueryProvider;
    private final SolutionStorageService solutionStorageService;

    public QualifierWorkflow(RestTemplate restTemplate,
                             SqlQueryProvider sqlQueryProvider,
                             SolutionStorageService solutionStorageService) {
        this.restTemplate = restTemplate;
        this.sqlQueryProvider = sqlQueryProvider;
        this.solutionStorageService = solutionStorageService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting qualifier workflow");

        String finalQuery = sqlQueryProvider.provide();
        solutionStorageService.persist(finalQuery);

        GenerateWebhookResponse webhookResponse = requestWebhook();
        if (webhookResponse == null || isBlank(webhookResponse.webhook()) || isBlank(webhookResponse.accessToken())) {
            log.error("Invalid webhook response received, terminating workflow");
            return;
        }
        log.info("Received webhook: {} and accessToken: {}", webhookResponse.webhook(), maskToken(webhookResponse.accessToken()));

        // submitSolution(webhookResponse, finalQuery);
        log.info("Solution prepared but submission skipped for qualifier purposes");
    }

    private GenerateWebhookResponse requestWebhook() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            GenerateWebhookRequest payload = new GenerateWebhookRequest(
                    "Mohd Umar Khan",
                    "22BCE07693",
                    "umar8931008277@gmail.com"
            );
            ResponseEntity<GenerateWebhookResponse> response = restTemplate.postForEntity(
                    WEBHOOK_GENERATOR_URL,
                    new HttpEntity<>(payload, headers),
                    GenerateWebhookResponse.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Webhook generated successfully");
                return response.getBody();
            }
            log.error("Webhook generation failed with status: {}", response.getStatusCode());
        } catch (Exception ex) {
            log.error("Exception during webhook generation", ex);
        }
        return null;
    }

    private void submitSolution(GenerateWebhookResponse response, String finalQuery) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Token " + response.accessToken());
            FinalQueryRequest payload = new FinalQueryRequest(finalQuery);
            ResponseEntity<String> submissionResponse = restTemplate.postForEntity(
                    response.webhook(),
                    new HttpEntity<>(payload, headers),
                    String.class
            );
            if (submissionResponse.getStatusCode().is2xxSuccessful()) {
                log.info("Final SQL query submitted successfully");
            } else {
                log.error("Submission failed with status: {}", submissionResponse.getStatusCode());
            }
        } catch (Exception ex) {
            log.error("Exception during solution submission", ex);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return token;
        }
        return token.substring(0, 5) + "..." + token.substring(token.length() - 5);
    }
}
