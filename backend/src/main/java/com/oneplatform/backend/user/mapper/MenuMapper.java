package com.oneplatform.backend.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneplatform.backend.user.entity.SysMenu;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MenuMapper extends BaseMapper<SysMenu> {
}
