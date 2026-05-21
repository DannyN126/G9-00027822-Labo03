package com.example.labo3.services.impl;

import com.example.labo3.dto.request.CreateSpecimenRequest;
import com.example.labo3.dto.request.UpdateSpecimenRequest;
import com.example.labo3.dto.response.PageableResponse;
import com.example.labo3.dto.response.SpecimenResponse;
import com.example.labo3.entities.Specimen;
import com.example.labo3.exceptions.ResourceNotFoundException;
import com.example.labo3.mappers.SpecimenMapper;
import com.example.labo3.repositories.SpecimenRepository;
import com.example.labo3.services.SpecimenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpecimenServiceImpl implements SpecimenService {

    private final SpecimenRepository specimenRepository;
    private final SpecimenMapper specimenMapper;

    @Override
    @Transactional
    public SpecimenResponse createSpecimen(CreateSpecimenRequest request) {
        Specimen specimen = specimenMapper.toEntityCreate(request);
        Specimen savedSpecimen = specimenRepository.save(specimen);

        return specimenMapper.toDto(savedSpecimen);
    }

    @Override
    public PageableResponse<SpecimenResponse> getAllSpecimens(
            int page,
            int size,
            String sortBy,
            String sortOrder
    ) {
        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Specimen> specimenPage = specimenRepository.findAll(pageable);

        if (specimenPage.isEmpty()) {
            throw new ResourceNotFoundException("No specimens are registered in Hyrule");
        }

        return specimenMapper.toPageableResponse(specimenPage);
    }

    @Override
    public SpecimenResponse getSpecimenById(UUID id) {
        Specimen specimen = specimenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Specimen not found in Sheikah Slate records"
                ));

        return specimenMapper.toDto(specimen);
    }

    @Override
    @Transactional
    public SpecimenResponse updateSpecimen(UUID id, UpdateSpecimenRequest request) {
        this.getSpecimenById(id);

        Specimen specimenToUpdate = specimenMapper.toEntityUpdate(request, id);
        Specimen updatedSpecimen = specimenRepository.save(specimenToUpdate);

        return specimenMapper.toDto(updatedSpecimen);
    }

    @Override
    @Transactional
    public SpecimenResponse deleteSpecimen(UUID id) {
        SpecimenResponse existingSpecimen = this.getSpecimenById(id);

        specimenRepository.deleteById(id);

        return existingSpecimen;
    }
}