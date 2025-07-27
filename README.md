# Processor-csv
Microservicio que implementa un job batch encargado de procesar registros de usuarios desde un archivo CSV que valida y persiste en base de datos. Utiliza Spring Batch para la ejecución del job y Quartz para su programación.

  Funcionalidades del microservicio:
- Leer y procesar registros desde archivos CSV.
- Validar y transformar los datos.
- Guardar resultados en base de datos relacional.
- Manejar errores con SkipListener y generar logs detallados.
- Ejecutar automáticamente el job con Quartz.

Herramienta	Uso principal
- Spring Boot	Framework. 
- Spring Batch para lectura, procesamiento y escritura de datos.
- Quartz Scheduler	Planificación de ejecución de jobs.
- OpenCSV	Lectura de archivos CSV.
- Spring Data JPA	Persistencia de datos.
- PostgreSQL base de datos de destino.
- Lombok	Reducción de boilerplate en POJOs.
- Bean Validation	Validación de campos.
- Logback + SLF4J	Logging de procesos y errores.
- Edita el archivo application.yml para:

Configuración inicial
- Definir la ruta al CSV.
- Configurar credenciales de base de datos en este caso PostgreSQL.
- Establecer la expresión cron para Quartz.
