package com.sunny.ai.embedding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Import;
import com.sunny.ai.common.config.OpenSearchConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
@EnableAsync
@Import(OpenSearchConfig.class)
public class EmbeddingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmbeddingServiceApplication.class, args);
    }
}
