package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
public class VetServiceTest {

    @Autowired
    private VetService vetService;

    /**
     * Integrante A - Crear veterinario
     */
    @Test
    public void testCreateVet() {

        String FIRST_NAME = "Gregory";
        String LAST_NAME = "House";
        String EMAIL = "gregory.house@petclinic.com";
        String PHONE = "987654321";

        VetDTO vetDTO = VetDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .phone(PHONE)
                .build();

        VetDTO newVetDTO = this.vetService.create(vetDTO);
        log.info("VET CREATED: {}", newVetDTO);

        assertNotNull(newVetDTO.getId());
        assertEquals(FIRST_NAME, newVetDTO.getFirstName());
        assertEquals(LAST_NAME, newVetDTO.getLastName());
        assertEquals(EMAIL, newVetDTO.getEmail());
        assertEquals(PHONE, newVetDTO.getPhone());
        assertTrue(newVetDTO.getActive());
    }

    /**
     * Integrante A - Actualizar veterinario
     */
    @Test
    public void testUpdateVet() {

        String FIRST_NAME = "Lisa";
        String LAST_NAME = "Cuddy";
        String EMAIL = "lisa.cuddy@petclinic.com";
        String PHONE = "911111111";

        String UP_FIRST_NAME = "Lisa Marie";
        String UP_LAST_NAME = "Cuddy Smith";
        String UP_EMAIL = "lisa.smith@petclinic.com";
        String UP_PHONE = "922222222";

        // ------------ Create ---------------
        VetDTO vetDTO = VetDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .phone(PHONE)
                .build();

        VetDTO vetDTOCreated = this.vetService.create(vetDTO);
        log.info("VET CREATED: {}", vetDTOCreated);

        // ------------ Update ---------------
        vetDTOCreated.setFirstName(UP_FIRST_NAME);
        vetDTOCreated.setLastName(UP_LAST_NAME);
        vetDTOCreated.setEmail(UP_EMAIL);
        vetDTOCreated.setPhone(UP_PHONE);

        VetDTO updatedVetDTO = this.vetService.update(vetDTOCreated);
        log.info("VET UPDATED: {}", updatedVetDTO);

        // ------------ Validation ---------------
        assertEquals(vetDTOCreated.getId(), updatedVetDTO.getId());
        assertEquals(UP_FIRST_NAME, updatedVetDTO.getFirstName());
        assertEquals(UP_LAST_NAME, updatedVetDTO.getLastName());
        assertEquals(UP_EMAIL, updatedVetDTO.getEmail());
        assertEquals(UP_PHONE, updatedVetDTO.getPhone());
    }

    /**
     * Integrante A - Buscar veterinario existente
     */
    @Test
    public void testFindVetById() {

        Integer ID = 1;
        String FIRST_NAME_EXPECTED = "James";
        String LAST_NAME_EXPECTED = "Carter";

        VetDTO vet = null;

        try {
            vet = this.vetService.findById(ID);
        } catch (VetNotFoundException e) {
            fail(e.getMessage());
        }
        log.info("VET FOUND: {}", vet);

        assertEquals(FIRST_NAME_EXPECTED, vet.getFirstName());
        assertEquals(LAST_NAME_EXPECTED, vet.getLastName());
    }

    /**
     * Integrante A - Eliminar veterinario (Caso 1 del laboratorio)
     */
    @Test
    public void testDeleteVet() {

        // ------------ Create ---------------
        VetDTO vetDTO = VetDTO.builder()
                .firstName("James")
                .lastName("Wilson")
                .email("james.wilson@petclinic.com")
                .phone("933333333")
                .build();

        VetDTO newVetDTO = this.vetService.create(vetDTO);
        log.info("VET CREATED: {}", newVetDTO);

        // ------------ Delete ---------------
        try {
            this.vetService.delete(newVetDTO.getId());
        } catch (VetNotFoundException e) {
            fail(e.getMessage());
        }

        // ------------ Validation ---------------
        assertThrows(VetNotFoundException.class,
                () -> this.vetService.findById(newVetDTO.getId()));
    }

    // ---------------- Integrante B : Soft delete y reactivación ----------------

    @Test
    public void testDeactivateVet() throws VetNotFoundException {

        VetDTO deactivated = vetService.deactivate(1);
        log.info("VET DEACTIVATED : {}", deactivated);

        VetDTO found = vetService.findById(1);       // el registro sigue existiendo

        assertNotNull(found);
        assertEquals("Carter", found.getLastName());
        assertFalse(found.getActive());
    }

    @Test
    public void testReactivateVet() throws VetNotFoundException {

        assertFalse(vetService.findById(6).getActive());

        VetDTO reactivated = vetService.reactivate(6);
        log.info("VET REACTIVATED : {}", reactivated);

        assertTrue(vetService.findById(6).getActive());
    }

    @Test
    public void testFindActiveVets() {

        List<VetDTO> vets = vetService.findActiveVets();
        vets.forEach(vet -> log.info("ACTIVE : {}", vet));

        assertFalse(vets.isEmpty());
        assertTrue(vets.stream().allMatch(VetDTO::getActive));
        assertTrue(vets.stream().noneMatch(vet -> vet.getId() == 6));
    }
}