// package com.cloudvandana.backend.controller;

// import com.cloudvandana.backend.service.SalesforceService;

// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.servlet.http.HttpSession;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.client.RestTemplate;

// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.security.MessageDigest;
// import java.security.SecureRandom;
// import java.util.Base64;
// import java.util.Map;

// @RestController
// @CrossOrigin(origins = "https://salesforce-frontend-41ny.onrender.com")
// public class AuthController {

//     private final SalesforceService salesforceService;

//     public AuthController(SalesforceService salesforceService) {
//         this.salesforceService = salesforceService;
//     }

//     @Value("${salesforce.client.id}")
//     private String clientId;

//     @Value("${salesforce.client.secret}")
//     private String clientSecret;

//     @Value("${salesforce.redirect.uri}")
//     private String redirectUri;

//     @Value("${salesforce.auth.url}")
//     private String authUrl;

//     @Value("${salesforce.token.url}")
//     private String tokenUrl;

//     private final RestTemplate restTemplate = new RestTemplate();

//     // ---------------- PKCE METHODS ----------------

//     private String generateCodeVerifier() {

//         byte[] code = new byte[32];

//         new SecureRandom().nextBytes(code);

//         return Base64.getUrlEncoder()
//                 .withoutPadding()
//                 .encodeToString(code);
//     }

//     private String generateCodeChallenge(String codeVerifier)
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

//     @GetMapping("/api/login")
//     public void login(HttpServletResponse response,
//                       HttpSession session) throws Exception {

//         String codeVerifier = generateCodeVerifier();

//         String codeChallenge =
//                 generateCodeChallenge(codeVerifier);

//         session.setAttribute("code_verifier", codeVerifier);

//         String loginUrl =
//                 authUrl +
//                 "?response_type=code" +
//                 "&client_id=" + clientId +
//                 "&redirect_uri=" +
//                 URLEncoder.encode(
//                         redirectUri,
//                         StandardCharsets.UTF_8) +
//                 "&code_challenge=" + codeChallenge +
//                 "&code_challenge_method=S256";

//         response.sendRedirect(loginUrl);
//     }

//     // ---------------- CALLBACK ----------------

//     @GetMapping("/api/callback")
//     public ResponseEntity<?> callback(
//             @RequestParam("code") String code,
//             HttpSession session) {

//         try {

//             String codeVerifier =
//                     (String) session.getAttribute(
//                             "code_verifier");

//             MultiValueMap<String, String> params =
//                     new LinkedMultiValueMap<>();

//             params.add("grant_type", "authorization_code");
//             params.add("client_id", clientId);
//             params.add("client_secret", clientSecret);
//             params.add("redirect_uri", redirectUri);
//             params.add("code", code);
//             params.add("code_verifier", codeVerifier);

//             HttpHeaders headers = new HttpHeaders();

//             headers.setContentType(
//                     MediaType.APPLICATION_FORM_URLENCODED);

//             HttpEntity<MultiValueMap<String, String>>
//                     request =
//                     new HttpEntity<>(params, headers);

//             ResponseEntity<Map> tokenResponse =
//                     restTemplate.postForEntity(
//                             tokenUrl,
//                             request,
//                             Map.class
//                     );

//             session.setAttribute(
//                     "access_token",
//                     tokenResponse.getBody()
//                             .get("access_token"));

//             session.setAttribute(
//                     "instance_url",
//                     tokenResponse.getBody()
//                             .get("instance_url"));

//             // return ResponseEntity.ok(
//             //         "Salesforce Login Successful");
//             response.sendRedirect(
//     "https://salesforce-frontend-41ny.onrender.com"
// );
// return null;

//         } catch (Exception e) {

//             e.printStackTrace();

//             return ResponseEntity
//                     .badRequest()
//                     .body("Login Failed : "
//                             + e.getMessage());
//         }
//     }

//     // ---------------- VALIDATION RULES ----------------

//     @GetMapping("/api/validation-rules")
//     public ResponseEntity<?> getRules(
//             HttpSession session) {

//         try {

//             String accessToken =
//                     (String) session.getAttribute(
//                             "access_token");

//             String instanceUrl =
//                     (String) session.getAttribute(
//                             "instance_url");

//             if (accessToken == null) {

//                 return ResponseEntity
//                         .badRequest()
//                         .body("Please login first");
//             }

//             HttpHeaders headers = new HttpHeaders();

//             headers.setBearerAuth(accessToken);

//             HttpEntity<String> entity =
//                     new HttpEntity<>(headers);

//             String toolingApiUrl =
//                     instanceUrl +
//                     "/services/data/v59.0/tooling/query/" +
//                     "?q=SELECT+Id,ValidationName,Active+" +
//                     "FROM+ValidationRule";

//             ResponseEntity<String> response =
//                     restTemplate.exchange(
//                             toolingApiUrl,
//                             HttpMethod.GET,
//                             entity,
//                             String.class
//                     );

//             return ResponseEntity.ok(
//                     response.getBody());

//         } catch (Exception e) {

//             e.printStackTrace();

//             return ResponseEntity
//                     .badRequest()
//                     .body("Error : " + e.getMessage());
//         }
//     }

//     // ---------------- TOGGLE RULE ----------------

//     @GetMapping("/api/toggle-rule")
//     public String toggle(@RequestParam String id,
//                          @RequestParam Boolean active) {

//         return salesforceService
//                 .toggleValidationRule(id, active);
//     }
// }


// package com.cloudvandana.backend.controller;

// import com.cloudvandana.backend.service.SalesforceService;

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

//     // ---------------- PKCE ----------------

//     private String generateCodeVerifier() {

//         byte[] code = new byte[32];

//         new SecureRandom().nextBytes(code);

//         return Base64.getUrlEncoder()
//                 .withoutPadding()
//                 .encodeToString(code);
//     }

//     private String generateCodeChallenge(String codeVerifier)
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

//     @GetMapping("/api/login")
//     public void login(
//             HttpServletResponse response,
//             HttpSession session)
//             throws Exception {

//         String codeVerifier = generateCodeVerifier();

//         String codeChallenge =
//                 generateCodeChallenge(codeVerifier);

//         session.setAttribute(
//                 "code_verifier",
//                 codeVerifier);

//         String loginUrl =
//                 authUrl +
//                 "?response_type=code" +
//                 "&client_id=" + clientId +
//                 "&redirect_uri=" +
//                 URLEncoder.encode(
//                         redirectUri,
//                         StandardCharsets.UTF_8) +
//                 "&code_challenge=" +
//                 codeChallenge +
//                 "&code_challenge_method=S256";

//         response.sendRedirect(loginUrl);
//     }

//     // ---------------- CALLBACK ----------------

//     @GetMapping("/api/callback")
//     public void callback(
//             @RequestParam("code") String code,
//             HttpSession session,
//             HttpServletResponse response)
//             throws IOException {

//         try {

//             String codeVerifier =
//                     (String) session.getAttribute(
//                             "code_verifier");

//             salesforceService.getAccessToken(
//                     code,
//                     codeVerifier);

//             response.sendRedirect(
//                     "https://salesforce-frontend-41ny.onrender.com");

//         } catch (Exception e) {

//             e.printStackTrace();

//             response.sendRedirect(
//                     "https://salesforce-frontend-41ny.onrender.com");
//         }
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

package com.cloudvandana.backend.controller;

import com.cloudvandana.backend.service.SalesforceService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

    public AuthController(
            SalesforceService salesforceService) {

        this.salesforceService = salesforceService;
    }

    @Value("${salesforce.client.id}")
    private String clientId;

    @Value("${salesforce.redirect.uri}")
    private String redirectUri;

    @Value("${salesforce.auth.url}")
    private String authUrl;

    // ---------------- PKCE ----------------

    private String generateCodeVerifier() {

        byte[] code = new byte[32];

        new SecureRandom().nextBytes(code);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(code);
    }

    private String generateCodeChallenge(
            String codeVerifier)
            throws Exception {

        byte[] bytes =
                MessageDigest.getInstance("SHA-256")
                        .digest(
                                codeVerifier.getBytes(
                                        StandardCharsets.UTF_8));

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    // ---------------- LOGIN ----------------

    // @GetMapping("/api/login")
    // public void login(
    //         HttpServletResponse response)
    //         throws Exception {

    //     String codeVerifier =
    //             generateCodeVerifier();

    //     String codeChallenge =
    //             generateCodeChallenge(
    //                     codeVerifier);

    //     // SAVE IN COOKIE INSTEAD OF SESSION

    //     Cookie cookie =
    //             new Cookie(
    //                     "code_verifier",
    //                     codeVerifier);

    //     cookie.setHttpOnly(true);

    //     cookie.setSecure(true);

    //     cookie.setPath("/");

    //     cookie.setMaxAge(300);

    //     response.addCookie(cookie);

    //     String loginUrl =
    //             authUrl +
    //             "?response_type=code" +
    //             "&client_id=" + clientId +
    //             "&redirect_uri=" +
    //             URLEncoder.encode(
    //                     redirectUri,
    //                     StandardCharsets.UTF_8) +
    //             "&code_challenge=" +
    //             codeChallenge +
    //             "&code_challenge_method=S256";

    //     response.sendRedirect(loginUrl);
    // }
//     @GetMapping("/api/login")
// public void login(HttpServletResponse response) throws Exception {

//     String codeVerifier = generateCodeVerifier();
//     String codeChallenge = generateCodeChallenge(codeVerifier);

//     Cookie cookie = new Cookie("code_verifier", codeVerifier);
//     cookie.setHttpOnly(true);
//     cookie.setSecure(true);
//     cookie.setPath("/");
//     cookie.setMaxAge(300);
//     response.addCookie(cookie);

//     String loginUrl =
//             "https://login.salesforce.com/services/oauth2/authorize"
//             + "?response_type=code"
//             + "&client_id=" + clientId
//             + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
//             + "&code_challenge=" + codeChallenge
//             + "&code_challenge_method=S256";

//     System.out.println("REDIRECTING TO: " + loginUrl);

//     response.sendRedirect(loginUrl);
// }
@GetMapping("/api/login")
public void login(HttpServletResponse response, HttpSession session)
        throws Exception {

    String codeVerifier = generateCodeVerifier();
    String codeChallenge = generateCodeChallenge(codeVerifier);

    session.setAttribute("code_verifier", codeVerifier);

    String loginUrl =
            authUrl
            + "?response_type=code"
            + "&client_id=" + clientId
            + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
            + "&code_challenge=" + codeChallenge
            + "&code_challenge_method=S256";

    System.out.println("LOGIN URL => " + loginUrl);

    response.sendRedirect(loginUrl);
}

    // ---------------- CALLBACK ----------------

    @GetMapping("/api/callback")
public void callback(
        @RequestParam("code") String code,
        HttpSession session,
        HttpServletResponse response)
        throws IOException {

    try {

        String codeVerifier =
                (String) session.getAttribute("code_verifier");

        if (codeVerifier == null) {
            throw new RuntimeException("Missing code_verifier in session");
        }

        salesforceService.getAccessToken(code, codeVerifier);

        response.sendRedirect(
                "https://salesforce-frontend-41ny.onrender.com");

    } catch (Exception e) {

        e.printStackTrace();

        response.getWriter()
                .write("LOGIN FAILED: " + e.getMessage());
    }
}
    // @GetMapping("/api/callback")
    // public void callback(
    //         @RequestParam("code") String code,
    //         @CookieValue(
    //                 value = "code_verifier",
    //                 required = false)
    //         String codeVerifier,
    //         HttpServletResponse response)
    //         throws IOException {

    //     try {

    //         if (codeVerifier == null) {

    //             response.getWriter().write(
    //                     "PKCE code verifier missing");

    //             return;
    //         }

    //         salesforceService.getAccessToken(
    //                 code,
    //                 codeVerifier);

    //         response.sendRedirect(
    //                 "https://salesforce-frontend-41ny.onrender.com");

    //     } catch (Exception e) {

    //         e.printStackTrace();

    //         response.getWriter().write(
    //                 "LOGIN FAILED : "
    //                         + e.getMessage());
    //     }
    // }

    // ---------------- VALIDATION RULES ----------------

    @GetMapping("/api/validation-rules")
    public ResponseEntity<?> getRules() {

        try {

            return ResponseEntity.ok(
                    salesforceService
                            .getValidationRules());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Error fetching validation rules : "
                                    + e.getMessage());
        }
    }

    // ---------------- TOGGLE RULE ----------------

    @GetMapping("/api/toggle-rule")
    public ResponseEntity<?> toggle(
            @RequestParam String id,
            @RequestParam Boolean active) {

        try {

            return ResponseEntity.ok(
                    salesforceService
                            .toggleValidationRule(
                                    id,
                                    active));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Error updating validation rule : "
                                    + e.getMessage());
        }
    }
}