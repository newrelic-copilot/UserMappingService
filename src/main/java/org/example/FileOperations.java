package org.example;

import Model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

public class FileOperations {
    public void fewMore() {
        System.out.println("Hello, Vulnerable World!");
        processFileName(" myFile.txt ");
        useStringUtils("  test  ");
        fileUploadExample();
        useJackson();
        useSpring();
    }

    public static void processFileName(String name) {
        System.out.println("Processing file: '" + StringUtils.strip(name) + "'");
    }

    public static void useStringUtils(String text) {
        System.out.println("Stripped string: '" + StringUtils.strip(text) + "'");
    }

    public static void fileUploadExample() {
        DiskFileItemFactory factory = FileUploadSupport.createDiskFileItemFactory();
        ServletFileUpload upload = FileUploadSupport.createServletFileUpload(factory);
        System.out.println("File upload factory created with request limit: " + upload.getSizeMax());
    }

//    public static void useJackson() {
//        ObjectMapper mapper = new ObjectMapper();
//        User user = new User("Alice", 30);
//        try {
//            String json = mapper.writeValueAsString(user);
//            System.out.println("User as JSON: " + json);
//            String reversedName = new StringBuilder(user.name).reverse().toString();
//            User user2 = mapper.readValue("{\"name\":\"" + reversedName + "\",\"age\":35}", User.class);
//            System.out.println("User 2: " + user2);
//        } catch (IOException e) {
//            System.err.println("Error processing JSON: " + e.getMessage());
//        }
//    }

    public static void useJackson() {
        ObjectMapper mapper = new ObjectMapper();
        UserWithIgnore user = new UserWithIgnore("Bob", 25, "secret");
        try {
            String json = mapper.writeValueAsString(user);
            System.out.println("User with ignore as JSON: " + json);
            // Attempting to deserialize might also reveal changes
            UserWithIgnore user2 = mapper.readValue("{\"name\":\"Charlie\",\"age\":40,\"secret\":\"top\"}", UserWithIgnore.class);
            System.out.println("User with ignore 2: " + user2);
        } catch (IOException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
        }
    }



    public static void useSpring() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        ObjectMapper springObjectMapper = builder.build();
        System.out.println("Spring's ObjectMapper created: " + springObjectMapper.getClass().getName());
    }
}
