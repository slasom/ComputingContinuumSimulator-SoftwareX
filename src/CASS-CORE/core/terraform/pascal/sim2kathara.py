import os
from enum import Enum

class NetworkType(Enum):
    SDN = 0
    IP = 1
    P4 = 2

class UnsupportedError(Exception):
    pass

class QuaggaConfig:

    def __init__(self):
        pass

    @staticmethod
    def daemons() -> str:
        return r'''# This file tells the quagga package 
# which daemons to start.
# Entries are in the format: <daemon>=(yes|no|priority)
# where 'yes' is equivalent to infinitely low priority, and
# lower numbers mean higher priority. Read
# /usr/doc/quagga/README.Debian.gz for details.
# Daemons are: bgpd zebra ospfd ospf6d ripd ripngd
zebra=yes
bgpd=no
ospfd=no
ospf6d=no
ripd=yes
ripngd=no
'''

    @staticmethod
    def zebra_conf(hostname: str) -> str:
        return r'''! -*- zebra -*-
!
! zebra configuration file
!
''' + f'hostname {hostname}' + r'''
password zebra
enable password zebra

log file /var/log/quagga/zebra.log
'''

    @staticmethod
    def ripd_conf(networks: "list[str]") -> str:
        conf = r'''!
hostname ripd
password zebra
enable password zebra
!
router rip
redistribute connected
'''
        for network in networks:
            conf += f'network {network}/24\n'
        return conf + r'''!
log file /var/log/quagga/ripd.log
'''

class SDNWatchDog:

    def __init__(self):
        pass

    @staticmethod
    def generate_watchdog(nodes: "list[str]") -> str:
        watchdog =  r'''import os
import time

'''
        watchdog += f'host_list={nodes}\n'
        watchdog += r'''
def watchdog():

    all_hosts_in = False

    begin = time.perf_counter()

    deadline = 60

    current = time.perf_counter()

    while (current - begin) <= deadline:
        hosts = os.listdir("shared")
        all_hosts_in = True
        for host in host_list:
            if host not in hosts:
                all_hosts_in = False
                break
        if not all_hosts_in:
            time.sleep(10)
        else:
            break
        current = time.perf_counter()
    missing_hosts = [h for h in host_list if h not in hosts]
    return all_hosts_in, missing_hosts

res = False
print("Pascal's Watchdog activated")
while not res:
    res, missing = watchdog()
    if not res:
        print(f"Hosts {missing} not ready")
        print("Simulation is stuck, resetting...")
        os.system("kathara lclean")
        os.system("rm -rf shared")
        os.system("mkdir shared")
        os.system("kathara lstart")
    else:
        print("Simulation ready!")
'''
        return watchdog

class DADOSIM2Kathara:

    def __init__(self, dadosim: dict, ms_to_image: "dict[str, str]" = {}, net_type: NetworkType = NetworkType.SDN, iot_image: str = "pascal/iot"):
        self._dadosim = dadosim
        self._image_registry = ms_to_image
        self._net_type = net_type
        self._iot_image = iot_image
        self._node_info = {}
        self._col_domains = {}
        self._last_net_a = 8
        self._last_net_b = 0
        self._last_net_c = 0
        self._networks = {}
        self._sw_dns = {}
        self._gateways = {}
        self._switch_ids = [x['id'] for x in dadosim['switches']]
        if self._net_type == NetworkType.IP:
            self.__assess_node_info_ip()
        elif self._net_type == NetworkType.SDN:
            self._sw_port_to_net = {}
            self._sw_to_dpid = {}
            self._control_domains = {}
            self._controller_dns = {}
            self._net_to_host = {}
            self._link_info = None
            self._initialize_link_info()
            self._last_dpid = 1
            self.__assess_node_info_sdn()
        elif self._net_type == NetworkType.P4:
            self.__assess_node_info_p4()
    
    def __next_net(self) -> str:
        network = f"{self._last_net_a}.{self._last_net_b}.{self._last_net_c}.0"
        if network not in self._networks:
            self._networks[network] = 1
        else:
            self._last_net_a += 1
            self._last_net_b = 0
            self._last_net_c = 0
            self.__next_net()
        if self._last_net_c == 255:
            self._last_net_b += 1
            self._last_net_c = 0
        else:
            self._last_net_c += 1
        return network
    
    def __int_to_dpid(self, num: int) -> str:
        return '0'*(16-len(str(num)))+f'{num}'
    
    def __next_ip(self, network: str) -> str:
        if self._networks.get(network, 256) > 255:
            raise Exception()
        a, b, c, __ = network.split('.')
        ip = f'{a}.{b}.{c}.{self._networks[network]}'
        self._networks[network] = self._networks[network] + 1
        return ip
    
    @staticmethod
    def broadcast(network: str) -> str:
        a, b, c, __ = network.split('.')
        return f'{a}.{b}.{c}.255'
    
    @staticmethod
    def direct_number(network: str, number: int) -> str:
        a, b, c, __ = network.split('.')
        return f'{a}.{b}.{c}.{number}'
    
    def __assess_node_info_ip(self):
        for switch in self._dadosim['switches']:
            switch_info = {"links": [], 'switch': True}
            switch_info["image"] = "kathara/quagga"
            self._node_info[switch["id"]] = switch_info
        for host in self._dadosim['hosts']:
            if len(host["deployed"]) > 1:
                self._col_domains[host["id"]] = self.__next_net()
                for ms in host["deployed"]:
                    sub_host_info = {"common_colission": host["id"], "links": []}
                    sub_host_id = f"{host['id']}ms{ms}"
                    sub_host_info["image"] = self._image_registry.get(ms, f"pascal/{ms}")
                    if "mem" in host:
                        sub_host_info["mem"] = host["mem"]
                    if "cpus" in host:
                        sub_host_info["cpus"] = host["cpus"]
                    self._node_info[sub_host_id] = sub_host_info
            else:
                host_info = {"links": []}
                if len(host["deployed"]) == 1:
                    ms = host["deployed"][0]
                    host_info["image"] = self._image_registry.get(ms, f"pascal/{ms}")
                    if "mem" in host:
                        host_info["mem"] = host["mem"]
                    if "cpus" in host:
                        host_info["cpus"] = host["cpus"]
                else:
                    continue
                self._node_info[host["id"]] = host_info

        for workflow in self._dadosim['workflows']:
            iot_id = f"iotdev{workflow['starter']}"
            self._node_info[iot_id] = {"common_colission": workflow["starter"], "image": self._iot_image, "links": []}
            for host in self._dadosim['hosts']:
                if host["id"] == workflow['starter']:
                    if "mem" in host:
                        self._node_info[iot_id]["mem"] = host["mem"]
                    if "cpus" in host:
                        self._node_info[iot_id]["cpus"] = host["cpus"]
            if workflow["starter"] not in self._col_domains:
                self._col_domains[workflow["starter"]]= self.__next_net()
        for link in self._dadosim['links']:
            domain = f"{link['source']}to{link['destination']}"
            if domain not in self._col_domains:
                self._col_domains[domain] = self.__next_net()
            lnk_info = {"domain": domain, "bandwidth": link["capacity"], "latency": link["latency"]}
            if link["source"] in self._node_info:
                self._node_info[link["source"]]["links"].append(lnk_info)
            else:
                if f'iotdev{link["source"]}' in self._node_info:
                    self._node_info[f'iotdev{link["source"]}']["links"].append(lnk_info)
                else:
                    for node in self._node_info:
                        if self._node_info[node].get("common_colission") == link["source"]:
                            self._node_info[node]["links"].append(lnk_info)
            if link["destination"] in self._node_info:
                self._node_info[link["destination"]]["links"].append(lnk_info)
            else:
                if f'iotdev{link["destination"]}' in self._node_info:
                    self._node_info[f'iotdev{link["destination"]}']["links"].append(
                        lnk_info)
                else:
                    for node in self._node_info:
                        if self._node_info[node].get("common_colission") == link["destination"]:
                            self._node_info[node]["links"].append(lnk_info)
            if link["source"] in self._switch_ids and not link["destination"] in self._switch_ids:
                if link["destination"] in self._node_info:
                    self._gateways[link["destination"]] = link["source"]
                else:
                    if f'iotdev{link["destination"]}' in self._node_info:
                        self._gateways[f'iotdev{link["destination"]}'] = link["source"]
                    else:
                        for node in self._node_info:
                            if self._node_info[node].get("common_colission") == link["destination"]:
                                self._gateways[node] = link["source"]
            elif link["source"] not in self._switch_ids and link["destination"] in self._switch_ids:
                if link["source"] in self._node_info:
                    self._gateways[link["source"]] = link["destination"]
                else:
                    if f'iotdev{link["source"]}' in self._node_info:
                        self._gateways[f'iotdev{link["source"]}'] = link["destination"]
                    else:
                        for node in self._node_info:
                            if self._node_info[node].get("common_colission") == link["source"]:
                                self._gateways[node] = link["destination"]
    
    def __assess_node_info_sdn(self):
        controller_info = {"links": [], 'deployed': True, 'image': 'pascal/faucet', 'shell': 'bash', 'controller': True}
        self._node_info['controller_node'] = controller_info
        for switch in self._dadosim['switches']:
            switch_info = {"links": [], 'switch': True, 'image': 'pascal/sdn', 'shell': 'bash', 'deployed': False}
            self._node_info[switch["id"]] = switch_info
        for host in self._dadosim['hosts']:
            if len(host["deployed"]) > 1:
                self._col_domains[host["id"]] = self.__next_net()
                for ms in host["deployed"]:
                    sub_host_info = {"common_colission": host["id"], "links": []}
                    sub_host_id = f"{host['id']}ms{ms}"
                    sub_host_info["image"] = self._image_registry.get(ms, f"pascal/{ms}")
                    self._node_info[sub_host_id] = sub_host_info
            else:
                host_info = {"links": []}
                if len(host["deployed"]) == 1:
                    host_info["image"] = self._image_registry.get(ms, f"pascal/{ms}")
                    self._node_info[host["id"]] = host_info
        for workflow in self._dadosim['workflows']:
            iot_id = f"iotdev{workflow['starter']}"
            self._node_info[iot_id] = {"common_colission": workflow["starter"], "image": self._iot_image, "links": []}
            if workflow["starter"] not in self._col_domains:
                self._col_domains[workflow["starter"]] = self.__next_net()
        for link in self._dadosim['links']:
            if link['source'] in self._switch_ids and link['destination'] in self._switch_ids:
                # Special treatment for stack links
                domain = f"{link['source']}stack{link['destination']}"
                # Stack links do not belong to any concrete network. No need to create nets or IPs.
                lnk_info = {"domain": domain, "bandwidth": link["capacity"], "latency": link["latency"], "stack": True, 'source': link["source"], 'destination': link["destination"]}
                # Stack links already have their nodes in node_info (guaranteed, we created them earlier)
                self._node_info[link['source']]['links'].append(lnk_info)
                self._node_info[link['destination']]['links'].append(lnk_info)
            else:
                domain = f"{link['source']}to{link['destination']}"
                if domain not in self._col_domains:
                    self._col_domains[domain] = self.__next_net()
                lnk_info = {"domain": domain, "bandwidth": link["capacity"], "latency": link["latency"]}
                if link["source"] in self._node_info:
                    self._node_info[link["source"]]["links"].append(lnk_info)
                else:
                    if f'iotdev{link["source"]}' in self._node_info:
                        self._node_info[f'iotdev{link["source"]}']["links"].append(
                            lnk_info)
                    else:
                        for node in self._node_info:
                            if self._node_info[node].get("common_colission") == link["source"]:
                                self._node_info[node]["links"].append(lnk_info)
                if link["destination"] in self._node_info:
                    self._node_info[link["destination"]]["links"].append(lnk_info)
                else:
                    if f'iotdev{link["destination"]}' in self._node_info:
                        self._node_info[f'iotdev{link["destination"]}']["links"].append(
                            lnk_info)
                    else:
                        for node in self._node_info:
                            if self._node_info[node].get("common_colission") == link["destination"]:
                                self._node_info[node]["links"].append(lnk_info)
                if link["source"] in self._switch_ids and not link["destination"] in self._switch_ids:
                    if link["destination"] in self._node_info:
                        self._gateways[link["destination"]] = link["source"]
                    else:
                        if f'iotdev{link["destination"]}' in self._node_info:
                            self._gateways[f'iotdev{link["destination"]}'] = link["source"]
                        else:
                            for node in self._node_info:
                                if self._node_info[node].get("common_colission") == link["destination"]:
                                    self._gateways[node] = link["source"]
                elif link["source"] not in self._switch_ids and link["destination"] in self._switch_ids:
                    if link["source"] in self._node_info:
                        self._gateways[link["source"]] = link["destination"]
                    else:
                        if f'iotdev{link["source"]}' in self._node_info:
                            self._gateways[f'iotdev{link["source"]}'] = link["destination"]
                        else:
                            for node in self._node_info:
                                if self._node_info[node].get("common_colission") == link["source"]:
                                    self._gateways[node] = link["destination"]

    def __assess_node_info_p4(self):
        raise UnsupportedError("P4 networks are not yet supported")
    
    @staticmethod
    def __lab_add_info() -> "list[str]":
        return ['LAB_DESCRIPTION="Automated Kathara lab generated using Pascal"', 'LAB_VERSION=1.0', '\n']
    
    def create_kathara_lab(self, lab_dir: str, debug: bool = False):
        if self._net_type == NetworkType.IP:
            self._create_kathara_lab_ip(lab_dir, debug)
        elif self._net_type == NetworkType.SDN:
            self._create_kathara_lab_sdn(lab_dir, debug)
    
    def _process_node_sdn(self, node: str, lab_dir: str) -> "tuple[list[str], list[str]]":
        lab_conf = []
        host_startup = []
        os.makedirs(os.path.join(lab_dir, node), exist_ok=True)
        lab_conf.append(f'{node}[image]="{self._node_info[node]["image"]}"')
        lab_conf.append(f'{node}[shell]="{self._node_info[node].get("shell", "sh")}"')
        if self._node_info[node].get("switch", False):
            startup, conf = self._process_switch_sdn(node)
        elif self._node_info[node].get("controller", False):
            startup, conf = self._process_controller_sdn(node)
        else:
            startup, conf = self._process_host_sdn(node)
        host_startup += startup
        lab_conf += conf
        return host_startup, lab_conf
    
    def _initialize_link_info(self):
        link_info = {}
        for link in self._dadosim['links']:
            src = link["source"]
            dst = link["destination"]
            edge_info = {}
            if "latency" in link:
                    edge_info["latency"] = link["latency"]
            if "capacity" in link:
                edge_info["bandwidth"] = link["capacity"]
            if src not in link_info:
                link_info[src] = {dst: edge_info}
            else:
                link_info[src][dst] = edge_info
            if dst not in link_info:
                link_info[dst] = {src: edge_info}
            else:
                link_info[dst][src] = edge_info
        self._link_info = link_info

    def _get_control_route_characteristics(self, route: "list[str]") -> "tuple[float, float]":
        total_lat = 0
        total_bw = None
        for src, dst in zip(route, route[1:]):
            link_info = self._link_info[src][dst]
            if 'bandwidth' in link_info:
                if total_bw is None:
                    total_bw = link_info['bandwidth']
                elif total_bw > link_info['bandwidth']:
                    total_bw = link_info['bandwidth']
            if 'latency' in link_info:
                total_lat += link_info['latency']
        return total_lat, total_bw
    
    def _process_controller_sdn(self, node: str) -> "tuple[list[str], list[str]]":
        host_startup = ["set -e"]
        lab_conf = []
        if_counter = 0
        control_domains_to_add = self._control_domains.get(node, [])
        for control_domain in control_domains_to_add:
            domain_name = control_domain['domain']
            control_latency = control_domain.get('latency', 0)
            control_bw = control_domain.get('bw')
            control_net = control_domain['network']
            control_ip = control_domain['control_ip']
            control_bc = self.broadcast(control_net)
            host_startup.append(
                f"ifconfig eth{if_counter} {control_ip} netmask 255.255.255.0 broadcast {control_bc} up")
            lab_conf.append(f'{node}[{if_counter}]="{domain_name}"')
            c_unit = 's'
            if control_latency > 0:
                if control_latency > 10:
                    c_unit = 'ms'
                elif control_latency < 1e-3:
                    control_latency = control_latency * 1e3
                    c_unit = 'ms'
                if control_latency > 0:
                    host_startup.append(
                        f"tc qdisc add dev eth{if_counter} root netem delay {control_latency}{c_unit}")
            if control_bw is not None:
                pass
                #write_later.append(
                #    f"tc qdisc add dev eth0 root tbf rate {control_bw}mbit burst {control_bw} latency 10s")
            if_counter += 1
        host_startup.append("faucet --verbose > /var/log/faucet.log 2>&1 &")
        return host_startup, lab_conf

    def _process_switch_sdn(self, node: str):
        host_startup = []
        lab_conf = []
        if_counter = 0
        alt_if_counter = 0
        ofport_counter = 1
        write_later = []
        int_dpid = self._last_dpid
        bridge_name = f'sw{int_dpid}'
        dpid = self.__int_to_dpid(int_dpid)
        self._last_dpid += 1
        self._sw_to_dpid[node] = dpid
        host_startup.append("service openvswitch-switch start")
        host_startup.append("set -e")
        host_startup.append(f"ovs-vsctl add-br {bridge_name}")
        host_startup.append(f"ovs-vsctl set bridge {bridge_name} other-config:datapath-id={dpid}")
        host_startup.append(f"ovs-vsctl set bridge {bridge_name} other-config:disable-in-band=true")
        if not self._node_info[node].get("deployed", False):
            if_counter +=1
            alt_if_counter += 1
            #controller = self._node_info[node]["controller"]
            controller = 'controller_node'
            domain_name = f'ctrl{node}to{controller}'
            control_net = self.__next_net()
            control_route = self._node_info[node].get("controller_route", [])
            control_lat, control_bw = self._get_control_route_characteristics(control_route)
            #host_startup.append(f"ovs-vsctl add-port {bridge_name} eth0 -- set interface eth0 ofport_request=1")
            lab_conf.append(f'{node}[0]="{domain_name}"')
            control_ip = self.__next_ip(control_net)
            control_broadcast = self.broadcast(control_ip)
            controller_ip = self.__next_ip(control_net)
            self._controller_dns[node] = controller_ip
            self._control_domains[controller] = self._control_domains.get(controller, []) + [{"domain": domain_name, "latency": control_lat, "bandwidth": control_bw, "network": control_net, "control_ip": controller_ip}]
            #self._sw_port_to_net[node] = self._sw_port_to_net.get(node, []) + [(0, control_net)]
            host_startup.append(
                f'ifconfig eth0 {control_ip} netmask 255.255.255.0 broadcast {control_broadcast} up')
            #write_later.append(f'ifconfig {bridge_name}:0 {control_ip} netmask 255.255.255.0 broadcast {control_broadcast} up')
            c_unit = 's'
            if control_lat > 0:
                if control_lat > 10:
                    c_unit = 'ms'
                elif control_lat < 1e-3:
                    control_lat = control_lat * 1e3
                    c_unit = 'ms'
                write_later.append(
                    f"tc qdisc add dev eth0 root netem delay {control_lat}{c_unit}")
            if control_bw is not None:
                pass
                #write_later.append(
                #    f"tc qdisc add dev eth0 root tbf rate {control_bw}mbit burst {control_bw} latency 10s")
        else:
            control_domains_to_add = self._control_domains.get(node, [])
            for control_domain in control_domains_to_add:
                domain_name = control_domain['domain']
                control_latency = control_domain.get('latency', 0)
                control_bw = control_domain.get('bw')
                control_net = control_domain['network']
                control_ip = control_domain['control_ip']
                control_bc = self.broadcast(control_net)
                host_startup.append(f"ovs-vsctl add-port {bridge_name} eth{if_counter} -- set interface eth{if_counter} ofport_request={ofport_counter}")
                lab_conf.append(f'{node}[{if_counter}]="{domain_name}"')
                self._sw_port_to_net[node] = self._sw_port_to_net.get(node, []) + [(ofport_counter, control_net)]
                write_later.append(
                    f'ifconfig {bridge_name}:0 {control_ip} netmask 255.255.255.0 broadcast {control_bc} up')
                c_unit = 's'
                if control_latency > 0:
                    if control_latency > 10:
                        c_unit = 'ms'
                    elif control_latency < 1e-3:
                        control_latency = control_latency * 1e3
                        c_unit = 'ms'
                    write_later.append(
                        f"tc qdisc add dev eth0 root netem delay {control_latency}{c_unit}")
                if control_bw is not None:
                    pass
                    #write_later.append(
                    #    f"tc qdisc add dev eth0 root tbf rate {control_bw}mbit burst {control_bw} latency 10s")
                if_counter += 1
                alt_if_counter += 1
                ofport_counter += 1
        for link_info in self._node_info[node]["links"]:
            host_startup.append(
                f"ovs-vsctl add-port {bridge_name} eth{if_counter} -- set interface eth{if_counter} ofport_request={ofport_counter}")
            col_domain = link_info['domain']
            lab_conf.append(f'{node}[{if_counter}]="{col_domain}"')
            if not link_info.get("stack", False):
                ip = self.__next_ip(self._col_domains[col_domain])
                broadcast = self.broadcast(
                    self._col_domains[col_domain])
                self._sw_port_to_net[node] = self._sw_port_to_net.get(
                    node, []) + [(ofport_counter, self._col_domains[col_domain])]
                write_later.append(
                    f'ifconfig {bridge_name}:{alt_if_counter} {ip} netmask 255.255.255.0 broadcast {broadcast} up')
                alt_if_counter += 1
            else:
                stacked_to = link_info["source"] if link_info["source"] != node else link_info["destination"]
                self._sw_port_to_net[node] = self._sw_port_to_net.get(
                    node, []) + [(ofport_counter, "stack", stacked_to)]
            if 'latency' in link_info:
                lat = link_info['latency']
                unit = 's'
                if lat > 0:
                    if lat > 10:
                        unit = 'ms'
                    elif lat < 1e-3:
                        lat = lat * 1e3
                        unit = 'ms'
                    write_later.append(
                        f"tc qdisc add dev eth{if_counter} root netem delay {lat}{unit}")
            if 'bandwidth' in link_info:
                pass
                #write_later.append(
                #    f"tc qdisc add dev eth{if_counter} root tbf rate {link_info['bandwidth']}mbit burst {link_info['bandwidth']} latency 10s")
            if_counter += 1
            ofport_counter += 1
        host_startup.append(f'ifconfig {bridge_name} up')
        for line in write_later:
            host_startup.append(line)
        if self._node_info[node].get("deployed", False):
            host_startup.append(
                "faucet --verbose > /var/log/faucet.log 2>&1 &")
        host_startup.append(f'ovs-vsctl set bridge {bridge_name} protocols=OpenFlow13')
        host_startup.append(f'ovs-vsctl set-fail-mode {bridge_name} secure')
        if self._node_info[node].get("deployed", False):
            host_startup.append(f'ovs-vsctl set-controller {bridge_name} tcp:127.0.0.1:6653 tcp:127.0.0.1:6654 ')
        else:
            controller_ip = self._controller_dns[node]
            host_startup.append(f'ovs-vsctl set-controller {bridge_name} tcp:{controller_ip}:6653 tcp:{controller_ip}:6554')
        return host_startup, lab_conf

    def _process_host_sdn(self, node: str):
        lab_conf = []
        host_startup = []
        if_counter = 0
        if "common_colission" in self._node_info[node]:
            col_domain = self._node_info[node]['common_colission']
            lab_conf.append(f'{node}[{if_counter}]="{col_domain}"')
            ip = self.__next_ip(self._col_domains[col_domain])
            broadcast = self.broadcast(self._col_domains[col_domain])
            host_startup.append(
                f"ifconfig eth{if_counter} {ip} netmask 255.255.255.0 broadcast {broadcast} up")
            if_counter += 1
        for link_info in self._node_info[node]["links"]:
            col_domain = link_info['domain']
            lab_conf.append(f'{node}[{if_counter}]="{col_domain}"')
            ip = self.__next_ip(self._col_domains[col_domain])
            self._net_to_host[self._col_domains[col_domain]] = ip
            broadcast = self.broadcast(self._col_domains[col_domain])
            host_startup.append(
                f"ifconfig eth{if_counter} {ip} netmask 255.255.255.0 broadcast {broadcast} up")
            if 'latency' in link_info:
                lat = link_info['latency']
                unit = 's'
                if lat > 0:
                    if lat > 10:
                        unit = 'ms'
                    elif lat < 1e-3:
                        lat = lat * 1e3
                        unit = 'ms'
                    host_startup.append(
                        f"tc qdisc add dev eth{if_counter} root netem delay {lat}{unit}")
            if 'bandwidth' in link_info:
                pass
                #host_startup.append(
                #    f"tc qdisc add dev eth{if_counter} root tbf rate {link_info['bandwidth']}mbit burst {link_info['bandwidth']} latency 10s")
            gw = self._gateways.get(node)
            if gw:
                net = self._col_domains.get(col_domain)
                if net:
                    #virtual_gateway = self.direct_number(net, 254)
                    #host_startup.append(
                    #    f"route add default gw {virtual_gateway} eth{if_counter}")
                    host_startup.append(
                        f"route add default eth{if_counter}"
                    )
            if_counter += 1
        return host_startup, lab_conf
    
    def _find_port_number(self, stack_src: str, stack_dst: str):
        ports_to_nets = self._sw_port_to_net[stack_dst]
        for port_info in ports_to_nets:
            if len(port_info) > 2:
                if port_info[2] == stack_src:
                    return port_info[0]
    
    def _generate_controller_yaml(self, controller_dirs: "list[str]"):
        import yaml
        class HexInt(int):
            pass
        def representer(dumper, data):
            return yaml.ScalarNode('tag:yaml.org,2002:int', hex(data))
        yaml.add_representer(HexInt, representer)
        yaml_dkt = {}
        acls = {}
        vlan_ids = []
        acl_id = 901
        br_ports_to_acls = {}
        datapaths = {}
        for switch in self._sw_to_dpid:
            dpid = self._sw_to_dpid[switch]
            int_dpid = int(dpid)
            ports_to_nets = self._sw_port_to_net[switch]
            bridge_name = f'sw{int_dpid}'
            dp_info = {'dp_id': HexInt(int(dpid, 16))}
            has_stack = False
            interfaces = {}
            for net_info in ports_to_nets:
                port_num = net_info[0]
                if_name = f'{switch}if{port_num}'
                if len(net_info) > 2:
                    has_stack = True
                    stack_dst = net_info[2]
                    destination_port = self._find_port_number(switch, stack_dst)
                    interfaces[port_num] = {'description': if_name, 'stack': {
                        'dp': f'sw{int(self._sw_to_dpid[stack_dst])}', 'port': destination_port}}
                else:
                    host_net = net_info[1]
                    host_ip = self._net_to_host[host_net]
                    vlan_id = f'{if_name}'
                    vlan_ids.append(vlan_id)
                    acl_name = f'tunnel-to-{if_name}'
                    ip_rule = {"dl_type": 0x800, 'ipv4_dst': host_ip, 'actions': {'output': {'tunnel': {
                        "type": 'vlan', 'tunnel_id': acl_id, 'dp': bridge_name, 'port': port_num}}}}
                    arp_rule = {"dl_type": 0x806, 'arp_tpa': host_ip, 'actions': {'output': {
                        'tunnel': {"type": 'vlan', 'tunnel_id': acl_id, 'dp': bridge_name, 'port': port_num}}}}
                    acls[acl_name] = [{'rule': ip_rule}, {'rule': arp_rule}]
                    if not bridge_name in br_ports_to_acls:
                        br_ports_to_acls[bridge_name] = {}
                    br_ports_to_acls[bridge_name][port_num] = acl_name
                    acl_id += 1
                    interfaces[port_num] = {'description': if_name, 'native_vlan': vlan_id, 'acls_in': []}
            if has_stack:
                dp_info['stack'] = {'priority': 1}
            dp_info['interfaces'] = {k: interfaces[k]
                                     for k in sorted(interfaces)}
            datapaths[bridge_name] = dp_info
        vlans = {}
        vlan_id = 101
        for vlan in vlan_ids:
            vlans[vlan] = {'vid': vlan_id}
            vlan_id += 1
        yaml_dkt['vlans'] = vlans
        for bridge in datapaths:
            for interface_num in datapaths[bridge]['interfaces']:
                if 'acls_in' in datapaths[bridge]['interfaces'][interface_num]:
                    forbidden_acl = br_ports_to_acls[bridge][interface_num]
                    for acl_name in acls:
                        if acl_name != forbidden_acl:
                            datapaths[bridge]['interfaces'][interface_num]['acls_in'].append(acl_name)
        yaml_dkt['dps'] = datapaths
        yaml_dkt['acls'] = acls
        #yaml_dkt['routers'] = {'router-def': {'vlans': list(vlans.keys())}}
        for cnt_dir in controller_dirs:
            faucet_path = os.path.join(cnt_dir, "etc", "faucet")
            os.makedirs(faucet_path, exist_ok=True)
            with open(os.path.join(faucet_path, 'faucet.yaml'), 'w') as out_yaml:
                yaml.dump(yaml_dkt, out_yaml)

    def _create_kathara_lab_sdn(self, lab_dir: str, debug: bool = False):
        os.makedirs(os.path.join(lab_dir, "shared"), exist_ok=True)
        lab_conf = self.__lab_add_info()
        host_startup = {}
        for node in sorted(self._node_info, key=lambda n: (n == 'controller_node', self._node_info[n].get("switch", False), self._node_info[n].get("deployed", False))):
            startup, conf = self._process_node_sdn(node, lab_dir)
            host_startup[node] = startup
            host_startup[node].append(f'echo "ready" > /shared/{node}')
            lab_conf += conf
        controllers = [ctl for ctl in self._node_info.keys() if (self._node_info[ctl].get(
            "deployed", False) and self._node_info[ctl].get("switch", False)) or self._node_info[ctl].get("controller", False)]
        controller_dirs = [os.path.join(lab_dir, ctl) for ctl in controllers]
        self._generate_controller_yaml(controller_dirs)
        for host in host_startup:
            with open(os.path.join(lab_dir, f'{host}.startup'), 'w') as out_startup:
                for line in host_startup[host]:
                    out_startup.write(line)
                    out_startup.write("\n")
                if debug:
                    out_startup.write(f'tcpdump -i any -w /shared/{host}.pcap &\n')
            if not os.path.exists(os.path.join(lab_dir, host)):
                os.makedirs(os.path.join(lab_dir, host), exist_ok=True)
        with open(os.path.join(lab_dir, 'lab.conf'), 'w') as out_conf:
            for line in lab_conf:
                out_conf.write(line)
                out_conf.write("\n")
        py_watchdog = SDNWatchDog.generate_watchdog(list(host_startup.keys()))
        with open(os.path.join(lab_dir, 'watchdog.py'), 'w') as out_watchdog:
            out_watchdog.write(py_watchdog)
        with open(os.path.join(lab_dir, 'pascal.sh'), 'w') as out_script:
            out_script.write('kathara lstart\n')
            out_script.write('python3 watchdog.py\n')

    def _create_kathara_lab_ip(self, lab_dir: str, debug: bool = False):
        os.makedirs(os.path.join(lab_dir, "shared"), exist_ok=True)
        lab_conf = self.__lab_add_info()
        host_startup = {}
        switch_networks = {}
        for node in self._node_info:
            os.makedirs(os.path.join(lab_dir, node), exist_ok=True)
            host_startup[node] = []
            lab_conf.append(f'{node}[image]="{self._node_info[node]["image"]}"')
            lab_conf.append(f'{node}[shell]="sh"')
            if "mem" in self._node_info[node]:
                lab_conf.append(f'{node}[mem]="{self._node_info[node]["mem"]}"')
            if "cpus" in self._node_info[node]:
                lab_conf.append(f'{node}[cpus]="{self._node_info[node]["cpus"]}"')
            if_counter = 0
            if "common_colission" in self._node_info[node]:
                col_domain = self._node_info[node]['common_colission']
                lab_conf.append(f'{node}[{if_counter}]="{col_domain}"')
                ip = self.__next_ip(self._col_domains[col_domain])
                broadcast = self.broadcast(self._col_domains[col_domain])
                host_startup[node].append(f"ifconfig eth{if_counter} {ip} netmask 255.255.255.0 broadcast {broadcast} up")
                #self._dns.append(node, ip)
                if_counter += 1
            for link_info in self._node_info[node]["links"]:
                col_domain = link_info['domain']
                lab_conf.append(f'{node}[{if_counter}]="{col_domain}"')
                ip = self.__next_ip(self._col_domains[col_domain])
                broadcast = self.broadcast(self._col_domains[col_domain])
                host_startup[node].append(
                    f"ifconfig eth{if_counter} {ip} netmask 255.255.255.0 broadcast {broadcast} up")
                if 'latency' in link_info:
                    lat = link_info['latency']
                    unit = 's'
                    if lat > 0:
                        if lat > 10:
                            unit = 'ms'
                        elif lat < 1e-3:
                            lat = lat * 1e3
                        unit = 'ms'
                        host_startup[node].append(f"tc qdisc add dev eth{if_counter} root netem delay {lat}{unit}")
                if 'bandwidth' in link_info:
                    pass
                    #host_startup[node].append(f"tc qdisc add dev eth{if_counter} root tbf rate {link_info['bandwidth']}mbit burst {link_info['bandwidth']} latency 10s")
                #self._dns.append(node, ip)
                if self._node_info[node].get("switch", False):
                    if node not in self._sw_dns:
                        self._sw_dns[node] = {}
                    self._sw_dns[node][col_domain] = ip
                    switch_networks[node] = switch_networks.get(node, []) + [self._col_domains[col_domain]]
                else:
                    gw = self._gateways.get(node)
                    if gw:
                        gw_ip = self._sw_dns[gw].get(col_domain)
                        if gw_ip:
                            host_startup[node].append(f"route add default gw {gw_ip} eth{if_counter}")
                if_counter += 1
            if self._node_info[node].get("switch", False):
                host_startup[node].append("/etc/init.d/quagga start")
                quagga_dir = os.path.join(lab_dir, node, "etc", "quagga")
                os.makedirs(quagga_dir, exist_ok=True)
                with open(os.path.join(quagga_dir, "daemons"), 'w') as out_daemons:
                    out_daemons.write(QuaggaConfig.daemons())
                with open(os.path.join(quagga_dir, 'zebra.conf'), 'w') as out_zebra:
                    out_zebra.write(QuaggaConfig.zebra_conf(node))
                with open(os.path.join(quagga_dir, 'ripd.conf'), 'w') as out_ripd:
                    out_ripd.write(QuaggaConfig.ripd_conf(switch_networks[node]))
            lab_conf.append("\n")
        for host in host_startup:
            with open(os.path.join(lab_dir, f'{host}.startup'), 'w') as out_startup:
                for line in host_startup[host]:
                    out_startup.write(line)
                    out_startup.write("\n")
                if debug:
                    out_startup.write(
                        f'tcpdump -i any -w /shared/{host}.pcap &\n')
            if not os.path.exists(os.path.join(lab_dir, host)):
                os.makedirs(os.path.join(lab_dir, host), exist_ok=True)
        with open(os.path.join(lab_dir, 'lab.conf'), 'w') as out_conf:
            for line in lab_conf:
                out_conf.write(line)
                out_conf.write("\n")
