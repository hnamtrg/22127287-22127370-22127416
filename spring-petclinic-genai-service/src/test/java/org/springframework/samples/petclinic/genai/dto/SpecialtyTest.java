package org.springframework.samples.petclinic.genai.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpecialtyTest {

    @Test
    public void testConstructorAndGetters() {
        Specialty specialty = new Specialty(1, "Surgery");

        assertEquals(1, specialty.id());
        assertEquals("Surgery", specialty.name());
    }

    @Test
    public void testEqualsAndHashCode() {
        Specialty specialty1 = new Specialty(1, "Surgery");
        Specialty specialty2 = new Specialty(1, "Surgery");
        Specialty specialty3 = new Specialty(2, "Dentistry");

        assertEquals(specialty1, specialty2);
        assertNotEquals(specialty1, specialty3);
        assertEquals(specialty1.hashCode(), specialty2.hashCode());
    }

    @Test
    public void testToString() {
        Specialty specialty = new Specialty(1, "Surgery");

        String result = specialty.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("name=Surgery"));
    }

    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Specialty specialty = new Specialty(1, "Surgery");
        String json = objectMapper.writeValueAsString(specialty);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"Surgery\""));
    }
}
