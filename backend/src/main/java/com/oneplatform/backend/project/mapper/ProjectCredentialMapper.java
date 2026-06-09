package com.oneplatform.backend.project.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneplatform.backend.project.ProjectCredentialOperationLogRecord;
import com.oneplatform.backend.project.ProjectCredentialRecord;
import com.oneplatform.backend.project.entity.OpProjectCredential;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProjectCredentialMapper extends BaseMapper<OpProjectCredential> {

    @Select("""
            <script>
            SELECT c.id,
                   c.project_id AS projectId,
                   p.name AS projectName,
                   c.name,
                   c.username,
                   c.password_cipher AS passwordCipher,
                   c.password_masked AS passwordMasked,
                   c.environment,
                   c.description,
                   c.expire_date AS expireDate,
                   c.status,
                   c.updated_at AS updatedAt
            FROM op_project_credential c
            LEFT JOIN op_project p ON p.id = c.project_id AND p.deleted = 0
            WHERE c.deleted = 0
              <if test="projectId != null">
              AND c.project_id = #{projectId}
              </if>
              <if test="environment != null and environment != ''">
              AND c.environment = #{environment}
              </if>
              <if test="status != null">
              AND c.status = #{status}
              </if>
            ORDER BY c.project_id ASC, c.id ASC
            </script>
            """)
    List<ProjectCredentialRecord> findCredentials(
            @Param("projectId") Long projectId,
            @Param("environment") String environment,
            @Param("status") Integer status
    );

    @Select("""
            SELECT c.id,
                   c.project_id AS projectId,
                   p.name AS projectName,
                   c.name,
                   c.username,
                   c.password_cipher AS passwordCipher,
                   c.password_masked AS passwordMasked,
                   c.environment,
                   c.description,
                   c.expire_date AS expireDate,
                   c.status,
                   c.updated_at AS updatedAt
            FROM op_project_credential c
            LEFT JOIN op_project p ON p.id = c.project_id AND p.deleted = 0
            WHERE c.id = #{id}
              AND c.deleted = 0
            LIMIT 1
            """)
    ProjectCredentialRecord findCredentialById(@Param("id") Long id);

    @Insert("""
            INSERT INTO op_operation_log (
              operator_id,
              operator,
              account,
              department_id,
              department,
              project_id,
              project_name,
              operation_type,
              target,
              result,
              ip,
              remark
            )
            VALUES (
              #{operatorId},
              #{operator},
              #{account},
              #{departmentId},
              #{department},
              #{projectId},
              #{projectName},
              #{operationType},
              #{target},
              #{result},
              #{ip},
              #{remark}
            )
            """)
    void recordOperation(ProjectCredentialOperationLogRecord log);
}
