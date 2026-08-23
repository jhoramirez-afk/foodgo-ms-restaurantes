package cl.duoc.jv0101.foodgo.restaurantes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.restaurantes.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
}
