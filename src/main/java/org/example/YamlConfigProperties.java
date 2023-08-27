package org.example;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "myapp")
public class YamlConfigProperties {
    private String remoteUrl;
    private String baseUrl;
    private String post;

    private String get;

}
