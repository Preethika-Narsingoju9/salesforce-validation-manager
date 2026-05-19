package com.cloudvandana.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class ValidationRuleResponse {

    private List<ValidationRule> records;
}