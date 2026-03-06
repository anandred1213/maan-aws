package com.practise.revision.service;

import com.practise.revision.dto.TokenRefreshRequest;
import com.practise.revision.dto.TokenRefreshResponse;

public interface UserAuthenticationService {
    boolean validateUser(String email, String password);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
}
