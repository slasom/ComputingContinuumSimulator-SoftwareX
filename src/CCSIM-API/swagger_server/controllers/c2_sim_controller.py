import connexion
import six
import petition

from swagger_server import util
import requests
from flask import send_file, abort, request

URL = 'http://localhost:8081'


def get_all_executions_from_database():  # noqa: E501
    """Retrieves information about all executions.

    Retrieves information about all executions. # noqa: E501


    :rtype: List[object]
    """
    return petition.make_petition(URL + '/executions')


def get_all_executions_from_user_from_database(user_name):  # noqa: E501
    """Retrieves all executions from a certain user.

    Retrieves all executions from a certain user. # noqa: E501

    :param user_name: Name of the user whose executions should be fetched
    :type user_name: str

    :rtype: List[object]
    """
    request_user_name = request.headers.get('user_name')
    api_key = request.headers.get('api_key')

    # Verificar que los encabezados existen antes de enviar la solicitud
    if not request_user_name or not api_key:
        abort(400, 'Missing user_name or api_key in headers')

    headers = {
        'user_name': request_user_name,
        'api_key': api_key
    }
    print("Sending Headers:", headers)
    response = requests.get(f"{URL}/executions/{user_name}", headers=headers)
    if response.status_code == 200:
        return response.json()
    else:
        abort(response.status_code, response.text)

def get_all_projects_from_database():  # noqa: E501
    """Retrieves information about all projects.

    Retrieves information about all projects. # noqa: E501


    :rtype: List[object]
    """
    return petition.make_petition(URL + '/projects')


def get_all_projects_from_user_from_database(user_name):  # noqa: E501
    """Retrieves all projects from a certain user.

    Retrieves all projects from a certain user. # noqa: E501

    :param user_name: Name of the user whose projects should be fetched
    :type user_name: str

    :rtype: List[object]
    """
    request_user_name = request.headers.get('user_name')
    api_key = request.headers.get('api_key')
    headers = {
        'user_name': request_user_name,
        'api_key': api_key
    }
    response = requests.get(f"{URL}/projects/{user_name}", headers=headers, stream=True)
    if response.status_code == 200:
        return response.json()
    else:
        abort(response.status_code, response.text)

def get_all_users_from_database():  # noqa: E501
    """Retrieves information about all users.

    Retrieves information about all users. # noqa: E501


    :rtype: List[object]
    """
    return petition.make_petition(URL + '/users')


def get_execution_from_database(execution_name):  # noqa: E501
    """Fetches information about a specific execution.

    Fetches information about a specific execution. # noqa: E501

    :param execution_name: Name of the execution to fetch
    :type execution_name: str

    :rtype: List[object]
    """
    request_user_name = request.headers.get('user_name')
    api_key = request.headers.get('api_key')

    # Verificar que los encabezados existen antes de enviar la solicitud
    if not request_user_name or not api_key:
        abort(400, 'Missing user_name or api_key in headers')

    headers = {
        'user_name': request_user_name,
        'api_key': api_key
    }
    print("Sending Headers:", headers)
    response = requests.get(f"{URL}/execution/{execution_name}", headers=headers)
    if response.status_code == 200:
        return response.json()
    else:
        abort(response.status_code, response.text)


def get_project_from_database(project_name):  # noqa: E501
    """Fetches information about a specific project.

    Fetches information about a specific project. # noqa: E501

    :param project_name: Name of the project to fetch
    :type project_name: str

    :rtype: List[object]
    """
    request_user_name = request.headers.get('user_name')
    api_key = request.headers.get('api_key')
    headers = {
        'user_name': request_user_name,
        'api_key': api_key
    }
    print("Request User Name:", request_user_name)
    print("API Key:", api_key)

    response = requests.get(f"{URL}/project/{project_name}", headers=headers)
    if response.status_code == 200:
        return response.json()
    else:
        abort(response.status_code, response.text)
    #return petition.make_petition(URL + '/project/' + project_name)


def get_user_from_database(user_name):  # noqa: E501
    """Fecthes information about a specific user.

    Fecthes information about a specific user. # noqa: E501

    :param user_name: Name of the user to fetch
    :type user_name: str

    :rtype: List[object]
    """
    return petition.make_petition(URL + '/user/' + user_name)

def download_files_from_execution(project_name, execution_name):
    # Obtener el api_key del encabezado de la solicitud entrante
    api_key = request.headers.get('api_key')

    print("Incoming download")

    # Verificar que el api_key est  presente
    if not api_key:
        abort(401, "API Key is required")

    # URL del servicio de gesti n de proyectos
    project_manager_url = f"http://localhost:8082/download/{project_name}/{execution_name}"

    # Realizar la solicitud HTTP al servicio de gesti n de proyectos, incluyendo el api_key en los headers
    headers = {'api_key': api_key}
    response = requests.get(project_manager_url, headers=headers, stream=True)

    # Comprobar la respuesta y manejar seg n el c digo de estado
    if response.status_code == 200:
        print("Downloading...")
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