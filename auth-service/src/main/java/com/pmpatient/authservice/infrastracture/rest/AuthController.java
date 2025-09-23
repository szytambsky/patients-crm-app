package com.pmpatient.authservice.infrastracture.rest;

import com.pmpatient.authservice.domain.AuthService;
import com.pmpatient.authservice.infrastracture.rest.dto.LoginRequestDto;
import com.pmpatient.authservice.infrastracture.rest.dto.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        Optional<String> token = authService.authenticate(loginRequestDto);
        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String authToken = token.get();
        return ResponseEntity.ok(new LoginResponseDto(authToken));
    }
}
