package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(classes = {AIBeanConfiguration.class})
public class AIBeanConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private EmbeddingModel embeddingModel;

    @Test
    public void testChatMemoryBeanCreation() {
        ChatMemory chatMemory = applicationContext.getBean(ChatMemory.class);
        
        assertNotNull(chatMemory, "ChatMemory bean should not be null");
        assertTrue(chatMemory instanceof InMemoryChatMemory, 
                "ChatMemory bean should be an instance of InMemoryChatMemory");
    }

    @Test
    public void testVectorStoreBeanCreation() {
        VectorStore vectorStore = applicationContext.getBean(VectorStore.class);
        
        assertNotNull(vectorStore, "VectorStore bean should not be null");
        assertTrue(vectorStore instanceof SimpleVectorStore, 
                "VectorStore bean should be an instance of SimpleVectorStore");
    }

    @Test
    public void testWebClientBuilderBeanCreation() {
        WebClient.Builder webClientBuilder = applicationContext.getBean(WebClient.Builder.class);
        
        assertNotNull(webClientBuilder, "WebClient.Builder bean should not be null");
    }

    @Test
    public void testChatMemoryDirectCall() {
        AIBeanConfiguration configuration = new AIBeanConfiguration();
        ChatMemory chatMemory = configuration.chatMemory();
        
        assertNotNull(chatMemory, "ChatMemory created directly should not be null");
        assertTrue(chatMemory instanceof InMemoryChatMemory, 
                "ChatMemory created directly should be an instance of InMemoryChatMemory");
    }

    @Test
    public void testVectorStoreDirectCall() {
        AIBeanConfiguration configuration = new AIBeanConfiguration();
        VectorStore vectorStore = configuration.vectorStore(embeddingModel);
        
        assertNotNull(vectorStore, "VectorStore created directly should not be null");
        assertTrue(vectorStore instanceof SimpleVectorStore, 
                "VectorStore created directly should be an instance of SimpleVectorStore");
    }

    @Test
    public void testWebClientBuilderDirectCall() {
        AIBeanConfiguration configuration = new AIBeanConfiguration();
        WebClient.Builder webClientBuilder = configuration.loadBalancedWebClientBuilder();
        
        assertNotNull(webClientBuilder, "WebClient.Builder created directly should not be null");
    }
}