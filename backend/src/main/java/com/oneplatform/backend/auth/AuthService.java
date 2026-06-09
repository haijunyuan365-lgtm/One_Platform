package com.oneplatform.backend.auth;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneplatform.backend.auth.dto.LoginRequest;
import com.oneplatform.backend.auth.dto.LoginResponse;
import com.oneplatform.backend.auth.dto.UserInfoResponse;
import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.security.JwtTokenService;
import com.oneplatform.backend.security.JwtUser;
import com.oneplatform.backend.user.entity.SysDepartment;
import com.oneplatform.backend.user.entity.SysRole;
import com.oneplatform.backend.user.entity.SysUser;
import com.oneplatform.backend.user.mapper.DepartmentMapper;
import com.oneplatform.backend.user.mapper.RoleMapper;
import com.oneplatform.backend.user.mapper.UserMapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(
            UserMapper userMapper,
            DepartmentMapper departmentMapper,
            RoleMapper roleMapper,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.userMapper = userMapper;
        this.departmentMapper = departmentMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        String account = request.account();
        if (account == null || account.isBlank()) {
            throw new BusinessException(400, "请输入登录账号");
        }
        SysUser user = findEnabledUser(account);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        String token = jwtTokenService.generate(user.getId(), user.getUsername());
        String refreshToken = jwtTokenService.generate(user.getId(), user.getUsername());
        return new LoginResponse(token, refreshToken, buildUserInfo(user));
    }

    public UserInfoResponse currentUser(String authorization) {
        String token = resolveBearerToken(authorization);
        JwtUser jwtUser = jwtTokenService.parse(token);
        SysUser user = userMapper.selectById(jwtUser.userId());
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(401, "用户不可用或登录已失效");
        }
        return buildUserInfo(user);
    }

    private SysUser findEnabledUser(String account) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, account)
                .eq(SysUser::getDeleted, 0)
                .last("LIMIT 1"));
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return user;
    }

    private UserInfoResponse buildUserInfo(SysUser user) {
        SysDepartment department = departmentMapper.selectById(user.getDepartmentId());
        List<SysRole> roles = roleMapper.findByUserId(user.getId());
        List<String> buttons = roleMapper.findMenuPermissionsByUserId(user.getId());
        return new UserInfoResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                null,
                null,
                user.getUsername(),
                user.getNickname() == null ? user.getRealName() : user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                user.getStatus(),
                user.getLastLoginTime(),
                user.getLastLoginIp(),
                user.getRemark(),
                user.getDepartmentId(),
                mapDepartment(department),
                roles.stream().map(this::mapRole).toList(),
                buttons
        );
    }

    private UserInfoResponse.DepartmentInfo mapDepartment(SysDepartment department) {
        if (department == null) {
            return null;
        }
        return new UserInfoResponse.DepartmentInfo(
                department.getId(),
                department.getCreatedAt(),
                department.getUpdatedAt(),
                null,
                null,
                department.getName(),
                department.getCode(),
                department.getParentId(),
                "department",
                1L,
                department.getStatus(),
                department.getSort(),
                null,
                department.getLeader(),
                department.getPhone(),
                null
        );
    }

    private UserInfoResponse.RoleInfo mapRole(SysRole role) {
        return new UserInfoResponse.RoleInfo(
                role.getId(),
                role.getCreatedAt(),
                role.getUpdatedAt(),
                null,
                null,
                role.getName(),
                role.getCode(),
                role.getDescription(),
                null,
                role.getStatus(),
                role.getSort(),
                role.getIsSystem()
        );
    }

    private static String resolveBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return authorization.substring("Bearer ".length());
    }
}
