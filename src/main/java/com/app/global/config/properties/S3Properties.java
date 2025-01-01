package com.app.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties("aws.s3")
public class S3Properties {

    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private String bucket;
}
