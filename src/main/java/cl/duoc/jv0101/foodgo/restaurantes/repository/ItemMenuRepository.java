package cl.duoc.jv0101.foodgo.restaurantes.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.restaurantes.model.ItemMenu;

public interface ItemMenuRepository extends JpaRepository<ItemMenu, Long> {
    List<ItemMenu> findByRestaurante_Id(Long restauranteId);
}
