package cl.duoc.jv0101.foodgo.restaurantes.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.jv0101.foodgo.restaurantes.exception.ResourceNotFoundException;
import cl.duoc.jv0101.foodgo.restaurantes.model.ItemMenu;
import cl.duoc.jv0101.foodgo.restaurantes.model.Restaurante;
import cl.duoc.jv0101.foodgo.restaurantes.repository.ItemMenuRepository;
import cl.duoc.jv0101.foodgo.restaurantes.repository.RestauranteRepository;

@Service
@Transactional
public class ItemMenuService {

    private final ItemMenuRepository repository;
    private final RestauranteRepository restauranteRepository;

    public ItemMenuService(ItemMenuRepository repository, RestauranteRepository restauranteRepository) {
        this.repository = repository;
        this.restauranteRepository = restauranteRepository;
    }

    @Transactional(readOnly = true)
    public List<ItemMenu> findByRestauranteId(Long restauranteId) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new ResourceNotFoundException("Restaurante no encontrado con id " + restauranteId);
        }
        return repository.findByRestaurante_Id(restauranteId);
    }

    @Transactional(readOnly = true)
    public ItemMenu findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemMenu no encontrado con id " + id));
    }

    public ItemMenu create(Long restauranteId, ItemMenu recurso) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado con id " + restauranteId));
        recurso.setId(null);
        recurso.setRestaurante(restaurante);
        return repository.save(recurso);
    }

    public ItemMenu update(Long id, ItemMenu datos) {
        ItemMenu existente = findById(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecio(datos.getPrecio());
        existente.setDisponible(datos.isDisponible());
        return repository.save(existente);
    }

    public void delete(Long id) {
        ItemMenu existente = findById(id);
        repository.delete(existente);
    }
}
