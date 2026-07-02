package org.example;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.File;

public final class FileUploadSupport {
    static final int MAX_PART_HEADER_SIZE_BYTES = 512;
    static final long MAX_FILE_COUNT = 10L;
    static final long MAX_FILE_SIZE_BYTES = 1024L * 1024L;
    static final long MAX_REQUEST_SIZE_BYTES = 5L * 1024L * 1024L;
    static final int FILE_SIZE_THRESHOLD_BYTES = 1024 * 1024;

    private FileUploadSupport() {
    }

    public static DiskFileItemFactory createDiskFileItemFactory() {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        factory.setRepository(tempDir);
        factory.setSizeThreshold(FILE_SIZE_THRESHOLD_BYTES);
        return factory;
    }

    public static ServletFileUpload createServletFileUpload(DiskFileItemFactory factory) {
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setPartHeaderSizeMax(MAX_PART_HEADER_SIZE_BYTES);
        upload.setFileCountMax(MAX_FILE_COUNT);
        upload.setFileSizeMax(MAX_FILE_SIZE_BYTES);
        upload.setSizeMax(MAX_REQUEST_SIZE_BYTES);
        return upload;
    }
}
