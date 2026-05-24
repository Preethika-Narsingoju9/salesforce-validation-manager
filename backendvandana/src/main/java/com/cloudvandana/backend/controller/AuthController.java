
package com.cloudvandana.backend.controller;

import com.cloudvandana.backend.service.SalesforceService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@RestController
@CrossOrigin(
        origins = "https://salesforce-frontend-41ny.onrender.com",
        allowCredentials = "true"
)
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

    // ---------------- LOGIN ----------------

    @GetMapping("/api/login")
    public void login(HttpServletResponse response) throws Exception {

        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        String cookie =
                "code_verifier=" + codeVerifier +
                "; Path=/" +
                "; HttpOnly" +
                "; Secure" +
                "; SameSite=None";

        response.addHeader("Set-Cookie", cookie);

        String loginUrl =
                authUrl +
                        "?response_type=code" +
                        "&client_id=" + clientId +
                        "&redirect_uri=" +
                        URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                        "&code_challenge=" + codeChallenge +
                        "&code_challenge_method=S256";

        System.out.println("LOGIN API HIT");
        System.out.println(loginUrl);

        response.sendRedirect(loginUrl);
    }

    // ---------------- CALLBACK ----------------

    @GetMapping("/api/callback")
    public void callback(
            @RequestParam("code") String code,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String codeVerifier = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("code_verifier".equals(c.getName())) {
                    codeVerifier = c.getValue();
                }
            }
        }

        if (codeVerifier == null) {

            response.sendRedirect(
                    "https://salesforce-frontend-41ny.onrender.com?error=session_lost"
            );

            return;
        }

        System.out.println("CALLBACK HIT");
        System.out.println(code);

        salesforceService.getAccessToken(code, codeVerifier);

        response.sendRedirect(
                "https://salesforce-frontend-41ny.onrender.com"
        );
    }

    // ---------------- RULES ----------------

    @GetMapping("/api/validation-rules")
    public ResponseEntity<?> getRules() {

        return ResponseEntity.ok(
                salesforceService.getValidationRules()
        );
    }

    // ---------------- TOGGLE ----------------

    @GetMapping("/api/toggle-rule")
    public ResponseEntity<?> toggle(
            @RequestParam String id,
            @RequestParam Boolean active) {

        return ResponseEntity.ok(
                salesforceService.toggleValidationRule(id, active)
        );
    }

    // ---------------- PKCE HELPERS ----------------

    private String generateCodeVerifier() {

        byte[] bytes = new byte[32];

        new SecureRandom().nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String generateCodeChallenge(String codeVerifier)
            throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        byte[] hash =
                digest.digest(
                        codeVerifier.getBytes(StandardCharsets.UTF_8)
                );

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(hash);
    }
}