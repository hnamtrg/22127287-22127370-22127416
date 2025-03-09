package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import java.util.function.Consumer;
import org.mockito.ArgumentMatchers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.ArgumentCaptor;

public class PetclinicChatClientTest {

    private PetclinicChatClient petclinicChatClient;
    private ChatClient mockChatClient;
    private ChatClient.Builder mockBuilder;
    private ChatMemory mockChatMemory;
    private ChatClient.ChatClientRequestSpec mockRequestSpec;
    private ChatClient.CallResponseSpec mockCallResponseSpec;

    @BeforeEach
    public void setUp() {
        mockBuilder = mock(ChatClient.Builder.class);
        mockChatClient = mock(ChatClient.class);
        mockChatMemory = mock(ChatMemory.class);
        mockRequestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        mockCallResponseSpec = mock(ChatClient.CallResponseSpec.class);

        // Mock chuỗi gọi builder
        when(mockBuilder.defaultSystem(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.defaultAdvisors(any(MessageChatMemoryAdvisor.class), any(SimpleLoggerAdvisor.class))).thenReturn(mockBuilder);
        when(mockBuilder.defaultFunctions(eq("listOwners"), eq("addOwnerToPetclinic"), eq("addPetToOwner"), eq("listVets"))).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockChatClient);

        // Mock request spec cơ bản
        when(mockChatClient.prompt()).thenReturn(mockRequestSpec);
        when(mockRequestSpec.user(ArgumentMatchers.any(Consumer.class))).thenReturn(mockRequestSpec);

        // Khởi tạo PetclinicChatClient
        petclinicChatClient = new PetclinicChatClient(mockBuilder, mockChatMemory);
    }

    @Test
    public void testExchangeSuccess() {
        // Dữ liệu giả lập
        String query = "List all owners";
        String expectedResponse = "Here are all owners...";

        // Mock cho trường hợp thành công
        when(mockRequestSpec.call()).thenReturn(mockCallResponseSpec);
        when(mockCallResponseSpec.content()).thenReturn(expectedResponse);

        // Gọi phương thức
        String result = petclinicChatClient.exchange(query);

        // Kiểm tra kết quả
        assertEquals(expectedResponse, result);
        verify(mockRequestSpec).user(ArgumentMatchers.any(Consumer.class));
    }

    @Test
    public void testExchangeFailure() {
        // Dữ liệu giả lập
        String query = "List all owners";

        // Mock cho trường hợp thất bại
        when(mockRequestSpec.call()).thenThrow(new RuntimeException("LLM error"));

        // Gọi phương thức
        String result = petclinicChatClient.exchange(query);

        // Kiểm tra kết quả
        assertEquals("Chat is currently unavailable. Please try again later.", result);

        // Dùng ArgumentCaptor để kiểm tra nội dung user()
        ArgumentCaptor<Consumer<ChatClient.PromptUserSpec>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(mockRequestSpec).user(captor.capture());

        // Giả lập PromptUserSpec để kiểm tra giá trị truyền vào
        ChatClient.PromptUserSpec mockUserSpec = mock(ChatClient.PromptUserSpec.class);
        captor.getValue().accept(mockUserSpec);

        // Kiểm tra xem giá trị query có đúng không
        verify(mockUserSpec).text(query);
    }
}