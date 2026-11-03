package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.mappers.VetMapper;
import com.tecsup.petclinic.repositories.VetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final VetMapper vetMapper;

    @Override
    public VetDTO create(VetDTO vetDTO) {
        Vet newVet = vetRepository.save(vetMapper.mapToEntity(vetDTO));
        return vetMapper.mapToDto(newVet);
    }

    @Override
    public VetDTO update(VetDTO vetDTO) {
        Vet updatedVet = vetRepository.save(vetMapper.mapToEntity(vetDTO));
        return vetMapper.mapToDto(updatedVet);
    }

    @Override
    public void delete(Integer id) throws VetNotFoundException {
        VetDTO vet = findById(id);
        vetRepository.delete(vetMapper.mapToEntity(vet));
    }

    @Override
    public VetDTO findById(Integer id) throws VetNotFoundException {
        Optional<Vet> vet = vetRepository.findById(id);

        if (vet.isEmpty())
            throw new VetNotFoundException("Vet not found...!");

        return vetMapper.mapToDto(vet.get());
    }

    @Override
    public List<VetDTO> findAll() {
        return vetRepository.findAll()
                .stream()
                .map(vetMapper::mapToDto)
                .toList();
    }
}