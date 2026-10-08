package cl.duoc.jv0101.foodgo.restaurantes.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.restaurantes.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    @Override
    @EntityGraph(attributePaths = "menuitems")
    List<Restaurante> findAll();

    @Override
    @EntityGraph(attributePaths = "menuitems")
    Optional<Restaurante> findById(Long id);
}
