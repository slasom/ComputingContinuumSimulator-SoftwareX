import argparse
import json
import internet_config
from sim2kathara import DADOSIM2Kathara, NetworkType


class CommandUI:

    NET_TYPE_IP = "IP"
    NET_TYPE_SDN = "SDN"
    NET_TYPE_P4 = "P4"
    NET_TYPES = [NET_TYPE_IP, NET_TYPE_SDN, NET_TYPE_P4]

    DEFAULT_IOT_IMAGE = "pascal/iot"

    ARGUMENTS = {
        "-i": {
            "required": True,
            "metavar": "DADOSIM config",
            "help": "Simulation config in DADOSIM format"
        },
        "-t": {
            "required": False,
            "metavar": "Network type",
            "help": "Network type to emulate (supported: IP, SDN, P4; default: IP)",
            "choices": NET_TYPES,
            "default": NET_TYPE_IP
        },
        "-r": {
            "required": False,
            "metavar": "microservice registry JSON",
            "help": "JSON file that ties microservices to Docker images. If none is provided, pascal/<microservice ID> will be used"
        },
        "--iot": {
            "required": False,
            "metavar": "IoT image",
            "help": f"Docker image to use for IoT devices. By default, {DEFAULT_IOT_IMAGE}",
            "default": DEFAULT_IOT_IMAGE
        },
        "--internet": {
            "required": False,
            "help": f"Grants the architecture the ability to connect to the internet. A switch named 'internetgateway' must exist and be connected to the network in order for this to work.",
            "action": "store_true"
        },
        "--debug": {
            "required": False,
            "help": "Enable debug mode (traffic capturing)",
            "action": "store_true"
        },
        "-o": {
            "required": True,
            "metavar": "Output lab",
            "help": "Output directory for the Kathará lab"
        }
    }

    def __init__(self) -> None:
        self.__ap = argparse.ArgumentParser(
            description="Parser to convert DADOSIM files to Kathará labs", add_help=True)
        for argument in self.ARGUMENTS:
            arg_params = self.ARGUMENTS[argument]
            self.__ap.add_argument(argument, **arg_params)

    def launch(self) -> None:
        ui_args = self.__ap.parse_args()
        with open(ui_args.i, 'r') as in_json:
            dadosim_scenario = json.load(in_json)
        ms_to_image = {}
        if ui_args.r:
            with open(ui_args.r, 'r') as in_reg:
                ms_to_image = json.load(in_reg)
        if ui_args.t == self.NET_TYPE_IP:
            net_type = NetworkType.IP
        elif ui_args.t == self.NET_TYPE_SDN:
            net_type = NetworkType.SDN
        elif ui_args.t == self.NET_TYPE_P4:
            net_type = NetworkType.P4
        else:
            net_type = NetworkType.IP
        conv = DADOSIM2Kathara(dadosim_scenario, ms_to_image, net_type, ui_args.iot)
        conv.create_kathara_lab(ui_args.o, ui_args.debug)
        if ui_args.internet:
            internet_config.configure_internet(ui_args.o.replace('/', ''), ui_args.i)

if __name__ == '__main__':
    CommandUI().launch()
