# DialogFlow en Android

Este proyecto es para probar la tecnología de DialogFlow basada en IA del lenguaje natural.

## MainActivity

Se encarga del flujo general de la aplicación, contiene funciones para las siguientes acciones:

- Escribir los mensajes de usuario y DF en pantalla.
- Hablar y escuchar al usuario.
- Guardar en calendario un evento.
- Mostar una pagina de wikipedia.
- Llamar a un contacto de la agenda.

## Carpeta: models

Reune los modelos para interactuar con los datos de los diferentes origenes, DF, estimador de coches, servicio de citas.

- Cita: Contiene los campos basicos de la cita.
- Coche: Contiene los datos de respuesta del servicio de coches. *No todos, hay algunos que no se usan y no se han mapeado.
- Saved: Es un booleano para comprobar si la operacion se realizo correctamente.
- **DialogFlowIntent:** Esta clase contiene todos los posibles campos relacionados con el cliente y con la funcionalidad local de DF. De manera que es casi como dos modelos en uno.

## Carpeta: dialogflow

Con una unica clase se centraliza toda la gestion de los intents de DF y la accion de la aplicacion en respuesta.

- DialogFlow: Comprueba los intents, parametros y respuestas de DF para mostrar la informacion al usuario o realizar la accion correspondiente.

## Carpeta: dates

Con una unica clase se centraliza toda la gestion de fechas para toda la aplicacion.

- DateFormatter: Permite usar formatos ISO, Instant y formatos amigables para el usuario incluyendo palabra 'Junio, Marzo...'.

## Carpeta: clients

Se centraliza toda la gestion de peticiones a clientes externos.

- IzvServer: Interfaz de definicion de los servicios.
- Request: Implementacion de la llamada y acciones a las respuestas de los servicios.

