package com.intelligent.devcore.gateway;
import java.io.InputStream;
public interface StorageProvider {
    String saveFiles(InputStream inputStream, String secureFileName) throws Exception;
}
