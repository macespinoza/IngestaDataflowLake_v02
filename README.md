# Pipeline de Ingesta de Datos con Google Cloud Platform

Este repositorio contiene los archivos y configuraciones necesarios para implementar un pipeline de ingesta de datos desde MySQL hacia Google Cloud Platform, utilizando **Apache Beam (JDK 17)** con **NetBeans** como entorno de desarrollo. El pipeline está diseñado para cargar datos en un Data Lake (Google Cloud Storage) y posteriormente en BigQuery para su procesamiento y análisis. La arquitectura del proyecto se describe en la siguiente imagen:

## Arquitectura del Proyecto

![Dataflow Architecture](https://raw.githubusercontent.com/macespinoza/IngestaDataflowLake_v02/main/dataflow_v3.png)


### Componentes Principales:
1. **Fuente de Datos**:
   - Base de datos MySQL que contiene tablas como `products`, `productlines`, `customers`, `orders`, entre otras.

2. **Orquestación**:
   - **Cloud Scheduler**: Programa la ejecución periódica de la ingesta.
   - **Cloud Functions**: Maneja la orquestación para iniciar trabajos en Dataflow. Existen dos funciones:
     - **CF-TemplateDataflow**: Inicializa el pipeline en la plantilla de Dataflow.
     - **CF-TriggerStart**: Lanza los parametroses de tablas a Dataflow utilizando CF-TemplateDataflow.

3. **Procesamiento**:
   - **Apache Beam en Dataflow**: Pipelines que leen los datos desde MySQL, los convierten a formato CSV y los almacenan en Google Cloud Storage.

4. **Data Lake**:
   - **Google Cloud Storage**: Actúa como un repositorio para los datos crudos en formato CSV.

5. **Data Warehouse**:
   - **BigQuery**: Procesa y almacena los datos en capas para análisis:
     - **RAW**: Datos crudos provenientes de GCS.
     - **QUALITY**: Datos validados y limpiados.
     - **ACCESS**: Datose eliminando columnas sensibles para ser accesado por todos.
     - **STAGING/BUSINESS**: Capa final para visualizaciones en Looker.

---

## Estructura del Proyecto

```plaintext
/
├── CF-TemplateDataflow/
│   ├── main.py                # Código principal para inicializar plantillas de Dataflow
│   ├── requirements.txt       # Dependencias para la función
├── CF-TriggerStart/
│   ├── main.py                # Código principal para disparar pipelines de Dataflow
│   ├── requirements.txt       # Dependencias para la función
├── Data csv/                  # Archivos de datos de tabla en CSV 
├── Repositorio Dataflow/
│   ├── dataflow_v3.png        # Diagrama de arquitectura
├── labsqltocs/
│   ├── src/
│   │   ├── main/java/
│   │   │   ├── com/digisoft/labsqltocs/
│   │   │   │   ├── Inicio.java            # Código principal del pipeline
│   │   │   │   ├── CustomPipelineOptions.java  # Opciones de configuración del pipeline
│   ├── target/                  # Salida de compilación
├── mysqlsampledatabase/        # Scripts SQL para crear tablas y datos de muestra en mysql
├── Explicacion/                # Documentos adicionales para la explicación del proyecto 
└── README.md                   # Documentación del proyecto
```

---

## Configuración del Proyecto

### Requisitos Previos

1. **Herramientas y servicios necesarios**:
   - **JDK 17** instalado.
   - **NetBeans IDE** (o cualquier otro IDE compatible con Java).
   - **Google Cloud SDK** configurado y autenticado.
   - **Base de datos MySQL** con las tablas necesarias.
   - Proyecto de **Google Cloud** con los servicios habilitados:
     - Google Cloud Storage
     - BigQuery
     - Cloud Functions
     - Dataflow
   - Permisos necesarios:
     - `roles/dataflow.admin`
     - `roles/storage.admin`
     - `roles/bigquery.admin`

2. **Base de Datos**:
   - Configura la base de datos MySQL para que sea accesible desde Google Cloud.
   - Asegúrate de que las tablas estén pobladas con datos.

3. **Configuración de Dataflow**:
   - Los pipelines deben utilizar la plantilla `templatemysqltocs.json` configurada previamente.

---

## Monitoreo y Resolución de Problemas

1. **Monitoreo**:
   - Revisa los pipelines en la consola de Google Cloud > Dataflow.
   - Inspecciona los logs en Cloud Logging para identificar errores.

2. **Problemas Comunes**:
   - **El pipeline se completa pero no genera archivos**:
     - Verifica que la consulta `query_origen` retorne datos válidos.
     - Confirma que la ruta en `gcslake` sea válida y única para cada tabla.
   - **Fallos intermitentes**:
     - Revisa los límites de simultaneidad en GCS (En este caso se puso 40 seg de delay).
     - Asegúrate de que la base de datos pueda manejar múltiples conexiones.

---

## Créditos

Proyecto desarrollado de manera publica. Para preguntas o soporte, contacta al mantenedor del repositorio.

---

## Licencia

Este proyecto está licenciado bajo la MIT License. Consulta el archivo LICENSE para más detalles.

## Autor

**Miguel Cotrina**

- LinkedIn: [mcotrina](https://www.linkedin.com/in/mcotrina/)
- Canal Youtube: [@macespinozaonline](https://www.youtube.com/@macespinozaonline)
