import requests

def make_petition(url):  # noqa: E501
    try:
        response = requests.get(url)
        # Asegura que la respuesta sea exitosa (código de estado 200)
        response.raise_for_status()
        # Retorna el JSON como una lista de objetos de Python
        return response.json()
    except requests.exceptions.HTTPError as errh:
        print(f"Http Error: {errh}")
    except requests.exceptions.ConnectionError as errc:
        print(f"Error Connecting: {errc}")
    except requests.exceptions.Timeout as errt:
        print(f"Timeout Error: {errt}")
    except requests.exceptions.RequestException as err:
        print(f"Oops: Something Else: {err}")

    return []
