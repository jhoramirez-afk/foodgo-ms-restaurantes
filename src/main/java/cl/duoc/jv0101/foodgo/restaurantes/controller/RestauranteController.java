package cl.duoc.jv0101.foodgo.restaurantes.controller;

import cl.duoc.jv0101.foodgo.restaurantes.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.jv0101.foodgo.restaurantes.model.Restaurante;
import cl.duoc.jv0101.foodgo.restaurantes.service.RestauranteService;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final RestauranteService service;

    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Restaurante>> listar() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurante> obtener(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado con id " + id));
    }

    @PostMapping
    public ResponseEntity<Restaurante> crear(@Valid @RequestBody Restaurante recurso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(recurso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurante> actualizar(@PathVariable Long id,
            @Valid @RequestBody Restaurante datos) {
        return service.update(id, datos).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado con id " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!service.delete(id)) {
            throw new ResourceNotFoundException("Restaurante no encontrado con id " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
