package cl.duoc.jv0101.foodgo.restaurantes;

import org.junit.jupiter.api.Test;
import cl.duoc.jv0101.foodgo.restaurantes.config.OpenApiConfig;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void beanOpenApiGenerado() {
        assertThat(new OpenApiConfig().customOpenAPI()).isNotNull();
    }
}
