package com.payper.server.domain.test;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController("/auth-test")
public class TestAuthController {
    @GetMapping("/")
    public String index() {
        return "Hello World";
    }

    @GetMapping("/me")
    public String getMe(
            Principal principal,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("principal.getName() = " + principal.getName());
        System.out.println("userDetails.username() = " + userDetails.getUsername());
        System.out.println("userDetails.password() = " + userDetails.getPassword());
        System.out.println("userDetails.getAuthorities() = " + userDetails.getAuthorities());

        return "Hello me";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Hello Admin";
    }
}
