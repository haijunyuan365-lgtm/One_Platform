package com.oneplatform.backend.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneplatform.backend.project.entity.OpProject;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProjectMapper extends BaseMapper<OpProject> {

    @Select("""
            SELECT
              (SELECT COUNT(1) FROM op_project_address WHERE project_id = #{projectId} AND deleted = 0)
            + (SELECT COUNT(1) FROM op_project_instruction WHERE project_id = #{projectId} AND deleted = 0)
            + (SELECT COUNT(1) FROM op_project_credential WHERE project_id = #{projectId} AND deleted = 0)
            + (SELECT COUNT(1) FROM op_project_qrcode WHERE project_id = #{projectId} AND deleted = 0)
            + (SELECT COUNT(1) FROM op_project_permission WHERE project_id = #{projectId} AND deleted = 0)
            + (SELECT COUNT(1) FROM op_project_favorite WHERE project_id = #{projectId})
            + (SELECT COUNT(1) FROM op_project_recent_visit WHERE project_id = #{projectId})
            + (SELECT COUNT(1) FROM op_status_config WHERE project_id = #{projectId} AND deleted = 0)
            + (SELECT COUNT(1) FROM op_status_record WHERE project_id = #{projectId})
            + (SELECT COUNT(1) FROM op_operation_log WHERE project_id = #{projectId})
            """)
    long countProjectRelations(Long projectId);
}
