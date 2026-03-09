package MockPorvider;

import com.intelligent.devcore.gateway.StorageProvider;

import java.io.InputStream;

public class MockStorageProvider implements StorageProvider {

    @Override
    public String saveFiles(InputStream inputStream, String secureFileName){
        System.out.println("save file" + secureFileName);
        return "/mock/storage/path"+secureFileName;

    }
}
