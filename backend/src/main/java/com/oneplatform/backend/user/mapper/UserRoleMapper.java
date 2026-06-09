package com.oneplatform.backend.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRoleMapper {

    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    @Delete("""
            <script>
            DELETE FROM sys_user_role
            WHERE user_id IN
            <foreach collection="userIds" item="userId" open="(" separator="," close=")">
              #{userId}
            </foreach>
            </script>
            """)
    void deleteByUserIds(@Param("userIds") List<Long> userIds);

    @Insert("INSERT INTO sys_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Select("SELECT COUNT(*) FROM sys_user_role WHERE role_id = #{roleId}")
    long countByRoleId(@Param("roleId") Long roleId);
}
