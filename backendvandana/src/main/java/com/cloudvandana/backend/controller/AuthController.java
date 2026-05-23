package com.cloudvandana.backend.controller;

import com.cloudvandana.backend.dto.ValidationRuleResponse;
import com.cloudvandana.backend.service.SalesforceService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@RestController
@CrossOrigin(origins = "https://salesforce-frontend-41ny.onrender.com")
public class AuthController {

    private final SalesforceService salesforceService;

    public AuthController(SalesforceService salesforceService) {
        this.salesforceService = salesforceService;
    }

    @Value("${salesforce.client.id}")
    private String clientId;

    @Value("${salesforce.redirect.uri}")
    private String redirectUri;

    @Value("${salesforce.auth.url}")
    private String authUrl;

    

    @GetMapping("/api/login")
public void login(HttpServletResponse response, HttpSession session) throws Exception {

    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);

    String verifier = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);

    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] digest = md.digest(verifier.getBytes());

    String challenge = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(digest);

    session.setAttribute("pkce_verifier", verifier);

    String state = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(("state-" + System.currentTimeMillis()).getBytes());

    String url = authUrl
            + "?response_type=code"
            + "&client_id=" + clientId
            + "&redirect_uri=" + redirectUri
            + "&state=" + state
            + "&code_challenge=" + challenge
            + "&code_challenge_method=S256";

    response.sendRedirect(url);
}

    // ---------------- CALLBACK ----------------
    @GetMapping("/api/callback")
    public void callback(@RequestParam("code") String code,
                         HttpServletResponse response,
                         HttpSession session) throws Exception {

        String verifier = (String) session.getAttribute("pkce_verifier");

        if (verifier == null) {
            throw new RuntimeException("Missing PKCE verifier in session");
        }

        salesforceService.getAccessToken(code, verifier);

        response.sendRedirect("https://salesforce-frontend-41ny.onrender.com");
    }

    // ---------------- VALIDATION RULES ----------------
    

    public ValidationRuleResponse getValidationRules() {

    if (instanceUrl == null || accessToken == null) {
        throw new RuntimeException("User not logged in");
    }

    try {
        String url = instanceUrl +
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

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Salesforce API failed: " + e.getMessage());
    }
}

    // ---------------- TOGGLE RULE ----------------
    @GetMapping("/api/toggle-rule")
    public String toggle(@RequestParam String id,
                         @RequestParam Boolean active) {

        return salesforceService.toggleValidationRule(id, active);
    }
}