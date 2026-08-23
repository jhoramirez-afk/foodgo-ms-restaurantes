# language: es
Característica: Servicio Restaurante (microservicio restaurantes del caso FoodGo)
  Los escenarios validan el contrato REST del microservicio alineado a sus endpoints.

  Escenario: el listado del recurso responde 200
    Dado el servicio "Restaurante" está disponible
    Cuando consulto el listado de "restaurantes"
    Entonces el listado responde con código 200

  Escenario: ciclo de vida completo del recurso
    Dado un nuevo "restaurante" con nombre "hola-cucumber"
    Cuando consulto el "restaurante" recién creado
    Entonces el recurso tiene nombre "hola-cucumber" y código 200
    Cuando actualizo el "restaurante" con nombre "cucumber-actualizado"
    Entonces el recurso queda con nombre "cucumber-actualizado" y código 200
    Cuando elimino el "restaurante"
    Entonces la eliminación responde con código 204
    Y al consultar el "restaurante" eliminado responde 404
