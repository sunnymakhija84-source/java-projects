package com.sunny.ai.governance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GovernancePolicyRequest {
    @NotBlank(message = "Policy name is required")
    private String name;
    private String description;
    private String rules;
    private String createdBy;
}
