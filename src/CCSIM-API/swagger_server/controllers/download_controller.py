# download_controller.py

import requests
from flask import send_file, abort,request

def download_files_from_execution(project_name, execution_name):
    # Obtener el api_key del encabezado de la solicitud entrante
    api_key = request.headers.get('api_key')

    # Verificar que el api_key está presente
    if not api_key:
        abort(401, "API Key is required")

    # URL del servicio de gestión de proyectos
    project_manager_url = f"http://localhost:8082/download/{project_name}/{execution_name}"

    # Realizar la solicitud HTTP al servicio de gestión de proyectos, incluyendo el api_key en los headers
    headers = {'api_key': api_key}
    response = requests.get(project_manager_url, headers=headers, stream=True)

    # Comprobar la respuesta y manejar según el código de estado
    if response.status_code == 200:
        # Escribir el contenido del archivo ZIP en un archivo temporal
        with open("temp_download.zip", "wb") as temp_file:
            for chunk in response.iter_content(chunk_size=8192):
                temp_file.write(chunk)

        # Enviar el archivo como respuesta con 'send_file' de Flask
        return send_file("temp_download.zip", as_attachment=True, download_name=f"{execution_name}.zip")
    elif response.status_code == 404:
        abort(404, "File not found")
    else:
        abort(500, "Internal Server Error")
