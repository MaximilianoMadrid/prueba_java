package ms_orders.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SqsConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    public SqsClient sqsClient() {
        // En EC2 (AWS Academy), toma las credenciales automáticamente
        // desde el rol de instancia (IMDS). No hace falta hardcodear nada.
        return SqsClient.builder()
                .region(Region.of(region))
                .build();
    }
}