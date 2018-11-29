#env properties
KIE_SERVER_URL="http://localhost:8080/kie-server/services/rest/server"
KIE_SERVER_USERNAME="kieserver"
KIE_SERVER_PASSWORD="kieserver1!"

(cd async-mi-test && mvn clean install)
(cd decision-client && mvn -Dkie-server-url=$KIE_SERVER_URL -Dkie-server-username=$KIE_SERVER_USERNAME -Dkie-server-password=$KIE_SERVER_PASSWORD clean install exec:java)
