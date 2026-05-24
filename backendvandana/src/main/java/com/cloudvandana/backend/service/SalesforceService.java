




// package com.cloudvandana.backend.service;

// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.*;
// import org.springframework.stereotype.Service;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.client.RestTemplate;

// import com.cloudvandana.backend.dto.TokenResponse;
// import com.cloudvandana.backend.dto.ValidationRuleResponse;

// import jakarta.servlet.http.HttpServletResponse;

// @Service
// public class SalesforceService {

//     private final Map<String, String> cache =
//             new ConcurrentHashMap<>();

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

//     // ---------------- LOGIN ----------------



//     public TokenResponse getAccessToken(
//             String code,
//             String codeVerifier) {

//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(
//                 MediaType.APPLICATION_FORM_URLENCODED);

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
//                         TokenResponse.class);

//         cache.put(
//                 "access_token",
//                 response.getBody().getAccess_token()
//         );

//         cache.put(
//                 "instance_url",
//                 response.getBody().getInstance_url()
//         );

//         return response.getBody();
//     }

//     // ---------------- GET RULES ----------------

//     public ValidationRuleResponse getValidationRules() {

//         String accessToken = cache.get("access_token");
//         String instanceUrl = cache.get("instance_url");

//         if (accessToken == null || instanceUrl == null) {
//             throw new RuntimeException("User not logged in");
//         }

//         String url =
//                 instanceUrl +
//                 "/services/data/v62.0/tooling/query/?q=" +
//                 "SELECT+Id,Name,Active+" +
//                 "FROM+ValidationRule+LIMIT+50";

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

//     // ---------------- TOGGLE ----------------

//     public String toggleValidationRule(
//             String id,
//             Boolean active) {

//         String accessToken = cache.get("access_token");
//         String instanceUrl = cache.get("instance_url");

//         if (accessToken == null || instanceUrl == null) {
//             throw new RuntimeException("User not logged in");
//         }

//         String getUrl =
//                 instanceUrl +
//                 "/services/data/v62.0/tooling/sobjects/ValidationRule/" +
//                 id;

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

//         String errorConditionFormula =
//                 responseBody
//                         .split("\"errorConditionFormula\":\"")[1]
//                         .split("\"")[0];

//         String errorMessage =
//                 responseBody
//                         .split("\"errorMessage\":\"")[1]
//                         .split("\"")[0];

//         String patchUrl =
//                 instanceUrl +
//                 "/services/data/v62.0/tooling/sobjects/ValidationRule/" +
//                 id;

//         headers.setContentType(
//                 MediaType.APPLICATION_JSON);

//         String body =
//                 "{"
//                         + "\"Metadata\":{"
//                         + "\"active\":" + active + ","
//                         + "\"errorConditionFormula\":\""
//                         + errorConditionFormula + "\","
//                         + "\"errorMessage\":\""
//                         + errorMessage + "\""
//                         + "}"
//                         + "}";

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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SalesforceService {

    // private final RestTemplate restTemplate = new RestTemplate();
    private final RestTemplate restTemplate;

public SalesforceService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
}

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    @Value("${salesforce.client.id}")
    private String clientId;

    @Value("${salesforce.client.secret}")
    private String clientSecret;

    @Value("${salesforce.redirect.uri}")
    private String redirectUri;

    @Value("${salesforce.token.url}")
    private String tokenUrl;

    // ---------------- LOGIN TOKEN ----------------

    public void getAccessToken(String code, String codeVerifier) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

    body.add("grant_type", "authorization_code");
    body.add("client_id", clientId);
    body.add("client_secret", clientSecret);
    body.add("redirect_uri", redirectUri);
    body.add("code", code);
    body.add("code_verifier", codeVerifier);

    HttpEntity<?> request = new HttpEntity<>(body, headers);

    ResponseEntity<Map> response =
            restTemplate.postForEntity(tokenUrl, request, Map.class);

    cache.put("access_token", response.getBody().get("access_token").toString());
    cache.put("instance_url", response.getBody().get("instance_url").toString());
}
//     public void getAccessToken(String code) {

//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//         MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

//         body.add("grant_type", "authorization_code");
//         body.add("client_id", clientId);
//         body.add("client_secret", clientSecret);
//         body.add("redirect_uri", redirectUri);
//         body.add("code", code);

//         HttpEntity<MultiValueMap<String, String>> request =
//                 new HttpEntity<>(body, headers);

//         ResponseEntity<Map> response =
//                 restTemplate.postForEntity(tokenUrl, request, Map.class);

//         cache.put("access_token", response.getBody().get("access_token").toString());
//         cache.put("instance_url", response.getBody().get("instance_url").toString());
//     }

    // ---------------- RULES ----------------

public Map getValidationRules() {

    String url = cache.get("instance_url")
            + "/services/data/v62.0/tooling/query/?q="
            + "SELECT+Id,ValidationName,Active+FROM+ValidationRule+LIMIT+50";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(cache.get("access_token"));

    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map.class
    );

    Map body = response.getBody();

    // ✅ FIX: rename ValidationName → Name for frontend
    if (body != null && body.get("records") != null) {
        var records = (java.util.List<Map>) body.get("records");

        for (Map r : records) {
            r.put("Name", r.get("ValidationName"));
        }
    }

    return body;
}
// public Map getValidationRules() {

//     String url = cache.get("instance_url")
//             + "/services/data/v62.0/tooling/query/?q="
//             + "SELECT+Id,ValidationName,Active+FROM+ValidationRule+LIMIT+50";

//     HttpHeaders headers = new HttpHeaders();
//     headers.setBearerAuth(cache.get("access_token"));

//     HttpEntity<String> entity = new HttpEntity<>(headers);

//     ResponseEntity<Map> response = restTemplate.exchange(
//             url,
//             HttpMethod.GET,
//             entity,
//             Map.class
//     );

//     return response.getBody();
// }

    // ---------------- TOGGLE ----------------

    public String toggleValidationRule(String id, Boolean active) {

        return "Toggle request received for " + id + " -> " + active;
    }
}