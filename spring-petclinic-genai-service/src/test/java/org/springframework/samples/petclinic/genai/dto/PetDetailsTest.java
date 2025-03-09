package org.springframework.samples.petclinic.genai.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PetDetailsTest {

    @Test
    public void testConstructorAndGetters() {
        // ✅ Sửa constructor PetType
        PetType type = new PetType("Dog");

        // ✅ Sửa constructor VisitDetails (thêm petId)
        VisitDetails visit1 = new VisitDetails(1, 1, "2024-03-01", "Routine checkup");
        VisitDetails visit2 = new VisitDetails(2, 1, "2024-04-01", "Vaccination");

        PetDetails pet = new PetDetails(1, "Buddy", "2023-06-15", type, List.of(visit1, visit2));

        assertEquals(1, pet.id());
        assertEquals("Buddy", pet.name());
        assertEquals("2023-06-15", pet.birthDate());
        assertEquals(type, pet.type());
        assertEquals(2, pet.visits().size());
    }

    @Test
    public void testEqualsAndHashCode() {
        PetType type = new PetType("Dog");
        PetDetails pet1 = new PetDetails(1, "Buddy", "2023-06-15", type, List.of());
        PetDetails pet2 = new PetDetails(1, "Buddy", "2023-06-15", type, List.of());
        PetDetails pet3 = new PetDetails(2, "Charlie", "2023-07-10", type, List.of());

        assertEquals(pet1, pet2);
        assertNotEquals(pet1, pet3);
        assertEquals(pet1.hashCode(), pet2.hashCode());
    }

    @Test
    public void testToString() {
        PetType type = new PetType("Dog");
        PetDetails pet = new PetDetails(1, "Buddy", "2023-06-15", type, List.of());

        String expected = "PetDetails[id=1, name=Buddy, birthDate=2023-06-15, type=PetType[name=Dog], visits=[]]";
        assertEquals(expected, pet.toString());
    }
}
