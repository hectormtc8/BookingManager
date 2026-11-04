package com.project.bookingmanager.service;

import com.project.bookingmanager.exception.ResourceNotFoundException;
import com.project.bookingmanager.model.Resource;
import com.project.bookingmanager.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public List<Resource> getAllActive() {
        return resourceRepository.findByActiveTrue();
    }

    public Resource create(Resource resource) {
        resource.setActive(true);
        return resourceRepository.save(resource);
    }

    public Resource update(Long id, Resource updated) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada"));

        resource.setName(updated.getName());
        resource.setDescription(updated.getDescription());
        resource.setCapacity(updated.getCapacity());
        return resourceRepository.save(resource);
    }

    public void deactivate(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada"));
        resource.setActive(false);
        resourceRepository.save(resource);
    }
}