# Processor-csv
Microservicio está implementado para ejecutar un job batch que procesa registros de usuarios desde un archivo CSV, aplicando validaciones y persistiendo los datos en una base de datos. La solución se construye utilizando Spring Batch para la ejecución del proceso por lotes y Quartz Scheduler para la programación automática del job. Además, adopta una arquitectura hexagonal para garantizar una alta cohesión, bajo acoplamiento y facilidad de mantenimiento

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
