package org.springframework.samples.petclinic.visits.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class VisitRepositoryTest {

    @Autowired
    private VisitRepository visitRepository;

    private Visit visit1, visit2;

    @BeforeEach
    void setUp() {
        visit1 = new Visit();
        visit1.setPetId(1);
        visit1.setDescription("Regular Checkup");
        visitRepository.save(visit1);

        visit2 = new Visit();
        visit2.setPetId(2);
        visit2.setDescription("Vaccination");
        visitRepository.save(visit2);
    }

    @Test
    void shouldFindByPetId() {
        List<Visit> visits = visitRepository.findByPetId(1);
        assertThat(visits).isNotEmpty();
        assertThat(visits.get(0).getDescription()).isEqualTo("Regular Checkup");
    }

    @Test
    void shouldFindByPetIdIn() {
        List<Visit> visits = visitRepository.findByPetIdIn(Arrays.asList(1, 2));
        assertThat(visits).hasSize(2);
    }
}
