package com.fitpal.fitpalspringbootapp.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ==================== DEACTIVATE ACCOUNT REQUEST ====================
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeactivateAccountRequest {

    @NotBlank(message = "Password is required")
    private String password;
}
