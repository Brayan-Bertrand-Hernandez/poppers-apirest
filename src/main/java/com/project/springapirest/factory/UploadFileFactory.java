package com.project.springapirest.factory;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadFileFactory {
    MultipartFile copy(MultipartFile file) throws IOException;
    byte[] compressBytes(byte[] data);
    byte[] decompressBytes(byte[] data);
}
