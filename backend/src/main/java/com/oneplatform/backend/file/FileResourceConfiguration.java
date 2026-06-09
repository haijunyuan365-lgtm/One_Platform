package com.oneplatform.backend.file;

import java.nio.file.Path;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileResourceConfiguration implements WebMvcConfigurer {

    private final FileUploadProperties properties;

    public FileResourceConfiguration(ObjectProvider<FileUploadProperties> propertiesProvider) {
        this.properties = propertiesProvider.getIfAvailable(FileUploadProperties::new);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String prefix = properties.getPublicPrefix();
        if (prefix == null || prefix.isBlank()) {
            prefix = "/uploads";
        }
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        if (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        String location = Path.of(properties.getUploadDir()).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler(prefix + "/**").addResourceLocations(location);
    }
}
