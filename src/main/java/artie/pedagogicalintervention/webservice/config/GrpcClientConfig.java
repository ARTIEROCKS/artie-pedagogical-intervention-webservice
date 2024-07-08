package artie.pedagogicalintervention.webservice.config;

import net.devh.boot.grpc.client.config.GrpcChannelsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
    @Bean
    public GrpcChannelsProperties grpcChannelsProperties() {
        return new GrpcChannelsProperties();
    }
}
