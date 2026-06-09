package com.oneplatform.backend.portal;

import java.util.List;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.security.InvalidTokenException;
import com.oneplatform.backend.security.JwtTokenService;
import com.oneplatform.backend.security.JwtUser;
import com.oneplatform.backend.user.entity.SysDepartment;
import com.oneplatform.backend.user.entity.SysRole;
import com.oneplatform.backend.user.entity.SysUser;
import com.oneplatform.backend.user.mapper.DepartmentMapper;
import com.oneplatform.backend.user.mapper.RoleMapper;
import com.oneplatform.backend.user.mapper.UserMapper;

import org.springframework.stereotype.Component;

@Component
public class DefaultPortalUserResolver implements PortalUserResolver {

    private final JwtTokenService jwtTokenService;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final RoleMapper roleMapper;
    private final PortalProperties properties;

    public DefaultPortalUserResolver(
            JwtTokenService jwtTokenService,
            UserMapper userMapper,
            DepartmentMapper departmentMapper,
            RoleMapper roleMapper,
            PortalProperties properties
    ) {
        this.jwtTokenService = jwtTokenService;
        this.userMapper = userMapper;
        this.departmentMapper = departmentMapper;
        this.roleMapper = roleMapper;
        this.properties = properties;
    }

    @Override
    public PortalUserContext resolve(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                JwtUser jwtUser = jwtTokenService.parse(authorization.substring("Bearer ".length()));
                return loadUser(jwtUser.userId());
            } catch (InvalidTokenException ex) {
                throw new BusinessException(401, "未登录或登录已失效");
            }
        }
        if (properties.allowDevFallbackUser()) {
            return loadUser(properties.devFallbackUserId());
        }
        throw new BusinessException(401, "未登录或登录已失效");
    }

    private PortalUserContext loadUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(401, "用户不可用或登录已失效");
        }
        SysDepartment department = departmentMapper.selectById(user.getDepartmentId());
        List<Long> roleIds = roleMapper.findByUserId(user.getId()).stream()
                .map(SysRole::getId)
                .toList();
        return new PortalUserContext(
                user.getId(),
                user.getUsername(),
                user.getDepartmentId(),
                department == null ? null : department.getName(),
                roleIds
        );
    }
}
