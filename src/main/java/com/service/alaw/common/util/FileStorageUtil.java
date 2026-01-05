package com.service.alaw.common.util;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageUtil {
    String saveProfileImage(MultipartFile file);
    String savePostImage(MultipartFile file);
}
