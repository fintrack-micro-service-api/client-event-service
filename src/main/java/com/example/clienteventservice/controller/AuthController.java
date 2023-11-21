package com.example.clienteventservice.controller;

import com.example.clienteventservice.domain.request.LoginRequest;
import com.example.clienteventservice.domain.request.UserRequest;
import com.example.clienteventservice.exception.BadRequestException;
import com.example.clienteventservice.service.UserService;
import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/auth/clients")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "register")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        Preconditions.checkNotNull(userRequest, "userRequest can not be null");
        return ResponseEntity.ok().body(userService.create(userRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try{
        return ResponseEntity.ok().body(userService.login(loginRequest));

        }catch (BadRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }

    @GetMapping("/verify-email")
    @Operation(summary = "verify email use in email click link ")
    public RedirectView verifyEmail(@RequestParam String email, @RequestParam String type) {
        return userService.verifyEmail(email, type);
    }

    @PostMapping("/email")
    @Operation(summary = "generate verify email again")
    public ResponseEntity<?> generateCode(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.generateLinkVerifyEmail(email, "false", 1, "false"));
    }

    @PostMapping("/forget-password/email")
    @Operation(summary = "generate verify email for reset password")
    public ResponseEntity<?> generateCodeForget(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.generateEmailForgetPassword(email));
    }

    @PutMapping("/forget-password")
    @Operation(summary = "reset password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        return ResponseEntity.ok().body(userService.forgetPassword(email, newPassword));
    }
}
