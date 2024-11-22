import functions_framework
import requests
import time

def cf_trigger_start(request):

    
    projecto="proyecto"
    
    tablas = ["customers", "employees", "offices", "orderdetails", "orders", "payments", "productlines", "products"]
    
    # Lista para almacenar resultados
    resultados = []
    
    # api-endpoint 
    URL = "https://us-west2-proyecto.cloudfunctions.net/cf_trigger_dataflow"
    
    for tabla in tablas:
        query="SELECT * FROM basededatos."+tabla
        storage="gs://mac-dtlk-lab0-dev/"+tabla
        
        PARAMS = {'prj':projecto,
              'query_origen':query,
              'storage':storage,
              'tabla':tabla
             }
             
        # Realizar la solicitud
        r = requests.get(url=URL, params=PARAMS)

        # Guardar el resultado (tabla y respuesta)
        resultados.append({
            'tabla': tabla,
            'status_code': r.status_code,
            'response_text': r.text
        })
        time.sleep(40)
        
    return resultados
