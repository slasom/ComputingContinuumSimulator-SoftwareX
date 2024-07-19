FROM ubuntu/apache2

RUN apt-get update
RUN apt install -y net-tools curl iproute2 tcpdump iperf wget python3 python3-pip gcc python3-dev
RUN apt install iputils-ping
# RUN pip install scapy requests sockets
RUN wget https://github.com/pouriyajamshidi/tcping/releases/download/v1.22.1/tcping_Linux_static.tar.gz
RUN tar -xvf tcping_Linux_static.tar.gz
RUN chmod +x tcping
RUN cp tcping /usr/local/bin/