import json
import re
import find_default_gateway

def does_internet_gateway_exist(topology_file: str) -> bool:
    """Confirms if there is a switch named 'internetgateway' in the DADOSIM topology file.

    Args:
        topology_file (string): File path for the DADOSIM file.

    Returns:
        bool: True if the switch exists, false if it does not.
    """
    with open(topology_file, 'r') as file:
        data = json.load(file)
        switches = data.get("switches", [])

        for switch in switches:
            if switch.get("id") == "internetgateway":
                return True

    return False

def obtain_hosts_list(topology_file: str) -> list:
    """Returns a list with the names of the hosts that will connect to the internet based on the DADOSIM file.

    Args:
        topology_file (str): File path for the DADOSIM file.

    Returns:
        list: List containing the names of the hosts.
    """
    with open(topology_file, 'r') as file:
        data = json.load(file)
        hosts = data.get("hosts", [])
        workflows = data.get("workflows", [])

        hosts_list = []

        for h in hosts:
            if len(h.get("deployed")) > 1:
                for m in h.get("deployed"):
                    hosts_list.append(h.get("id") + "ms" + m)
            elif len(h.get("deployed")) == 1:
                hosts_list.append(h.get("id"))

        for w in workflows:
            hosts_list.append("iotdev" + w.get("starter"))

        print(hosts_list)
        return hosts_list
    
def obtain_switches_list(topology_file: str) -> list:
    """Returns a list with the names of the switches based on the DADOSIM file.

    Args:
        topology_file (str): File path for the DADOSIM file.

    Returns:
        list: List containing the names of the switches.
    """
    with open(topology_file, 'r') as file:
        data = json.load(file)
        switches = data.get("switches", [])

        return [s.get("id") for s in switches]
    
def obtain_next_free_interface(lab_conf_file: str, device: str) -> int:
    """Obtains the next free interface for a certain device based on the lab.conf file.

    Args:
        lab_conf_file (str): File path for the lab.conf file.
        device (str): Name of the device to get the next free interface from.

    Returns:
        int: Next free interface of specified device.
    """
    with open(lab_conf_file, 'r') as file:
        lines = file.readlines()
        occupied_interfaces = set()
        for line in lines:
            if f"{device}[" in line:
                parts = line.split("[")
                if parts[1].split("]")[0].isnumeric():
                    interface_num = int(parts[1].split("]")[0])
                    occupied_interfaces.add(interface_num)

        next_interface = max(occupied_interfaces) + 1

        return next_interface

def add_router_config_lab_conf(lab_conf_file: str) -> None:
    """Writes all necessary extra information for the 'internetgateway' router in the lab.conf file.

    Args:
        lab_conf_file (str): File path for the lab.conf file.
    """
    with open(lab_conf_file, 'a') as file:
        file.write(f"internetgateway[{obtain_next_free_interface(lab_conf_file, 'internetgateway')}]=\"A\"\n")
        file.write("internetgateway[bridged]=\"true\"\n")
        file.write("internetgateway[sysctl]=\"net.ipv4.ip_forward=1\"\n")
        file.write("internetgateway[sysctl]=\"net.ipv6.conf.all.forwarding=1\"\n")

def add_router_config_int_gateway_startup(router_startup_file: str, delay: str) -> None:
    """Writes all necessary extra information for the 'internetgateway' router in the internetgateway.startup file.

    Args:
        router_startup_file (str): File path for the internetgateway.startup file.
        delay (str): Delay affecting the Internet route (example: 0.020ms)
    """
    ip_pattern = r'\b(?:\d{1,3}\.){3}\d{1,3}\b'
    with open(router_startup_file, 'r') as file:
        lines = file.readlines()
        internet_gateway_ip = re.search(ip_pattern, lines[0]).group()
        lines = lines[:-1]
    with open(router_startup_file, 'w') as file:
        file.writelines(lines)
    with open(router_startup_file, 'a') as file:
        file.write("echo \"nameserver 127.0.0.11\" >> /etc/resolv.conf\n")
        file.write(f"iptables -t nat -A POSTROUTING -o eth0 -j SNAT --to-source 172.17.0.2\n")
        file.write(f"iptables -t nat -A PREROUTING -i eth0 -j DNAT --to-destination {internet_gateway_ip}\n")
        file.write(f"tc qdisc add dev eth0 root netem delay {delay}\n")
        file.write("/etc/init.d/quagga start\n")

def add_switches_config_switches_startup(lab_folder: str, switches_list: list) -> None:
    """Writes all necessary extra information for each switch in the switch.startup files.

    Args:
        lab_folder (str): Lab where the folder has been created.
        switches_list (list): List containing the name of all switches.
    """
    router_configs = {s : find_default_gateway.read_configuration(f"{lab_folder}/{s}.startup") for s in switches_list}
    network = find_default_gateway.build_network(router_configs)
    routes = find_default_gateway.find_routes(network, 'internetgateway')
    for s in switches_list:
        if s != 'internetgateway':
            with open(f"./{lab_folder}/{s}.startup", 'r') as file:
                lines = file.readlines()
                lines = lines[:-1]
            with open(f"./{lab_folder}/{s}.startup", 'w') as file:
                file.writelines(lines)
            with open(f"./{lab_folder}/{s}.startup", 'a') as file:
                file.write(f"route add default gw {routes[s][2]} {routes[s][1]}\n")
                file.write("echo \"nameserver 127.0.0.11\" >> /etc/resolv.conf\n")
                file.write("/etc/init.d/quagga start\n")
            

def add_devices_config_devices_startup(lab_folder: str, hosts_list: list) -> None:
    """Writes all necessary extra information for each device in the device.startup files.

    Args:
        lab_folder (str): Lab where the folder has been created.
        hosts_list (list): List containing the name of all devices.
    """
    for h in hosts_list:
        with open(f"./{lab_folder}/{h}.startup", 'a') as file:
            file.write("echo \"nameserver 127.0.0.11\" >> /etc/resolv.conf\n")


def configure_internet(project_name, topology_file):
    lab_conf_file = f"{project_name}/lab.conf"


    if does_internet_gateway_exist(topology_file):
        hosts_list = obtain_hosts_list(topology_file)
        swicthes_list = obtain_switches_list(topology_file)
        add_router_config_lab_conf(lab_conf_file)
        add_router_config_int_gateway_startup(f"{project_name}/internetgateway.startup", "0.020ms")
        add_switches_config_switches_startup(project_name, swicthes_list)
        add_devices_config_devices_startup(project_name, hosts_list)
    else:
        print("No se encontró el switch 'internet-gateway' en el archivo 'topology.dadosim'.")
