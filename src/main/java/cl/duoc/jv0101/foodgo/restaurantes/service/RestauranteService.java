package cl.duoc.jv0101.foodgo.restaurantes.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.jv0101.foodgo.restaurantes.model.Restaurante;
import cl.duoc.jv0101.foodgo.restaurantes.repository.RestauranteRepository;

@Service
@Transactional
public class RestauranteService {

    private final RestauranteRepository repository;

    public RestauranteService(RestauranteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Restaurante> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Restaurante> findById(Long id) {
        return repository.findById(id);
    }

    public Restaurante create(Restaurante recurso) {
        recurso.setId(null);
        return repository.save(recurso);
    }

    public Optional<Restaurante> update(Long id, Restaurante datos) {
        return repository.findById(id).map(existente -> {
            existente.setNombre(datos.getNombre());
            existente.setCategoria(datos.getCategoria());
            existente.setDireccion(datos.getDireccion());
            return repository.save(existente);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            return true;
        }).orElse(false);
    }
}
