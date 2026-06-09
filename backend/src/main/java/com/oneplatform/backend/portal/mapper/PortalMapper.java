package com.oneplatform.backend.portal.mapper;

import java.util.List;

import com.oneplatform.backend.portal.PortalAddressRecord;
import com.oneplatform.backend.portal.PortalCredentialRecord;
import com.oneplatform.backend.portal.PortalInstructionRecord;
import com.oneplatform.backend.portal.PortalOperationLogRecord;
import com.oneplatform.backend.portal.PortalPermissionSummary;
import com.oneplatform.backend.portal.PortalProjectRecord;
import com.oneplatform.backend.portal.PortalQrcodeRecord;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PortalMapper {

    @Select("""
            <script>
            SELECT p.id,
                   p.name,
                   p.short_name AS shortName,
                   p.logo_url AS logoUrl,
                   p.category,
                   p.tags,
                   p.description,
                   p.maintainer_name AS maintainerName,
                   p.status,
                   p.last_check_time AS lastCheckTime,
                   p.response_time AS responseTime,
                   p.abnormal_reason AS abnormalReason
            FROM op_project p
            WHERE p.enabled = 1
              AND p.deleted = 0
              AND EXISTS (
                SELECT 1
                FROM op_project_permission pp
                WHERE pp.project_id = p.id
                  AND pp.status = 1
                  AND pp.deleted = 0
                  AND pp.visible = 1
                  AND (
                    (pp.target_type = '用户' AND pp.target_id = #{userId})
                    OR (pp.target_type = '部门' AND pp.target_id = #{departmentId})
                    <if test="roleIds != null and roleIds.size() > 0">
                    OR (pp.target_type = '角色' AND pp.target_id IN
                      <foreach collection="roleIds" item="roleId" open="(" separator="," close=")">
                        #{roleId}
                      </foreach>
                    )
                    </if>
                  )
              )
              <if test="keyword != null and keyword != ''">
              AND (
                p.name LIKE CONCAT('%', #{keyword}, '%')
                OR p.short_name LIKE CONCAT('%', #{keyword}, '%')
                OR p.description LIKE CONCAT('%', #{keyword}, '%')
                OR p.tags LIKE CONCAT('%', #{keyword}, '%')
              )
              </if>
              <if test="category != null and category != ''">
              AND p.category = #{category}
              </if>
              <if test="status != null and status != ''">
              AND p.status = #{status}
              </if>
            ORDER BY p.sort ASC, p.id ASC
            </script>
            """)
    List<PortalProjectRecord> findVisibleProjects(
            @Param("userId") Long userId,
            @Param("departmentId") Long departmentId,
            @Param("roleIds") List<Long> roleIds,
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("status") String status
    );

    @Select("""
            <script>
            SELECT p.id,
                   p.name,
                   p.short_name AS shortName,
                   p.logo_url AS logoUrl,
                   p.category,
                   p.tags,
                   p.description,
                   p.maintainer_name AS maintainerName,
                   p.status,
                   p.last_check_time AS lastCheckTime,
                   p.response_time AS responseTime,
                   p.abnormal_reason AS abnormalReason
            FROM op_project p
            WHERE p.id = #{projectId}
              AND p.enabled = 1
              AND p.deleted = 0
              AND EXISTS (
                SELECT 1
                FROM op_project_permission pp
                WHERE pp.project_id = p.id
                  AND pp.status = 1
                  AND pp.deleted = 0
                  AND pp.visible = 1
                  AND (
                    (pp.target_type = '用户' AND pp.target_id = #{userId})
                    OR (pp.target_type = '部门' AND pp.target_id = #{departmentId})
                    <if test="roleIds != null and roleIds.size() > 0">
                    OR (pp.target_type = '角色' AND pp.target_id IN
                      <foreach collection="roleIds" item="roleId" open="(" separator="," close=")">
                        #{roleId}
                      </foreach>
                    )
                    </if>
                  )
              )
            LIMIT 1
            </script>
            """)
    PortalProjectRecord findVisibleProjectById(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId,
            @Param("departmentId") Long departmentId,
            @Param("roleIds") List<Long> roleIds
    );

    @Select("""
            SELECT id,
                   name,
                   type,
                   url,
                   is_default AS isDefault
            FROM op_project_address
            WHERE project_id = #{projectId}
              AND status = 1
              AND deleted = 0
            ORDER BY sort ASC, id ASC
            """)
    List<PortalAddressRecord> findAddresses(@Param("projectId") Long projectId);

    @Select("""
            SELECT id,
                   project_id AS projectId,
                   name,
                   username,
                   password_cipher AS passwordCipher,
                   password_masked AS passwordMasked,
                   environment,
                   description,
                   status
            FROM op_project_credential
            WHERE project_id = #{projectId}
              AND status = 1
              AND deleted = 0
            ORDER BY id ASC
            """)
    List<PortalCredentialRecord> findCredentials(@Param("projectId") Long projectId);

    @Select("""
            SELECT id,
                   project_id AS projectId,
                   name,
                   username,
                   password_cipher AS passwordCipher,
                   password_masked AS passwordMasked,
                   environment,
                   description,
                   status
            FROM op_project_credential
            WHERE id = #{credentialId}
              AND deleted = 0
            LIMIT 1
            """)
    PortalCredentialRecord findCredentialById(@Param("credentialId") Long credentialId);

    @Select("""
            SELECT id,
                   name,
                   image_url AS imageUrl,
                   audience,
                   description
            FROM op_project_qrcode
            WHERE project_id = #{projectId}
              AND status = 1
              AND deleted = 0
            ORDER BY id ASC
            """)
    List<PortalQrcodeRecord> findQrcodes(@Param("projectId") Long projectId);

    @Select("""
            SELECT browser_requirement AS browserRequirement,
                   vpn_requirement AS vpnRequirement,
                   notes,
                   maintainer,
                   contact_phone AS contactPhone
            FROM op_project_instruction
            WHERE project_id = #{projectId}
              AND deleted = 0
            LIMIT 1
            """)
    PortalInstructionRecord findInstruction(@Param("projectId") Long projectId);

    @Select("""
            <script>
            SELECT COALESCE(MAX(visible), 0) AS visible,
                   COALESCE(MAX(password_view), 0) AS passwordView,
                   COALESCE(MAX(password_copy), 0) AS passwordCopy,
                   COALESCE(MAX(qrcode_view), 0) AS qrcodeView
            FROM op_project_permission pp
            WHERE pp.project_id = #{projectId}
              AND pp.status = 1
              AND pp.deleted = 0
              AND (
                (pp.target_type = '用户' AND pp.target_id = #{userId})
                OR (pp.target_type = '部门' AND pp.target_id = #{departmentId})
                <if test="roleIds != null and roleIds.size() > 0">
                OR (pp.target_type = '角色' AND pp.target_id IN
                  <foreach collection="roleIds" item="roleId" open="(" separator="," close=")">
                    #{roleId}
                  </foreach>
                )
                </if>
              )
            </script>
            """)
    PortalPermissionSummary findPermission(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId,
            @Param("departmentId") Long departmentId,
            @Param("roleIds") List<Long> roleIds
    );

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
    void recordOperation(PortalOperationLogRecord log);

    @Update("""
            INSERT INTO op_project_recent_visit (user_id, project_id, visit_count, last_visit_time)
            VALUES (#{userId}, #{projectId}, 1, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
              visit_count = visit_count + 1,
              last_visit_time = CURRENT_TIMESTAMP
            """)
    void recordRecentVisit(@Param("userId") Long userId, @Param("projectId") Long projectId);
}
