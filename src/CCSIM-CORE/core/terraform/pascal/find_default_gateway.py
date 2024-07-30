import re

def read_configuration(file):
    """ Reads the configuration file and returns a list of tuples (interface, IP, mask). """
    with open(file, 'r') as f:
        content = f.read()
    return re.findall(r'ifconfig (\w+) (\d+\.\d+\.\d+\.\d+) netmask (\d+\.\d+\.\d+\.\d+)', content)

def find_connection(router_configs, start, destination):
    """ Finds the connection between two routers, including the network interface. """
    for start_interface, start_ip, start_mask in router_configs[start]:
        for dest_interface, dest_ip, dest_mask in router_configs[destination]:
            if start_ip.split('.')[:3] == dest_ip.split('.')[:3]:
                return start_interface, start_ip, dest_interface, dest_ip
    return None, None, None, None

def build_network(router_configs):
    """ Builds the network from the router configurations. """
    network = {}
    for router in router_configs:
        network[router] = set()
        for other_router in router_configs:
            if router != other_router:
                interface1, ip1, interface2, ip2 = find_connection(router_configs, router, other_router)
                if interface1 and ip1:
                    network[router].add((other_router, interface1, ip1, interface2, ip2))
    return network

def find_routes(network, start):
    """ Finds the shortest routes from the initial router to all others. """
    visited = {start}
    route = {start: (None, None, None)}
    queue = [start]

    while queue:
        current = queue.pop(0)
        for neighbor, interface1, ip1, interface2, ip2 in network[current]:
            if neighbor not in visited:
                visited.add(neighbor)
                route[neighbor] = (current, interface2, ip1)
                queue.append(neighbor)
    
    return route
