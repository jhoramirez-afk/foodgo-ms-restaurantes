package cl.duoc.jv0101.foodgo.restaurantes.controller;

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
import cl.duoc.jv0101.foodgo.restaurantes.model.ItemMenu;
import cl.duoc.jv0101.foodgo.restaurantes.service.ItemMenuService;

@RestController
@RequestMapping("/api")
public class ItemMenuController {

    private final ItemMenuService service;

    public ItemMenuController(ItemMenuService service) {
        this.service = service;
    }

    @GetMapping("/restaurantes/{restauranteId}/menu-items")
    public ResponseEntity<List<ItemMenu>> listarPorRestaurante(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(service.findByRestauranteId(restauranteId));
    }

    @PostMapping("/restaurantes/{restauranteId}/menu-items")
    public ResponseEntity<ItemMenu> crear(@PathVariable Long restauranteId, @Valid @RequestBody ItemMenu recurso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(restauranteId, recurso));
    }

    @GetMapping("/menu-items/{id}")
    public ResponseEntity<ItemMenu> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/menu-items/{id}")
    public ResponseEntity<ItemMenu> actualizar(@PathVariable Long id, @Valid @RequestBody ItemMenu datos) {
        return ResponseEntity.ok(service.update(id, datos));
    }

    @DeleteMapping("/menu-items/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
