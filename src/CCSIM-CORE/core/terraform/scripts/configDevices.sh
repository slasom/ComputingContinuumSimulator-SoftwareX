#! /bin/bash


# Configurar los dispositivos de la red kathara
# $1: Number of devices connected to network
# $2: Delay of the network (p.e: 100ms)


if [ $1 -gt 0 ] && [ $# -eq 2 ];
then
	# Router config
	echo 'echo "nameserver 127.0.0.11" >> /etc/resolv.conf' >> ./lab/router.startup
	echo "ifconfig eth0 10.0.0.1/8 netmask 255.255.255.0 broadcast 10.0.0.255 up" >> ./lab/router.startup
	
	for i in $(seq 1 $1); do
		if [ $i -lt 10 ]; then
			echo "iptables -t nat -A PREROUTING -p tcp --dport 600"$i" -j DNAT --to-destination 10.0.0."$(( $i + 1 ))":6080" >> ./lab/router.startup
			echo "iptables -t nat -A POSTROUTING -p tcp --sport 6080 -j SNAT --to-source 172.17.0.2:600"$i >> ./lab/router.startup
		else
			echo "iptables -t nat -A PREROUTING -p tcp --dport 60"$i" -j DNAT --to-destination 10.0.0."$(( $i + 1 ))":6080" >> ./lab/router.startup
			echo "iptables -t nat -A POSTROUTING -p tcp --sport 6080 -j SNAT --to-source 172.17.0.2:60"$i >> ./lab/router.startup
		fi
	done
	
	echo "iptables -t nat -A POSTROUTING -o eth1 -j SNAT --to-source 172.17.0.2" >> ./lab/router.startup
	echo "iptables -t nat -A PREROUTING -i eth1 -j DNAT --to-destination 10.0.0.1" >> ./lab/router.startup
	echo "tc qdisc add dev eth0 root netem delay "$2 >> ./lab/router.startup
	
	# Devices config
	for i in $(seq 1 $1); do
		echo 'echo "nameserver 127.0.0.11" >> /etc/resolv.conf' >> ./lab/device$i.startup
		echo "ifconfig eth0 10.0.0.$(($i + 1))/8 netmask 255.255.255.0 broadcast 10.0.0.255 up" >> ./lab/device$i.startup
		echo "route add default gw 10.0.0.1" >> ./lab/device$i.startup
	done

else
	echo "Enter the parameters, 1. Number of devices 2. Delay of the network (p.e: 100ms)"
fi



