# API REST: restaurantes

Base local: http://localhost:8082/api. Swagger UI: http://localhost:8082/swagger-ui/index.html.

| Método | Ruta | HTTP de éxito |
|---|---|---:|
| POST | /restaurantes | 201 |
| GET | /restaurantes | 200 |
| GET | /restaurantes/{id} | 200 |
| PUT | /restaurantes/{id} | 200 |
| DELETE | /restaurantes/{id} | 204 |
| POST | /restaurantes/{id}/menu-items | 201 |
| GET | /restaurantes/{id}/menu-items | 200 |
| GET | /menu-items/{id} | 200 |
| PUT | /menu-items/{id} | 200 |
| DELETE | /menu-items/{id} | 204 |

## Crear entidad principal

```json
{
  "nombre": "La Cocina de Barrio",
  "categoria": "Hamburguesas",
  "direccion": "Manuel Montt 820, Providencia"
}
```

## Crear entidad relacionada

```json
{
  "nombre": "Hamburguesa de vacuno con papas",
  "descripcion": "Pan brioche, vacuno, queso, tomate y papas rústicas.",
  "precio": 9990,
  "disponible": true
}
```

Usar el ID retornado por la creación del padre. Los ID son generados por la BD. Editar los hijos mediante sus propias rutas. Ver las reglas y los campos calculados en REGLAS_EP02.md.

Errores: 400 para datos o JSON inválidos; 404 para recurso/relación local inexistente; 409 para conflictos de integridad o unicidad cuando corresponda. Un campo demasiado largo devuelve 400. Los mensajes y validationErrors se entregan mediante ApiExceptionHandler.
