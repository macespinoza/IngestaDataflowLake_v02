import functions_framework
from googleapiclient.discovery import build

@functions_framework.http
def cf_trigger_dataflow(request):
    
    request_args = request.args
    ##################################### Clientes ############################################################
    dataflow = build('dataflow', 'v1b3')
    ##################################### Variable ############################################################
    #Project
    prj = request_args['prj']
    #Query de ingesta
    query_origen = request_args['query_origen']
    #Table
    table = request_args['tabla']
    #nombre de pipeline
    nombre_pipeline = 'jdbc-cs-pipeline-'+table
    #Storage
    storage=request_args['storage']
    

    ########################################### Parametros #####################################################

    parameters={
        'connectionURL': 'jdbc:mysql://xx.xx.xx.xx:3306/basededatos',
        'driverClassName': 'com.mysql.cj.jdbc.Driver',
        'query': query_origen,
        'gcslake': storage,
        'username': 'xxxxxx',
        'password': 'xxxxxxxx'
    }
    environment={
        'bypassTempDirValidation': False,
        'numWorkers': 2,
        'tempLocation': 'gs://cloudstorage/temp_job1'+table
    }
    
    ########################################### Pipeline Dataflow #########################################
    
    request = dataflow.projects().locations().templates().launch(
        projectId=prj,
        location='us-west2',
        gcsPath='gs://cloudstorage/data_templates/templatemysqltocs.json',
        body={
            'jobName': nombre_pipeline,
            'parameters': parameters,
            'environment':environment,
        }
    )

    response = request.execute()
    return response