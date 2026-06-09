package com.oneplatform.backend.project.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneplatform.backend.project.ProjectQrcodeRecord;
import com.oneplatform.backend.project.entity.OpProjectQrcode;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProjectQrcodeMapper extends BaseMapper<OpProjectQrcode> {

    @Select("""
            <script>
            SELECT q.id,
                   q.project_id AS projectId,
                   p.name AS projectName,
                   q.name,
                   q.file_id AS fileId,
                   q.image_url AS imageUrl,
                   q.audience,
                   q.description,
                   q.status,
                   q.updated_at AS updatedAt
            FROM op_project_qrcode q
            LEFT JOIN op_project p ON p.id = q.project_id AND p.deleted = 0
            WHERE q.deleted = 0
              <if test="projectId != null">
              AND q.project_id = #{projectId}
              </if>
              <if test="status != null">
              AND q.status = #{status}
              </if>
            ORDER BY q.project_id ASC, q.id ASC
            </script>
            """)
    List<ProjectQrcodeRecord> findQrcodes(
            @Param("projectId") Long projectId,
            @Param("status") Integer status
    );

    @Select("""
            SELECT q.id,
                   q.project_id AS projectId,
                   p.name AS projectName,
                   q.name,
                   q.file_id AS fileId,
                   q.image_url AS imageUrl,
                   q.audience,
                   q.description,
                   q.status,
                   q.updated_at AS updatedAt
            FROM op_project_qrcode q
            LEFT JOIN op_project p ON p.id = q.project_id AND p.deleted = 0
            WHERE q.id = #{id}
              AND q.deleted = 0
            LIMIT 1
            """)
    ProjectQrcodeRecord findQrcodeById(@Param("id") Long id);
}
