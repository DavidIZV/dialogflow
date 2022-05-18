# Scrapy project
Es un proyecto para recuperar información sobre coches de segunda mano con objetivo generar algoritmos de predicción de precios

A continuación podemos ver la estructura del proyecto por carpetas:

## Carpeta: aws
En esta carpeta podremos encontrar los ficheros generados en el proceso.
Hay un ejemplo de cada uno, con el único objetivo de clarificar el funcionamiento:

- **dpp1.py**
Archivo para transformaciones aws.

- **executer_aws_upload_test.ipynb**
Archivo para subir y probar los modelos de AWS desde AWS SageMaker Studio.

- **executer_test_models_aws(not work).ipynb**
Archivo para usar modelos AWS desde local usando los modelos descargados desde Sagemaker Studio.

## Carpeta: django
Contiene un proyecto django para poder usar los modelos entrenados.
Se compone de los siguientes recursos:

- **models**
Contiene la misma informacion que la carpeta models de la parte de entrenamiento.
Podria ser la misma carpeta, pero por no complicar con los permisos de acceso los copio a mano.

- **statics**
Contiene los siguientes recursos destacables.

    - **dictionary_manual_values_traducido.json**
        Contiene lo mismo que su analogo "dictionary_manual_values.json" con variaciones para el usuario.

    - **form-loader.js**
        Contiene la lógica del formulario para las llamadas y representación de la información.

- **templates**
Contiene el formulario para probar los modelos.

- **apps.py**
Contiene la definición del api para probar los modelos.

- **models.py**
Contiene los modelos de django, el modelo de coche que se usa en el formulario y lógica de negocio.

- **predictor.py**
Contiene la parte que interactua con todos los modelos.

- **urls.py**
Contiene la definición de urls, básicamente el formulario y el servicio de predicción.

- **view.py**
Contiene el retorno del formulario y la llamada a los métodos de predicción.

## Carpeta: generated
En esta carpeta podremos encontrar los ficheros generados en el proceso.
Hay un ejemplo de cada uno, con el único objetivo de clarificar el funcionamiento:

- **scrapy/urls**
Contienen ficheros nombrados con este patron "car_liks0.json", contienen un json con una lista de links de coches.

- **scrapy/cars**
Contienen ficheros nombrados con este patron "data0.csv", contienen un csv con la información de los coches de las urls.

- **ml/aws_preprocess**
Ficheros generados para los experimentos y generacion de modelos de AWS SageMaker Studio.

- **ml/preprocess**
Contienen ficheros nombrados con este patron "all.csv", contienen un csv con la información de los coches que se van a preprocesar.

- **ml/models**
Todos los modelos generados y su diccionario.
Ademas hay el modelo de una red neuronal para identificar coches dañados en fotos.

- **ml/models/aws**
Todos los modelos generados por aws.

- **ml/models/learner**
Todos los modelos generados por los algoritmos de aprendizaje.

- **ml/models/neural**
Todos los modelos generados por la red neuronal.

## Carpeta: ml
Es la parte encargada de la recuperación del preprocesado de datos y entrenamiento de modelos.
Usa diferentes modelos de aprendizaje y redes neuronales.
Aquí podemos encontrar los siguientes recursos (numerados para seguir el orden no alfabetico):

- **1 executer_join.ipynb**
Software para unificar multiples ficheros csv que se hayan podido scrapear en diferentes momentos.

- **2 executer_clean_&_train.ipynb**
Software para limpiar los datos, aplicar transformaciones y entrenar los modelos.
En esta acción se generan tanto modelos como diccionarios valores de los datos para los modelos.

- **3 executer_neural_network.ipynb**
Software para entrenar una red neuronal.
En esta acción se generan tanto modelos como diccionarios valores de los datos para los modelos.

- **4 executer_view.ipynb**
Software para visualizar los datos que se han generado a partir de la limpieza.
Es solo una copia de "2 executer_clean_&_train.ipynb" recortada, pero es muy agil tenerla.

- **5 executer_test_models.ipynb**
Software para pruebas de los modelos que se han generado.

## Carpeta: scrapy
Es la parte encargada de la recuperación de la información de la red.
Está basada en la librería “undetected-chromedriver” basada a su vez en Selenium.
Aquí podemos encontrar los siguientes recursos:

- **executer_urls_slow.ipynb**
Software para buscar las urls de las páginas donde están los coches.
Permite lanzar las búsquedas por lotes.

- **executer_urls_fast.ipynb**
Una versión del recurso anterior que hace los mismo, pero sin tiempos de espera.
No permite búsquedas por lotes.

- **executer_datas.ipynb**
Software para recuperar la información de los coches de los anteriores enlaces.
