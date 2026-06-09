package com.oneplatform.backend.organization;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.oneplatform.backend.user.entity.SysDepartment;
import com.oneplatform.backend.user.entity.SysRole;
import com.oneplatform.backend.user.entity.SysUser;
import com.oneplatform.backend.user.mapper.DepartmentMapper;
import com.oneplatform.backend.user.mapper.RoleMapper;
import com.oneplatform.backend.user.mapper.UserMapper;
import com.oneplatform.backend.user.mapper.UserRoleMapper;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MybatisUserRepository implements UserRepository {

    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public MybatisUserRepository(
            UserMapper userMapper,
            DepartmentMapper departmentMapper,
            RoleMapper roleMapper,
            UserRoleMapper userRoleMapper
    ) {
        this.userMapper = userMapper;
        this.departmentMapper = departmentMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<UserRecord> findAll() {
        Map<Long, SysDepartment> departmentMap = departmentMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                        .eq(SysDepartment::getDeleted, 0))
                .stream()
                .collect(Collectors.toMap(SysDepartment::getId, Function.identity()));
        return userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
                        .orderByAsc(SysUser::getId))
                .stream()
                .map(user -> map(user, departmentMap.get(user.getDepartmentId())))
                .toList();
    }

    @Override
    public List<AdminUserResponse.RoleBrief> findRoles(Long userId) {
        return roleMapper.findByUserId(userId).stream()
                .map(this::mapRole)
                .toList();
    }

    @Override
    public UserRecord findById(Long id) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, id)
                .eq(SysUser::getDeleted, 0)
                .last("LIMIT 1"));
        if (user == null) {
            return null;
        }
        SysDepartment department = departmentMapper.selectById(user.getDepartmentId());
        return map(user, department);
    }

    @Override
    public boolean existsByUsername(String username, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsEnabledDepartment(Long departmentId) {
        if (departmentId == null) {
            return false;
        }
        return departmentMapper.selectCount(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getId, departmentId)
                .eq(SysDepartment::getStatus, 1)
                .eq(SysDepartment::getDeleted, 0)) > 0;
    }

    @Override
    public List<Long> findAssignableRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return roleMapper.findAssignableIds(roleIds);
    }

    @Override
    @Transactional
    public UserRecord create(UserMutation mutation, String passwordHash) {
        SysUser user = new SysUser();
        applyMutation(user, mutation);
        user.setPasswordHash(passwordHash);
        user.setDeleted(0);
        userMapper.insert(user);
        replaceRoles(List.of(user.getId()), mutation.roleIds());
        return findById(user.getId());
    }

    @Override
    @Transactional
    public UserRecord update(Long id, UserMutation mutation) {
        SysUser user = new SysUser();
        user.setId(id);
        applyMutation(user, mutation);
        userMapper.updateById(user);
        replaceRoles(List.of(id), mutation.roleIds());
        return findById(id);
    }

    @Override
    public void softDelete(List<Long> ids) {
        userMapper.update(null, new LambdaUpdateWrapper<SysUser>()
                .in(SysUser::getId, ids)
                .set(SysUser::getDeleted, 1));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        userMapper.update(null, new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getId, id)
                .eq(SysUser::getDeleted, 0)
                .set(SysUser::getStatus, status));
    }

    @Override
    public void updatePasswordHash(Long id, String passwordHash) {
        userMapper.update(null, new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getId, id)
                .eq(SysUser::getDeleted, 0)
                .set(SysUser::getPasswordHash, passwordHash));
    }

    @Override
    public void updatePositionName(List<Long> ids, String positionName) {
        userMapper.update(null, new LambdaUpdateWrapper<SysUser>()
                .in(SysUser::getId, ids)
                .eq(SysUser::getDeleted, 0)
                .set(SysUser::getPositionName, positionName));
    }

    @Override
    @Transactional
    public void replaceRoles(List<Long> userIds, List<Long> roleIds) {
        userRoleMapper.deleteByUserIds(userIds);
        for (Long userId : userIds) {
            for (Long roleId : roleIds) {
                userRoleMapper.insertUserRole(userId, roleId);
            }
        }
    }

    private UserRecord map(SysUser user, SysDepartment department) {
        return new UserRecord(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getDepartmentId(),
                department == null ? null : department.getName(),
                user.getAvatar(),
                user.getPositionName(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginTime(),
                user.getLastLoginIp(),
                user.getRemark()
        );
    }

    private AdminUserResponse.RoleBrief mapRole(SysRole role) {
        return new AdminUserResponse.RoleBrief(role.getId(), role.getName(), role.getCode());
    }

    private void applyMutation(SysUser user, UserMutation mutation) {
        user.setUsername(mutation.username());
        user.setRealName(mutation.realName());
        user.setNickname(mutation.nickname());
        user.setAvatar(mutation.avatar());
        user.setEmail(mutation.email());
        user.setPhone(mutation.phone());
        user.setDepartmentId(mutation.departmentId());
        user.setPositionName(mutation.positionName());
        user.setStatus(mutation.status());
        user.setRemark(mutation.remark());
    }
}
