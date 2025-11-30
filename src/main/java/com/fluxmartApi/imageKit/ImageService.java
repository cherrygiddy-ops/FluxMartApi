package com.fluxmartApi.imageKit;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    private final ImageKit imageKit;

    public ImageService(ImageKit imageKit) {
        this.imageKit = imageKit;
    }

    public String uploadImage(byte[] fileBytes, String fileName) throws Exception {
        FileCreateRequest request = new FileCreateRequest(fileBytes, fileName);
        Result result = imageKit.upload(request);
        return result.getUrl(); // CDN URL to store in DB
    }
}
