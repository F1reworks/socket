# Extension for https://github.com/F1reworks/meetup
Create socket transport as extension for working war

## Requirements
* Java 8+
* Maven 3.3.+

## Getting started
1. Clone meetup project
2. Compile to get working meetup war file

## Add extension
1. Compile socket to jar file
2. Add jar file to app.war: 
- put folder structer WEB-INF/lib near app.war
- add socket.jar in lib folder
- use: jar vfu app.war WEB-INF