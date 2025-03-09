package org.springframework.samples.petclinic.genai.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PetTypeTest {

    @Test
    public void testConstructorAndGetter() {
        PetType petType = new PetType("Dog");

        assertEquals("Dog", petType.name());
    }

    @Test
    public void testEqualsAndHashCode() {
        PetType type1 = new PetType("Dog");
        PetType type2 = new PetType("Dog");
        PetType type3 = new PetType("Cat");

        assertEquals(type1, type2);
        assertNotEquals(type1, type3);
        assertEquals(type1.hashCode(), type2.hashCode());
    }

    @Test
    public void testToString() {
        PetType petType = new PetType("Dog");

        String result = petType.toString();
        assertTrue(result.contains("name=Dog"));
    }

    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        PetType petType = new PetType("Dog");
        String json = objectMapper.writeValueAsString(petType);

        assertTrue(json.contains("\"name\":\"Dog\""));
    }
}
