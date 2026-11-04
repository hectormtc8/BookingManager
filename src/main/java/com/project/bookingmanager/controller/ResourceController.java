package com.project.bookingmanager.controller;

import com.project.bookingmanager.model.Resource;
import com.project.bookingmanager.service.ResourceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Recursos", description = "Crear, modificar y desactivar salas")
@SecurityRequirement(name = "bearerAuth")
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    public ResponseEntity<List<Resource>> getAll() {
        return ResponseEntity.ok(resourceService.getAllActive());
    }

    @PostMapping
    public ResponseEntity<Resource> create(@Valid @RequestBody Resource resource) {
        return ResponseEntity.ok(resourceService.create(resource));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource> update(@Valid @PathVariable Long id, @RequestBody Resource resource) {
        return ResponseEntity.ok(resourceService.update(id, resource));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@Valid @PathVariable Long id) {
        resourceService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}