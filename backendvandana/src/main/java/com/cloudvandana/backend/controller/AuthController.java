

// package com.cloudvandana.backend.controller;

// import com.cloudvandana.backend.service.SalesforceService;

// import jakarta.servlet.http.Cookie;
// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.servlet.http.HttpSession;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.io.IOException;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.security.MessageDigest;
// import java.security.SecureRandom;
// import java.util.Base64;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// @RestController
// @CrossOrigin(
//         origins = "https://salesforce-frontend-41ny.onrender.com",
//         allowCredentials = "true"
// )
// public class AuthController {

//     private final SalesforceService salesforceService;

//     public AuthController(
//             SalesforceService salesforceService) {

//         this.salesforceService = salesforceService;
//     }

//     @Value("${salesforce.client.id}")
//     private String clientId;

//     @Value("${salesforce.redirect.uri}")
//     private String redirectUri;

//     @Value("${salesforce.auth.url}")
//     private String authUrl;

//     // ---------------- PKCE ----------------

//     private String generateCodeVerifier() {

//         byte[] code = new byte[32];

//         new SecureRandom().nextBytes(code);

//         return Base64.getUrlEncoder()
//                 .withoutPadding()
//                 .encodeToString(code);
//     }

//     private String generateCodeChallenge(
//             String codeVerifier)
//             throws Exception {

//         byte[] bytes =
//                 MessageDigest.getInstance("SHA-256")
//                         .digest(
//                                 codeVerifier.getBytes(
//                                         StandardCharsets.UTF_8));

//         return Base64.getUrlEncoder()
//                 .withoutPadding()
//                 .encodeToString(bytes);
//     }

//     // ---------------- LOGIN ----------------

// /

//     private final Map<String, String> pkceStore = new java.util.concurrent.ConcurrentHashMap<>();

// @GetMapping("/api/login")
// public void login(HttpServletResponse response) throws Exception {

//     String state = java.util.UUID.randomUUID().toString();

//     String codeVerifier = generateCodeVerifier();
//     String codeChallenge = generateCodeChallenge(codeVerifier);

//     pkceStore.put(state, codeVerifier);

//     String Url =
//             authUrl
//             + "?response_type=code"
//             + "&client_id=" + clientId
//             + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
//             + "&code_challenge=" + codeChallenge
//             + "&code_challenge_method=S256"
//             + "&state=" + state;
//             System.out.println("FORCED REDIRECT TO:");
//     System.out.println(Url);

//     // 🔥 HARD BROWSER REDIRECT (NO SPRING INTERFERENCE)
//     response.reset();
//     response.setStatus(302);
//     response.setHeader("Location", Url);
//     response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");


//     response.sendRedirect(loginUrl);
// }
    
// @GetMapping("/api/callback")
// public void callback(
//         @RequestParam("code") String code,
//         @RequestParam("state") String state,
//         HttpServletResponse response) throws IOException {

    

//         String codeVerifier = pkceStore.get(state);

//         if (codeVerifier == null) {
//             throw new RuntimeException("PKCE verifier missing (Render stateless issue)");
//         }

        

//         response.sendRedirect(
//                 "https://salesforce-frontend-41ny.onrender.com");

//     } 


    

//     // ---------------- VALIDATION RULES ----------------

//     @GetMapping("/api/validation-rules")
//     public ResponseEntity<?> getRules() {

//         try {

//             return ResponseEntity.ok(
//                     salesforceService
//                             .getValidationRules());

//         } catch (Exception e) {

//             e.printStackTrace();

//             return ResponseEntity
//                     .badRequest()
//                     .body(
//                             "Error fetching validation rules : "
//                                     + e.getMessage());
//         }
//     }

//     // ---------------- TOGGLE RULE ----------------

//     @GetMapping("/api/toggle-rule")
//     public ResponseEntity<?> toggle(
//             @RequestParam String id,
//             @RequestParam Boolean active) {

//         try {

//             return ResponseEntity.ok(
//                     salesforceService
//                             .toggleValidationRule(
//                                     id,
//                                     active));

//         } catch (Exception e) {

//             e.printStackTrace();

//             return ResponseEntity
//                     .badRequest()
//                     .body(
//                             "Error updating validation rule : "
//                                     + e.getMessage());
//         }
//     }
// }

// package com.cloudvandana.backend.controller;

// import com.cloudvandana.backend.service.SalesforceService;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.servlet.http.HttpSession;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.web.servlet.server.Session.Cookie;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.io.IOException;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.util.UUID;

// @RestController
// @CrossOrigin(
//         origins = "https://salesforce-frontend-41ny.onrender.com",
//         allowCredentials = "true"
// )
// public class AuthController {

//     private final SalesforceService salesforceService;

//     public AuthController(SalesforceService salesforceService) {
//         this.salesforceService = salesforceService;
//     }

//     @Value("${salesforce.client.id}")
//     private String clientId;

//     @Value("${salesforce.redirect.uri}")
//     private String redirectUri;

//     @Value("${salesforce.auth.url}")
//     private String authUrl;

//     // ---------------- LOGIN ----------------

//     @GetMapping("/api/login")
// public void login(HttpServletResponse response) throws Exception {

//     String codeVerifier = generateCodeVerifier();
//     String codeChallenge = generateCodeChallenge(codeVerifier);

//     // ❗ Instead of session, store in cookie
//     Cookie cookie = new Cookie("code_verifier", codeVerifier);
//     cookie.setHttpOnly(true);
//     cookie.setPath("/");
//     response.addCookie(cookie);

//     String loginUrl =
//             authUrl +
//             "?response_type=code" +
//             "&client_id=" + clientId +
//             "&redirect_uri=" +
//             URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
//             "&code_challenge=" + codeChallenge +
//             "&code_challenge_method=S256";

//     response.sendRedirect(loginUrl);
// }
// //     @GetMapping("/api/login")
// // public void login(HttpServletResponse response, HttpSession session) throws Exception {

// //     String codeVerifier = java.util.UUID.randomUUID().toString().replace("-", "");
// //     String codeChallenge = java.util.Base64.getUrlEncoder().withoutPadding()
// //             .encodeToString(
// //                     java.security.MessageDigest.getInstance("SHA-256")
// //                             .digest(codeVerifier.getBytes(java.nio.charset.StandardCharsets.UTF_8))
// //             );

// //     session.setAttribute("code_verifier", codeVerifier);

// //     String url =
// //             authUrl +
// //             "?response_type=code" +
// //             "&client_id=" + clientId +
// //             "&redirect_uri=" + java.net.URLEncoder.encode(redirectUri, java.nio.charset.StandardCharsets.UTF_8) +
// //             "&code_challenge=" + codeChallenge +
// //             "&code_challenge_method=S256";

// //     response.sendRedirect(url);
// // }
    

//     // ---------------- CALLBACK ----------------

//     @GetMapping("/api/callback")
// public void callback(
//         @RequestParam("code") String code,
//         HttpServletRequest request,
//         HttpServletResponse response) throws IOException {

//     String codeVerifier = null;

//     if (request.getCookies() != null) {
//         for (Cookie c : request.getCookies()) {
//             if (c.getName().equals("code_verifier")) {
//                 codeVerifier = c.getValue();
//             }
//         }
//     }

//     if (codeVerifier == null) {
//         response.sendRedirect("https://salesforce-frontend-41ny.onrender.com?error=session_lost");
//         return;
//     }

//     salesforceService.getAccessToken(code, codeVerifier);

//     response.sendRedirect("https://salesforce-frontend-41ny.onrender.com");
// }
// //     @GetMapping("/api/callback")
// // public void callback(
// //         @RequestParam("code") String code,
// //         HttpSession session,
// //         HttpServletResponse response) throws Exception {

// //     String codeVerifier = (String) session.getAttribute("code_verifier");

// //     salesforceService.getAccessToken(code, codeVerifier);

// //     response.sendRedirect("https://salesforce-frontend-41ny.onrender.com");
// // }
    

//     // ---------------- RULES ----------------

//     @GetMapping("/api/validation-rules")
//     public ResponseEntity<?> getRules() {
//         return ResponseEntity.ok(salesforceService.getValidationRules());
//     }

//     // ---------------- TOGGLE ----------------

//     @GetMapping("/api/toggle-rule")
//     public ResponseEntity<?> toggle(
//             @RequestParam String id,
//             @RequestParam Boolean active) {

//         return ResponseEntity.ok(
//                 salesforceService.toggleValidationRule(id, active)
//         );
//     }
// }


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

        // Secure cookie (required for Render HTTPS)
        Cookie cookie = new Cookie("code_verifier", codeVerifier);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        String loginUrl =
                authUrl +
                        "?response_type=code" +
                        "&client_id=" + clientId +
                        "&redirect_uri=" +
                        URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                        "&code_challenge=" + codeChallenge +
                        "&code_challenge_method=S256";

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

        salesforceService.getAccessToken(code, codeVerifier);

        response.sendRedirect(
                "https://salesforce-frontend-41ny.onrender.com"
        );
    }

    // ---------------- RULES ----------------

    @GetMapping("/api/validation-rules")
    public ResponseEntity<?> getRules() {
        return ResponseEntity.ok(salesforceService.getValidationRules());
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
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String generateCodeChallenge(String codeVerifier) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}