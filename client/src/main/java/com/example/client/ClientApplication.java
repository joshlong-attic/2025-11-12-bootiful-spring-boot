package com.example.client;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    McpSyncClient schedulerMcpClient() {
        var mcp = McpClient
                .sync(HttpClientSseClientTransport.builder("http://localhost:8081").build())
                .build();
        mcp.initialize();
        return mcp;
    }

    @Bean
    ApplicationRunner runner(
            McpSyncClient schedulerMcpClient, ChatClient.Builder builder) {
        return _ -> {
            var chatClient = builder
                    .defaultSystem("""
                            You are an AI powered assistant to help people adopt a dog from the adoption\s
                            agency named Pooch Palace with locations in Antwerp, Seoul, Tokyo, Singapore, Paris,\s
                            Mumbai, New Delhi, Barcelona, San Francisco, and London. Information about the dogs available\s
                            will be presented below. If there is no information, then return a polite response suggesting we\s
                            don't have any dogs available.
                            """)
                    .defaultToolCallbacks(new SyncMcpToolCallbackProvider(schedulerMcpClient))
                    .build();

            var content = chatClient
                    .prompt("when might I pick up Prancer, id 45 the dog from the San Francisco Pooch Palace location?")
                    .call()
                    .content();
            IO.println("result: " + content);

        };
    }
}
