package com.cloudvandana.backend.controller;

import java.io.IOException;
import com.cloudvandana.backend.dto.ValidationRuleResponse;
import com.cloudvandana.backend.service.SalesforceService;

import java.net.URLEncoder;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;


@RestController
@CrossOrigin(origins = "https://salesforce-frontend-41ny.onrender.com")
public class AuthController {


    private String generateCodeVerifier() {
    byte[] code = new byte[32];
    new SecureRandom().nextBytes(code);

    return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(code);
}

private String generateCodeChallenge(String codeVerifier) throws Exception {

    byte[] bytes = MessageDigest.getInstance("SHA-256")
            .digest(codeVerifier.getBytes(StandardCharsets.UTF_8));

    return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
}

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

 //-------------------LOGIN------------------------
 
    /**
     * @param response
     * @param session
     * @throws Exception
     */
    @GetMapping("/login")
public void login(HttpServletResponse response,
                  HttpSession session) throws Exception {

    String codeVerifier = generateCodeVerifier();

    String codeChallenge = generateCodeChallenge(codeVerifier);

    // Store verifier in session
    session.setAttribute("code_verifier", codeVerifier);

    String authUrl =
            salesforceAuthUrl +
            "?response_type=code" +
            "&client_id=" + clientId +
            "&redirect_uri=" +
            URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
            "&code_challenge=" + codeChallenge +
            "&code_challenge_method=S256";

    response.sendRedirect(authUrl);
}


//--------------CALL BACK---------------------------

@GetMapping("/callback")
public String callback(@RequestParam("code") String code,
                       HttpSession session) {

    String codeVerifier =
            (String) session.getAttribute("code_verifier");

    MultiValueMap<String, String> params =
            new LinkedMultiValueMap<>();

    params.add("grant_type", "authorization_code");
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("redirect_uri", redirectUri);
    params.add("code", code);

    // IMPORTANT
    params.add("code_verifier", codeVerifier);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> request =
            new HttpEntity<>(params, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(
            tokenUrl,
            request,
            String.class
    );

    return "Login Successful";
}

//--------------Validation Rule----------

@GetMapping("/api/validation-rules")
public ResponseEntity<?> getRules() {
    return ResponseEntity.ok("working");
}



    // ---------------- TOGGLE RULE ----------------
    @GetMapping("/api/toggle-rule")
    public String toggle(@RequestParam String id,
                         @RequestParam Boolean active) {

        return salesforceService.toggleValidationRule(id, active);
    }
}