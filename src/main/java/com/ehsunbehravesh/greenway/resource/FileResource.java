package com.ehsunbehravesh.greenway.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


/**
 *
 * @author ehsun7b
 */
public class FileResource {

    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private final String name;
    private final int bufferSize;
    
    public FileResource(String name) {
        this.name = name;
        this.bufferSize = DEFAULT_BUFFER_SIZE;
    }

    public FileResource(String name, int bufferSize) {
        this.name = name;
        this.bufferSize = bufferSize;
    }
   
    public String getTextContent() throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
            StringBuilder sb = new StringBuilder();
            
            byte[] buffer = new byte[bufferSize];
            
            for (int len = is.read(buffer); len > 0; len = is.read(buffer)) {
                sb.append(new String(buffer, StandardCharsets.UTF_8));
            }
            
            return sb.toString();
        }
    }
}
