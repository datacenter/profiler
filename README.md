# Cisco Application Profiler
This directory contains all the source files related to Profiler

Cisco Application Profiler is a web based application. This application can be used to build repository of ACI config objects by pulling configuration from well known devices such as Palo Alto Firewall. These repository objects can be used in contracts of an ACI project and can be pushed to APIC.


Pre-requisites: Install MySQL and create a database. Update username, password, and database name in application-dev.yml

Build:
1.Install Node components
2.Run mvn install -DskipTests=true This installs bower and other components and builds war file.
3.To run application locally, use the command, mvn spring-boot:run

Build Plugins: Palo Alto Plugin:
1. Go to directory paloPlugin
2. Run mvn install -DskipTests=true. This builds Jar file.
3. The location of the Jar file is configured in application-dev.yml. Copy built jar file to this location.


Deployment from OVA:

An OVA consisting of Ubuntu 16.04 LTS with application is available for download. (It is not in this Github repository). To setup a server, please follow below steps.

1. Deploy OVA
2. Boot the VM, Change the IP address of the server. Restart the server.
3. Access the application from the link http://<ip address>>:8080/

Refer userguide under docs directory for details.
