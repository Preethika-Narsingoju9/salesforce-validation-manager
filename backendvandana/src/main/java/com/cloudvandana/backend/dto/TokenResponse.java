package com.cloudvandana.backend.dto;

import lombok.Data;

@Data
public class TokenResponse {

    private String access_token;
    private String instance_url;
    private String token_type;
}