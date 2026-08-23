# Restaurante — Contrato de la API REST

## Base

- **Base path**: `/api/restaurantes`
- **Formato**: JSON — **Puerto**: 8082 (configurable con `PORT`)

## Recursos

| Método | Ruta | Códigos de estado | Descripción |
|--------|------|-------------------|-------------|
| GET | `/api/restaurantes` | 200 | Lista todos los recursos |
| GET | `/api/restaurantes/{id}` | 200 / 404 | Obtiene un recurso por id |
| POST | `/api/restaurantes` | 201 / 400 | Crea un recurso |
| PUT | `/api/restaurantes/{id}` | 200 / 404 / 400 | Actualiza un recurso |
| DELETE | `/api/restaurantes/{id}` | 204 / 404 | Elimina un recurso |

## Atributos de un recurso

| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| id | Long | - | Identificador autogenerado |
| nombre | String | Sí | Campo principal del recurso |
| categoria | String | No | Campo del dominio |
| direccion | String | No | Campo del dominio |

## Ejemplos con curl

```bash
# Listar
curl http://localhost:8082/api/restaurantes

# Crear
curl -X POST http://localhost:8082/api/restaurantes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Demo"}'

# Obtener por id
curl http://localhost:8082/api/restaurantes/1

# Actualizar
curl -X PUT http://localhost:8082/api/restaurantes/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Actualizado"}'

# Eliminar
curl -X DELETE http://localhost:8082/api/restaurantes/1
```
