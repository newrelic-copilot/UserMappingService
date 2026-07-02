package org.example;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileUploadSupportTest {
    @Test
    void createsUploadComponentsWithSecureDefaults() {
        DiskFileItemFactory factory = FileUploadSupport.createDiskFileItemFactory();
        ServletFileUpload upload = FileUploadSupport.createServletFileUpload(factory);

        assertEquals(new File(System.getProperty("java.io.tmpdir")), factory.getRepository());
        assertEquals(FileUploadSupport.FILE_SIZE_THRESHOLD_BYTES, factory.getSizeThreshold());
        assertEquals(FileUploadSupport.MAX_PART_HEADER_SIZE_BYTES, upload.getPartHeaderSizeMax());
        assertEquals(FileUploadSupport.MAX_FILE_COUNT, upload.getFileCountMax());
        assertEquals(FileUploadSupport.MAX_FILE_SIZE_BYTES, upload.getFileSizeMax());
        assertEquals(FileUploadSupport.MAX_REQUEST_SIZE_BYTES, upload.getSizeMax());
    }

    @Test
    void multipartPropertiesMatchUploadSupportLimits() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            assertNotNull(inputStream);
            properties.load(inputStream);
        }

        assertEquals("1MB", properties.getProperty("spring.servlet.multipart.max-file-size"));
        assertEquals("5MB", properties.getProperty("spring.servlet.multipart.max-request-size"));
        assertEquals(1024L * 1024L, FileUploadSupport.MAX_FILE_SIZE_BYTES);
        assertEquals(5L * 1024L * 1024L, FileUploadSupport.MAX_REQUEST_SIZE_BYTES);
    }
}
