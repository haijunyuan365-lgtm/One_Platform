package com.oneplatform.backend.organization;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneplatform.backend.user.entity.SysDepartment;
import com.oneplatform.backend.user.entity.SysUser;
import com.oneplatform.backend.user.mapper.DepartmentMapper;
import com.oneplatform.backend.user.mapper.UserMapper;

import org.springframework.stereotype.Repository;

@Repository
public class MybatisDepartmentRepository implements DepartmentRepository {

    private final DepartmentMapper departmentMapper;
    private final UserMapper userMapper;

    public MybatisDepartmentRepository(DepartmentMapper departmentMapper, UserMapper userMapper) {
        this.departmentMapper = departmentMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<DepartmentRecord> findAll() {
        return departmentMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                        .eq(SysDepartment::getDeleted, 0)
                        .orderByAsc(SysDepartment::getSort)
                        .orderByAsc(SysDepartment::getId))
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public DepartmentRecord findById(Long id) {
        SysDepartment department = departmentMapper.selectOne(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getId, id)
                .eq(SysDepartment::getDeleted, 0)
                .last("LIMIT 1"));
        return department == null ? null : map(department);
    }

    @Override
    public long countEnabledUsers(Long departmentId) {
        return userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDepartmentId, departmentId)
                .eq(SysUser::getStatus, 1)
                .eq(SysUser::getDeleted, 0));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysDepartment department = new SysDepartment();
        department.setId(id);
        department.setStatus(status);
        departmentMapper.updateById(department);
    }

    private DepartmentRecord map(SysDepartment department) {
        return new DepartmentRecord(
                department.getId(),
                department.getParentId(),
                department.getName(),
                department.getCode(),
                department.getLeader(),
                department.getPhone(),
                department.getSort(),
                department.getStatus(),
                department.getCreatedAt(),
                department.getUpdatedAt()
        );
    }
}
