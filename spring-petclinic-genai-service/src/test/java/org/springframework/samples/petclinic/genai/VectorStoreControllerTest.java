package org.springframework.samples.petclinic.genai;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.samples.petclinic.genai.dto.Specialty;
import org.springframework.samples.petclinic.genai.dto.Vet;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class VectorStoreControllerTest {

    @Mock
    private SimpleVectorStore vectorStore;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private ClassPathResource classPathResource;

    @Mock
    private Resource vectorStoreResource;  // Inject mock resource

    @InjectMocks
    private VectorStoreController vectorStoreController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
    }

    // @Test
    // void testLoadVetDataToVectorStoreOnStartup_FileExists() throws IOException {
    //     // Tạo file giả trong thư mục tạm
    //     File tempFile = File.createTempFile("vectorstore", ".json");

    //     // Dùng Spy thay vì Mock để có thể can thiệp vào getFile()
    //     ClassPathResource spyResource = spy(new ClassPathResource("vectorstore.json"));
    //     doReturn(true).when(spyResource).exists();
    //     doReturn(tempFile).when(spyResource).getFile();

    //     // Gọi phương thức cần test
    //     vectorStoreController.loadVetDataToVectorStoreOnStartup(mock(ApplicationStartedEvent.class));

    //     // Xác nhận rằng vectorStore.load() đã được gọi đúng 1 lần với file giả
    //     verify(vectorStore, times(1)).load(tempFile);
    // }
    @SuppressWarnings("unchecked")
    @Test
    void testLoadVetDataToVectorStoreOnStartup_FileDoesNotExist() throws IOException {
        // Mock resource không tồn tại
        ClassPathResource resource = mock(ClassPathResource.class);
        lenient().when(resource.exists()).thenReturn(false);

        // Mock WebClient
        WebClient.RequestHeadersUriSpec<?> requestMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseMock = mock(WebClient.ResponseSpec.class);

        webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);

        lenient().when(webClient.get().uri(anyString()).retrieve()).thenReturn(responseMock);

        lenient().when(responseMock.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(List.of()));

        // Kiểm tra khi file không tồn tại
        vectorStoreController.loadVetDataToVectorStoreOnStartup(mock(ApplicationStartedEvent.class));

        // Verify webClient được gọi
        verify(webClient, times(1)).get();
    }

    @Test
    void testConvertListToJsonResource_Success() {
        // Tạo danh sách Vet
        Specialty specialty = new Specialty(1, "Surgery");
        Vet vet = new Vet(1, "John", "Doe", Set.of(specialty));
        List<Vet> vets = List.of(vet);

        // Chạy method
        ByteArrayResource resource = (ByteArrayResource) vectorStoreController.convertListToJsonResource(vets);

        // Kiểm tra kết quả không null
        assertNotNull(resource);
        assertTrue(resource.getByteArray().length > 0);
    }

    @Test
    void testConvertListToJsonResource_EmptyList() {
        // Chạy method với danh sách rỗng
        ByteArrayResource resource = (ByteArrayResource) vectorStoreController.convertListToJsonResource(Collections.emptyList());

        // Kiểm tra resource có dữ liệu JSON của danh sách rỗng
        assertNotNull(resource);
        assertEquals("[]", new String(resource.getByteArray()));
    }

    @Test
    void testConvertListToJsonResource_NullInput() throws JsonProcessingException {
        // Chạy method với giá trị null
        ByteArrayResource resource = (ByteArrayResource) vectorStoreController.convertListToJsonResource(null);

        // Kiểm tra resource có giá trị JSON null
        assertNotNull(resource);
        assertEquals("null", new String(resource.getByteArray()));
    }
}
