package com.techmate.techmate.hexagonal.infrastructure.security;

import lombok.Data;

@Data
public class AuthCredentials {
    
    private String email;
    private String password;

}



