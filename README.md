# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/#web)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

Start docker desktop
docker compose -f .\docker-compose.yml up -d
docker exec broker kafka-topics --bootstrap-server broker:29092 --create --topic messages-avro-topic
docker exec -it schema-registry bash
kafka-avro-console-producer --bootstrap-server broker:29092 --topic messages-avro-topic \
--property parse.key=true \
--property "key.separator=;" \
--property key.schema='{"type":"record","name":"message_key","fields":[{"name":"sender","type":"string"}, {"name":"sequence","type":"string"}]}' \
--property value.schema='{"type":"record","name":"message_value","fields":[{"name":"text","type":"string"}]}' \
--property schema.registry.url=http://localhost:8081
{"sender": "Pete", "sequence": "1"};{"text": "Hi"}
{"sender": "Pete", "sequence": "2"};{"text": "Hello"}
{"sender": "Roger", "sequence": "1"};{"text": "Good bye"}

kafka-avro-console-consumer --from-beginning --topic messages-avro-topic \
--bootstrap-server broker:29092 \
--property print.key=true \
--property "key.separator=;" \
--property schema.registry.url=http://localhost:8081

docker exec -it ksqldb-cli ksql http://ksqldb-server:8088

select * from messages;

./mvnw spring-boot:run

curl -v http://localhost:8080

curl -v http://localhost:8080/messages/Pete/1




