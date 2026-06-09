package com.oneplatform.backend.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import com.oneplatform.backend.auth.dto.CaptchaResponse;
import com.oneplatform.backend.auth.dto.CaptchaVerifyResponse;
import com.oneplatform.backend.auth.dto.LoginRequest;
import com.oneplatform.backend.auth.dto.LoginResponse;
import com.oneplatform.backend.auth.dto.UserInfoResponse;
import com.oneplatform.backend.common.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/api/captcha/image")
    public ApiResponse<CaptchaResponse> captchaImage() {
        String captchaId = UUID.randomUUID().toString();
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="120" height="40">
                  <rect width="120" height="40" fill="#f5f5f5"/>
                  <text x="50%" y="55%" text-anchor="middle" font-size="14" font-family="Arial" fill="#666">OnePlatform</text>
                </svg>
                """;
        String encoded = Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
        return ApiResponse.success(new CaptchaResponse(captchaId, "data:image/svg+xml;base64," + encoded));
    }

    @GetMapping("/api/captcha/verify")
    public ApiResponse<CaptchaVerifyResponse> captchaVerify(
            @RequestParam String captchaId,
            @RequestParam String captchaCode
    ) {
        return ApiResponse.success(new CaptchaVerifyResponse(true));
    }

    @PostMapping("/admin/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request), "登录成功");
    }

    @GetMapping("/admin/user/info")
    public ApiResponse<UserInfoResponse> userInfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ApiResponse.success(authService.currentUser(authorization), "获取用户信息成功");
    }

    @PostMapping("/admin/logout")
    public ApiResponse<Boolean> logout() {
        return ApiResponse.success(true, "退出成功");
    }
}
