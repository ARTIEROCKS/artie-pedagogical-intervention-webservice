package artie.pedagogicalintervention.webservice.config;

import artie.pedagogicalintervention.webservice.stubs.ChatGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatGrpcClientConfiguration {

    @GrpcClient("chat")
    private ChatGrpc.ChatBlockingStub chatClient;

    @Bean
    public ChatGrpc.ChatBlockingStub chatBlockingStub() {
        return chatClient;
    }
}