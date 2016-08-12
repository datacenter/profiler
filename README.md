# Cisco Application Profiler
This directory contains all the source files related to Profiler

Pre-requisites: Install MySQL and create a database. Update username, password, and database name in application-dev.yml

Build:
1.Install Node components
2.Run mvn install -DskipTests=true This installs bower and other components and builds war file.
3.To run application locally, use the command, mvn spring-boot:run

Build: Paulo Alto Plugin:
1. Go to diectory paloPlugin
2. Run mvn install -DskipTests=true.  This builds Jar file.
