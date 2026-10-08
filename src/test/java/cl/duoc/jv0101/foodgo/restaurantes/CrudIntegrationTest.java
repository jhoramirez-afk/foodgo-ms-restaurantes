package cl.duoc.jv0101.foodgo.restaurantes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrudIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired cl.duoc.jv0101.foodgo.restaurantes.repository.ItemMenuRepository children;

    @Test
    void crudRelationsAndCascadeThroughHttp() throws Exception {
        String created = mvc.perform(post("/api/restaurantes").contentType("application/json")
                .content("""
{"nombre": "Restaurante EP02", "categoria": "Comida", "direccion": "Av. Principal 123"}
"""))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long parentId = mapper.readTree(created).get("id").asLong();
        String nested = "/api/restaurantes/%s/menu-items".formatted(parentId);
        String child = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"nombre": "Ejemplo", "descripcion": "Producto de ejemplo", "precio": 9990.0, "disponible": true}
"""))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long childId = mapper.readTree(child).get("id").asLong();
        mvc.perform(get("/api/restaurantes/" + parentId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.menuitems[0].id").value(childId));
        mvc.perform(get("/api/restaurantes")).andExpect(status().isOk());
        mvc.perform(get(nested)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(childId));
        mvc.perform(get("/api/menu-items/" + childId)).andExpect(status().isOk());
        mvc.perform(put("/api/restaurantes/" + parentId).contentType("application/json")
                .content("""
{"nombre": "Restaurante EP02 actualizado", "categoria": "Comida", "direccion": "Av. Principal 123"}
""")).andExpect(status().isOk());
        mvc.perform(put("/api/menu-items/" + childId).contentType("application/json")
                .content("""
{"nombre": "Ejemplo", "descripcion": "Producto de ejemplo", "precio": 9990.0, "disponible": true}
""")).andExpect(status().isOk());
        mvc.perform(delete("/api/menu-items/" + childId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/menu-items/" + childId)).andExpect(status().isNotFound());
        String second = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"nombre": "Ejemplo", "descripcion": "Producto de ejemplo", "precio": 9990.0, "disponible": true}
"""))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long cascadeId = mapper.readTree(second).get("id").asLong();
        mvc.perform(delete("/api/restaurantes/" + parentId)).andExpect(status().isNoContent());
        assertThat(children.existsById(cascadeId)).isFalse();
        mvc.perform(get("/api/restaurantes/" + parentId)).andExpect(status().isNotFound());
    }

    @Test
    void malformedJsonAndIdsReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/restaurantes").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/restaurantes/no-es-numero"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void databaseConstraintReturnsConflict() throws Exception {
        mvc.perform(post("/api/restaurantes").contentType("application/json")
                .content("""
{"nombre": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", "categoria": "Comida", "direccion": "Av. Principal 123"}
"""))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void validationAndMissingResourcesReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/restaurantes").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/restaurantes/9223372036854775807"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        mvc.perform(post("/api/restaurantes/%s/menu-items".formatted(Long.MAX_VALUE)).contentType("application/json")
                .content("""
{"nombre": "Ejemplo", "descripcion": "Producto de ejemplo", "precio": 9990.0, "disponible": true}
""")).andExpect(status().isNotFound());
    }
}
