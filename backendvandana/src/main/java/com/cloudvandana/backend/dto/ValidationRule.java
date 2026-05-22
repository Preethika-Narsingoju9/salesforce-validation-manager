package com.cloudvandana.backend.dto;

import lombok.Data;

// @Data
// public class ValidationRule {

//     private String Id;
//     private String ValidationName;
//     private Boolean Active;
// }

@Data
public class ValidationRule {

    private String Id;
    private String FullName;
    private Boolean Active;
}