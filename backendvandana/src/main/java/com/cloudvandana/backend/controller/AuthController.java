package com.cloudvandana.backend.controller;

import java.io.IOException;
import com.cloudvandana.backend.dto.ValidationRuleResponse;
import com.cloudvandana.backend.service.SalesforceService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
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
// @GetMapping("/api/login")
// public void login(HttpServletResponse response) throws IOException {

//     String clientId = "3MVG97L7PWbPq6UwCL.6YvIjV90HG23keKInIpqpKBwC0bwHPdUdg8OJmqYkTHDhnnS4OUmE5QdfydRcRoTaQ";
//     String redirectUri = "https://salesforce-validation-manager-snah.onrender.com/api/callback";

//     String url =
//         "https://login.salesforce.com/services/oauth2/authorize" +
//         "?response_type=code" +
//         "&client_id=" + clientId +
//         "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

//     response.sendRedirect(url);
}
//     @GetMapping("/api/login")
// public void login(HttpServletResponse response, HttpSession session) throws Exception {

//     SecureRandom random = new SecureRandom();
//     byte[] bytes = new byte[32];
//     random.nextBytes(bytes);

//     String verifier = Base64.getUrlEncoder()
//             .withoutPadding()
//             .encodeToString(bytes);

//     MessageDigest md = MessageDigest.getInstance("SHA-256");
//     byte[] digest = md.digest(verifier.getBytes());

//     String challenge = Base64.getUrlEncoder()
//             .withoutPadding()
//             .encodeToString(digest);

//     session.setAttribute("pkce_verifier", verifier);

//     String state = Base64.getUrlEncoder()
//             .withoutPadding()
//             .encodeToString(("state-" + System.currentTimeMillis()).getBytes());

//     String url = authUrl
//         + "?response_type=code"
//         + "&client_id=" + clientId
//         + "&redirect_uri=" + redirectUri
//         + "&state=" + state
//         + "&code_challenge=" + challenge
//         + "&code_challenge_method=S256";

// System.out.println("FINAL AUTH URL = " + url);
// System.out.println("REDIRECT URI RAW = [" + redirectUri + "]");



        
//     response.sendRedirect(url);
// }

    // ---------------- CALLBACK ----------------
    // @GetMapping("/api/callback")
    // public void callback(@RequestParam("code") String code,
    //                      HttpServletResponse response,
    //                      HttpSession session) throws Exception {

    //     String verifier = (String) session.getAttribute("pkce_verifier");

    //     if (verifier == null) {
    //         throw new RuntimeException("Missing PKCE verifier in session");
    //     }

    //     salesforceService.getAccessToken(code, verifier);

    //     response.sendRedirect("https://salesforce-frontend-41ny.onrender.com");
    // }


//     @GetMapping("/api/callback")

// @GetMapping("/api/callback")
// public ResponseEntity<String> callback(@RequestParam("code") String code) {

//     // exchange code for access token
//     String tokenUrl = "https://login.salesforce.com/services/oauth2/token";

//     // use RestTemplate to POST:
//     // grant_type=authorization_code
//     // code
//     // client_id
//     // client_secret
//     // redirect_uri

//     return ResponseEntity.ok("Login Successful");
// }

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
// public void callback(@RequestParam(value = "code", required = false) String code,
//                      HttpServletResponse response,
//                      HttpSession session) throws Exception {

//     if (code == null) {
//         throw new RuntimeException("OAuth failed: code is missing");
//     }

//     String verifier = (String) session.getAttribute("pkce_verifier");

//     if (verifier == null) {
//         throw new RuntimeException("Missing PKCE verifier");
//     }

//     salesforceService.getAccessToken(code, verifier);

//     response.sendRedirect("https://salesforce-frontend-41ny.onrender.com");
// }

    // ---------------- VALIDATION RULES ----------------
    
// @GetMapping("/api/validation-rules")
// public ValidationRuleResponse getValidationRules() {
//     return salesforceService.getValidationRules();
// }

@GetMapping("/api/validation-rules")
public ResponseEntity<?> getRules() {
    return ResponseEntity.ok("working");
}

//     public ValidationRuleResponse getValidationRules() {

//     if (instanceUrl == null || accessToken == null) {
//         throw new RuntimeException("User not logged in");
//     }

//     try {
//         String url = instanceUrl +
//                 "/services/data/v62.0/tooling/query/?q=" +
//                 "SELECT+Id,Name,Active+FROM+ValidationRule+LIMIT+50";

//         HttpHeaders headers = new HttpHeaders();
//         headers.setBearerAuth(accessToken);

//         HttpEntity<String> entity = new HttpEntity<>(headers);

//         ResponseEntity<ValidationRuleResponse> response =
//                 restTemplate.exchange(
//                         url,
//                         HttpMethod.GET,
//                         entity,
//                         ValidationRuleResponse.class
//                 );

//         return response.getBody();

//     } catch (Exception e) {
//         e.printStackTrace();
//         throw new RuntimeException("Salesforce API failed: " + e.getMessage());
//     }
// }

    // ---------------- TOGGLE RULE ----------------
    @GetMapping("/api/toggle-rule")
    public String toggle(@RequestParam String id,
                         @RequestParam Boolean active) {

        return salesforceService.toggleValidationRule(id, active);
    }
}