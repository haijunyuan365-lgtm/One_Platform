package com.oneplatform.backend.user.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneplatform.backend.user.entity.SysRole;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {

    @Select("""
            SELECT r.*
            FROM sys_role r
            INNER JOIN sys_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
              AND r.status = 1
              AND r.deleted = 0
            ORDER BY r.sort ASC, r.id ASC
            """)
    List<SysRole> findByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT DISTINCT m.permission
            FROM sys_menu m
            INNER JOIN sys_role_menu rm ON rm.menu_id = m.id
            INNER JOIN sys_user_role ur ON ur.role_id = rm.role_id
            INNER JOIN sys_role r ON r.id = ur.role_id
            WHERE ur.user_id = #{userId}
              AND r.status = 1
              AND m.status = 1
              AND m.deleted = 0
              AND m.permission IS NOT NULL
            ORDER BY m.permission
            """)
    List<String> findMenuPermissionsByUserId(@Param("userId") Long userId);

    @Select("""
            <script>
            SELECT id
            FROM sys_role
            WHERE status = 1
              AND deleted = 0
              AND id IN
              <foreach collection="roleIds" item="roleId" open="(" separator="," close=")">
                #{roleId}
              </foreach>
            </script>
            """)
    List<Long> findAssignableIds(@Param("roleIds") List<Long> roleIds);
}
