package org.springframework.samples.petclinic.genai.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VetTest {

    @Test
    public void testConstructorAndGetter() {
        Specialty surgery = new Specialty(1, "Surgery");
        Specialty dentistry = new Specialty(2, "Dentistry");

        Vet vet = new Vet(1, "John", "Doe", Set.of(surgery, dentistry));

        assertEquals(1, vet.id());
        assertEquals("John", vet.firstName());
        assertEquals("Doe", vet.lastName());
        assertEquals(2, vet.specialties().size());
    }

    @Test
    public void testEqualsAndHashCode() {
        Specialty surgery = new Specialty(1, "Surgery");
        Specialty dentistry = new Specialty(2, "Dentistry");

        Vet vet1 = new Vet(1, "John", "Doe", Set.of(surgery, dentistry));
        Vet vet2 = new Vet(1, "John", "Doe", Set.of(surgery, dentistry));
        Vet vet3 = new Vet(2, "Jane", "Smith", Set.of(surgery));

        assertEquals(vet1, vet2);
        assertNotEquals(vet1, vet3);
        assertEquals(vet1.hashCode(), vet2.hashCode());
    }

    @Test
    public void testToString() {
        Specialty surgery = new Specialty(1, "Surgery");
        Vet vet = new Vet(1, "John", "Doe", Set.of(surgery));

        String result = vet.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("firstName=John"));
        assertTrue(result.contains("lastName=Doe"));
        assertTrue(result.contains("specialties=[Specialty"));
    }

    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Specialty surgery = new Specialty(1, "Surgery");

        Vet vet = new Vet(1, "John", "Doe", Set.of(surgery));
        String json = objectMapper.writeValueAsString(vet);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"firstName\":\"John\""));
        assertTrue(json.contains("\"lastName\":\"Doe\""));
        assertTrue(json.contains("\"specialties\":["));
    }
}
