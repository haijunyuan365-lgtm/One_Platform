package com.oneplatform.backend.file;

public interface FileUploadRepository {

    PlatformFileRecord create(PlatformFileMutation mutation);
}
