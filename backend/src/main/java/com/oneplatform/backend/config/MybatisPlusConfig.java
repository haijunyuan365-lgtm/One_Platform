package com.oneplatform.backend.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.oneplatform.backend.**.mapper")
public class MybatisPlusConfig {
}
