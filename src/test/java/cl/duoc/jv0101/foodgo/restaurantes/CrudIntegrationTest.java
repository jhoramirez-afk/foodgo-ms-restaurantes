package cl.duoc.jv0101.foodgo.restaurantes;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrudIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private String unique(String json) { return json.replace("TEST20261009", UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()); }

    private long createParent() throws Exception {
        String result = mvc.perform(post("/api/restaurantes").contentType("application/json")
                .content(unique("""
{"nombre":"La Cocina de Barrio","categoria":"Hamburguesas","direccion":"Manuel Montt 820, Providencia"}
"""))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    private long createChild(String nested) throws Exception {
        String result = mvc.perform(post(nested).contentType("application/json")
                .content("""
{"nombre":"Hamburguesa de vacuno con papas","descripcion":"Pan brioche, vacuno, queso, tomate y papas rústicas.","precio":9990,"disponible":true}
""")).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(result).get("id").asLong();
    }

    @Test
    void crudRelationsAndCascadeThroughHttp() throws Exception {
        long id = createParent();
        String nested = "/api/restaurantes/" + id + "/menu-items";
        long childId = createChild(nested);
        mvc.perform(get("/api/restaurantes/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.menuitems[0].id").value(childId));
        mvc.perform(get("/api/restaurantes")).andExpect(status().isOk());
        mvc.perform(get(nested)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(childId));
        mvc.perform(get("/api/menu-items/" + childId)).andExpect(status().isOk());
        mvc.perform(put("/api/restaurantes/" + id).contentType("application/json").content(unique("""
{"nombre":"La Cocina de Barrio","categoria":"Hamburguesas artesanales","direccion":"Manuel Montt 820, local 2, Providencia"}
"""))).andExpect(status().isOk());
        mvc.perform(put("/api/menu-items/" + childId).contentType("application/json").content("""
{"nombre":"Hamburguesa de vacuno con papas","descripcion":"Pan brioche, vacuno, queso, tomate y papas rústicas. Opción sin cebolla.","precio":9990,"disponible":true}
""")).andExpect(status().isOk());
        mvc.perform(delete("/api/menu-items/" + childId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/menu-items/" + childId)).andExpect(status().isNotFound());
        long cascadeId = createChild(nested);
        mvc.perform(delete("/api/restaurantes/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/restaurantes/" + id)).andExpect(status().isNotFound());
        mvc.perform(get("/api/menu-items/" + cascadeId)).andExpect(status().isNotFound());
    }

    @Test
    void malformedJsonAndIdsReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/restaurantes").contentType("application/json").content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/restaurantes/no-es-numero"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void validationAndMissingResourcesReturnStructuredErrors() throws Exception {
        mvc.perform(post("/api/restaurantes").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors").isNotEmpty());
        mvc.perform(get("/api/restaurantes/9223372036854775807"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        mvc.perform(post("/api/restaurantes/9223372036854775807/menu-items").contentType("application/json")
                .content("""
{"nombre":"Hamburguesa de vacuno con papas","descripcion":"Pan brioche, vacuno, queso, tomate y papas rústicas.","precio":9990,"disponible":true}
""")).andExpect(status().isNotFound());
        var longBody = (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree("""
{"nombre":"La Cocina de Barrio","categoria":"Hamburguesas","direccion":"Manuel Montt 820, Providencia"}
""");
        longBody.put("nombre", "X".repeat(300));
        mvc.perform(post("/api/restaurantes").contentType("application/json").content(longBody.toString())).andExpect(status().isBadRequest());
    }

    static Stream<Arguments> invalidInputs() {
        return Stream.of(
            Arguments.of("Dirección obligatoria", "parent", """
{"nombre":"La Cocina de Barrio","categoria":"Hamburguesas","direccion":""}
""", "direccion"),
            Arguments.of("Precio cero", "child", """
{"nombre":"Hamburguesa de vacuno con papas","descripcion":"Pan brioche, vacuno, queso, tomate y papas rústicas.","precio":0,"disponible":true}
""", "precio"),
            Arguments.of("Precio fraccionario", "child", """
{"nombre":"Hamburguesa de vacuno con papas","descripcion":"Pan brioche, vacuno, queso, tomate y papas rústicas.","precio":9990.5,"disponible":true}
""", "precio"),
            Arguments.of("Disponibilidad obligatoria", "child", """
{"nombre":"Hamburguesa de vacuno con papas","descripcion":"Pan brioche, vacuno, queso, tomate y papas rústicas.","precio":9990,"disponible":null}
""", "disponible")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidInputs")
    void businessValidationReturns400WithField(String name, String target, String body, String field) throws Exception {
        if ("parent".equals(target)) {
            mvc.perform(post("/api/restaurantes").contentType("application/json").content(unique(body)))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors." + field).exists());
        } else {
            long id = createParent();
            mvc.perform(post("/api/restaurantes/" + id + "/menu-items").contentType("application/json").content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors." + field).exists());
            mvc.perform(delete("/api/restaurantes/" + id)).andExpect(status().isNoContent());
        }
    }

}
