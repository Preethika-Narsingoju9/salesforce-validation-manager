// package com.cloudvandana.backend.service;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.*;
// import org.springframework.stereotype.Service;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.client.RestTemplate;

// import com.cloudvandana.backend.dto.TokenResponse;
// import com.cloudvandana.backend.dto.ValidationRuleResponse;

// @Service
// public class SalesforceService {

//     private String accessToken;
//     private String instanceUrl;

//     @Value("${salesforce.client.id}")
//     private String clientId;

//     @Value("${salesforce.client.secret}")
//     private String clientSecret;

//     @Value("${salesforce.redirect.uri}")
//     private String redirectUri;

//     @Value("${salesforce.token.url}")
//     private String tokenUrl;

//     private final RestTemplate restTemplate;

//     public SalesforceService(RestTemplate restTemplate) {
//         this.restTemplate = restTemplate;
//     }

//     // LOGIN AND GET ACCESS TOKEN
//     public TokenResponse getAccessToken(String code, String codeVerifier) {

//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//         MultiValueMap<String, String> body =
//                 new LinkedMultiValueMap<>();

//         body.add("grant_type", "authorization_code");
//         body.add("client_id", clientId);
//         body.add("client_secret", clientSecret);
//         body.add("redirect_uri", redirectUri);
//         body.add("code", code);
//         body.add("code_verifier", codeVerifier);

//         HttpEntity<MultiValueMap<String, String>> request =
//                 new HttpEntity<>(body, headers);

//         ResponseEntity<TokenResponse> response =
//                 restTemplate.postForEntity(
//                         tokenUrl,
//                         request,
//                         TokenResponse.class
//                 );

//         // STORE TOKEN VALUES
//         this.accessToken = response.getBody().getAccess_token();
//         this.instanceUrl = response.getBody().getInstance_url();

//         System.out.println("ACCESS TOKEN SAVED");
//         System.out.println("INSTANCE URL: " + instanceUrl);

//         return response.getBody();
//     }

//     // GET VALIDATION RULES
//     public ValidationRuleResponse getValidationRules() {

//         if (instanceUrl == null || accessToken == null) {
//         //     throw new RuntimeException("Please login to Salesforce first.");
//         return null;
//         }


//         String url = instanceUrl
//                 + "/services/data/v62.0/tooling/query/?q="
//                 + "SELECT+Id,FullName,Active+FROM+ValidationRule";

//         HttpHeaders headers = new HttpHeaders();
//         headers.setBearerAuth(accessToken);

//         HttpEntity<String> entity =
//                 new HttpEntity<>(headers);

//         ResponseEntity<ValidationRuleResponse> response =
//                 restTemplate.exchange(
//                         url,
//                         HttpMethod.GET,
//                         entity,
//                         ValidationRuleResponse.class
//                 );

//         return response.getBody();
//     }


//     public String toggleValidationRule(String id, Boolean active) {

//         // STEP 1: GET existing validation rule details
//         String getUrl = instanceUrl
//                 + "/services/data/v62.0/tooling/sobjects/ValidationRule/"
//                 + id;

//         HttpHeaders headers = new HttpHeaders();
//         headers.setBearerAuth(accessToken);

//         HttpEntity<String> getEntity =
//                 new HttpEntity<>(headers);

//         ResponseEntity<String> getResponse =
//                 restTemplate.exchange(
//                         getUrl,
//                         HttpMethod.GET,
//                         getEntity,
//                         String.class
//                 );

//         String responseBody = getResponse.getBody();

//         // Extract required fields manually
//         String errorConditionFormula =
//                 responseBody.split("\"errorConditionFormula\":\"")[1]
//                         .split("\"")[0];

//         String errorMessage =
//                 responseBody.split("\"errorMessage\":\"")[1]
//                         .split("\"")[0];

//         // STEP 2: PATCH with all required fields
//         String patchUrl = instanceUrl
//                 + "/services/data/v62.0/tooling/sobjects/ValidationRule/"
//                 + id;

//         headers.setContentType(MediaType.APPLICATION_JSON);

//         String body =
//                 "{"
//                 + "\"Metadata\":{"
//                 + "\"active\":" + active + ","
//                 + "\"errorConditionFormula\":\""
//                 + errorConditionFormula + "\","
//                 + "\"errorMessage\":\""
//                 + errorMessage + "\""
//                 + "}"
//                 + "}";

//         HttpEntity<String> patchEntity =
//                 new HttpEntity<>(body, headers);

//         restTemplate.exchange(
//                 patchUrl,
//                 HttpMethod.PATCH,
//                 patchEntity,
//                 String.class
//         );

//         return "Validation Rule Updated Successfully";
//     }
// }


package com.cloudvandana.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.cloudvandana.backend.dto.TokenResponse;
import com.cloudvandana.backend.dto.ValidationRuleResponse;
import com.cloudvandana.backend.dto.ValidationRule;

import java.util.Arrays;

@Service
public class SalesforceService {

    private String accessToken;
    private String instanceUrl;

    @Value("${salesforce.client.id}")
    private String clientId;

    @Value("${salesforce.client.secret}")
    private String clientSecret;

    @Value("${salesforce.redirect.uri}")
    private String redirectUri;

    @Value("${salesforce.token.url}")
    private String tokenUrl;

    private final RestTemplate restTemplate;

    public SalesforceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // LOGIN AND GET ACCESS TOKEN
    public TokenResponse getAccessToken(String code, String codeVerifier) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("code_verifier", codeVerifier);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<TokenResponse> response =
                restTemplate.postForEntity(tokenUrl, request, TokenResponse.class);

        this.accessToken = response.getBody().getAccess_token();
        this.instanceUrl = response.getBody().getInstance_url();

        System.out.println("ACCESS TOKEN SAVED");
        System.out.println("INSTANCE URL: " + instanceUrl);

        return response.getBody();
    }

    // GET VALIDATION RULES
    public ValidationRuleResponse getValidationRules() {

        if (instanceUrl == null || accessToken == null) {
            throw new RuntimeException("User not logged in");
        }

        String url =
                instanceUrl +
                "/services/data/v62.0/tooling/query/?q=" +
                "SELECT+Id,Name,Active+FROM+ValidationRule+LIMIT+50";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ValidationRuleResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        ValidationRuleResponse.class
                );

        return response.getBody();
    }

    // TOGGLE RULE
    public String toggleValidationRule(String id, Boolean active) {

        String getUrl =
                instanceUrl +
                "/services/data/v62.0/tooling/sobjects/ValidationRule/" +
                id;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> getEntity = new HttpEntity<>(headers);

        ResponseEntity<String> getResponse =
                restTemplate.exchange(
                        getUrl,
                        HttpMethod.GET,
                        getEntity,
                        String.class
                );

        String responseBody = getResponse.getBody();

        String errorConditionFormula =
                responseBody.split("\"errorConditionFormula\":\"")[1].split("\"")[0];

        String errorMessage =
                responseBody.split("\"errorMessage\":\"")[1].split("\"")[0];

        String patchUrl =
                instanceUrl +
                "/services/data/v62.0/tooling/sobjects/ValidationRule/" +
                id;

        headers.setContentType(MediaType.APPLICATION_JSON);

        String body =
                "{"
                + "\"Metadata\":{"
                + "\"active\":" + active + ","
                + "\"errorConditionFormula\":\"" + errorConditionFormula + "\","
                + "\"errorMessage\":\"" + errorMessage + "\""
                + "}"
                + "}";

        HttpEntity<String> patchEntity =
                new HttpEntity<>(body, headers);

        restTemplate.exchange(
                patchUrl,
                HttpMethod.PATCH,
                patchEntity,
                String.class
        );

        return "Validation Rule Updated Successfully";
    }
}