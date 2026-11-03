package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.repositories.VetRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Pruebas de VetService con el repositorio simulado (Mockito).
 */
@SpringBootTest
@Slf4j
public class VetServiceMockitoTest {

    @Autowired
    private VetService vetService;

    @MockitoBean
    private VetRepository repository;

    private Vet vet(int id, String lastName, boolean active) {
        return new Vet(id, "Name", lastName, "vet@petclinic.com", "6085550000", active);
    }

    // ---------------- Integrante B : Soft delete y reactivación ----------------

    @Test
    public void testDeactivateVet() throws VetNotFoundException {

        Vet carter = vet(1, "Carter", true);
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(carter));
        Mockito.when(repository.save(any(Vet.class))).thenAnswer(inv -> inv.getArgument(0));

        VetDTO result = vetService.deactivate(1);
        log.info("VET DEACTIVATED (MOCK) : {}", result);

        assertFalse(result.getActive());
        Mockito.verify(repository).save(carter);                      // se actualiza
        Mockito.verify(repository, Mockito.never()).delete(any());    // no se borra
    }

    @Test
    public void testReactivateVet() throws VetNotFoundException {

        Mockito.when(repository.findById(6)).thenReturn(Optional.of(vet(6, "Jenkins", false)));
        Mockito.when(repository.save(any(Vet.class))).thenAnswer(inv -> inv.getArgument(0));

        VetDTO result = vetService.reactivate(6);
        log.info("VET REACTIVATED (MOCK) : {}", result);

        assertTrue(result.getActive());
    }

    @Test
    public void testFindActiveVets() {

        Mockito.when(repository.findByActiveTrue())
                .thenReturn(List.of(vet(1, "Carter", true), vet(2, "Leary", true)));

        List<VetDTO> vets = vetService.findActiveVets();
        vets.forEach(vet -> log.info("ACTIVE (MOCK) : {}", vet));

        assertEquals(2, vets.size());
        assertTrue(vets.stream().allMatch(VetDTO::getActive));
    }
}