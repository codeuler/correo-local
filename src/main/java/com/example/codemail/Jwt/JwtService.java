package com.example.codemail.Jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String getToken(UserDetails usuario) {
        return "";
    }
}
