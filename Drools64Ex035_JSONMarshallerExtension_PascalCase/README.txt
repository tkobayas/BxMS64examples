CAUTION: This extension affects whole kie-server operations so very risky (= Client including business-central will need to use PascalCase). This is just for example

USAGE:
- Build a jar
- Copy the jar to kie-server.war/WEB-INF/lib and business-central.war/WEB-INF/lib
- Access like this:
~~~
curl -X POST -u 'kieserver:kieserver1!' 'http://localhost:8080/kie-server/services/rest/server/containers/instances/example:VariableTest_02254680:1.01' -H  'content-type: application/json' -d '{ "Commands":[ { "insert":{ "Object":{ "example.Person":{ "Name":"abc" } } } },{ "fire-all-rules":""} ] }'
~~~

Note you need to use PascalCase for "Commands", "Object" and pojo's properties ("Name")
