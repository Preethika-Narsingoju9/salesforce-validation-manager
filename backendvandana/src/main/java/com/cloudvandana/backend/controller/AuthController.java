


package com.cloudvandana.backend.controller;

import com.cloudvandana.backend.dto.ValidationRuleResponse;
import com.cloudvandana.backend.service.SalesforceService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;



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

    // LOGIN → Salesforce OAuth page
    // @GetMapping("/login")
    // public void login(HttpServletResponse response) throws Exception {

    //     SecureRandom random = new SecureRandom();
    //     byte[] bytes = new byte[32];
    //     random.nextBytes(bytes);

    //     String verifier = Base64.getUrlEncoder()
    //             .withoutPadding()
    //             .encodeToString(bytes);

    //     // store per session (VERY IMPORTANT FIX)
        

    //     MessageDigest md = MessageDigest.getInstance("SHA-256");
    //     byte[] digest = md.digest(verifier.getBytes());

    //     String challenge = Base64.getUrlEncoder()
    //             .withoutPadding()
    //             .encodeToString(digest);

    //     String url = authUrl
    //             + "?response_type=code"
    //             + "&client_id=" + clientId
    //             + "&redirect_uri=" + redirectUri
    //             + "&code_challenge=" + challenge
    //             + "&code_challenge_method=S256";
    //             + "&state=" + verifier;

    //     response.sendRedirect(url);
    // }
//     @GetMapping("/login")
// public void login(HttpServletResponse response) throws Exception {

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

//     String url = authUrl
//             + "?response_type=code"
//             + "&client_id=" + clientId
//             + "&redirect_uri=" + redirectUri
//             + "&code_challenge=" + challenge
//             + "&code_challenge_method=S256"
//             + "&state=" + verifier;

//     response.sendRedirect(url);
// }
@GetMapping("/login")
public void login(HttpServletResponse response) throws Exception {

    String url = authUrl
            + "?response_type=code"
            + "&client_id=" + clientId
            + "&redirect_uri=" + redirectUri
            + "&state=test";

    response.sendRedirect(url);
}
    
    
    // CALLBACK → exchange token → return to React
    // @GetMapping("/callback")
    // public void callback(@RequestParam("code") String code,
    //                      HttpSession session,
    //                      HttpServletResponse response) throws Exception {

    //     String verifier = (String) session.getAttribute("verifier");

    //     if (verifier == null) {
    //         throw new RuntimeException("Code verifier missing. Login again.");
    //     }

    //     salesforceService.getAccessToken(code, verifier);

    //     response.sendRedirect("https://salesforce-frontend-41ny.onrender.com");
    // }
//     @GetMapping("/callback")
// public void callback(@RequestParam("code") String code,
//                      @RequestParam(value = "state", required = false) String verifier,
//                      HttpServletResponse response) throws Exception {

//     if (verifier == null) {
//         throw new RuntimeException("Verifier missing");
//     }

//     salesforceService.getAccessToken(code, verifier);

//     response.sendRedirect("https://salesforce-frontend-41ny.onrender.com");
// }
@GetMapping("/callback")
public void callback(@RequestParam("code") String code,
                     HttpServletResponse response) throws Exception {

    salesforceService.getAccessToken(code, "test");

    response.sendRedirect("https://salesforce-frontend-41ny.onrender.com");
}
    
    

    @GetMapping("/validation-rules")
    public ValidationRuleResponse getRules() {
        return salesforceService.getValidationRules();
    }

    @GetMapping("/toggle-rule")
    public String toggle(@RequestParam String id,
                         @RequestParam Boolean active) {
        return salesforceService.toggleValidationRule(id, active);
    }
}
