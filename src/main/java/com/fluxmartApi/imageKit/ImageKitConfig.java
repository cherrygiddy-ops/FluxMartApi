package com.fluxmartApi.imageKit;

import org.springframework.stereotype.Service;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
@Service
public class ImageKitConfig {

        @Value("${imagekit.urlEndpoint}")
        private String urlEndpoint;

        @Value("${imagekit.publicKey}")
        private String publicKey;

        @Value("${imagekit.privateKey}")
        private String privateKey;

        @Bean
        public ImageKit imageKit() {
            ImageKit imageKit = ImageKit.getInstance();
            Configuration config = new Configuration(publicKey, privateKey, urlEndpoint);
            imageKit.setConfig(config);
            return imageKit;
        }

}
