# Getting Started

### Preliminary steps

1. Start docker desktop
1. In a terminal from the root directory of the project run `docker compose -f ./docker-compose.yml up -d`. Use `docker compose -f ./docker-compose.yml start` if containers have been already created
1. Once all the containers are up and running execute the command  `docker exec broker kafka-topics --bootstrap-server broker:29092 --create --topic messages-avro-topic` to create the kafka topic
1. Run `docker exec -it schema-registry bash`
1. The prompt [appuser@schema-registry ~]$ comes up
1. Type the following command to publish avro records into the topic  
   `kafka-avro-console-producer --bootstrap-server broker:29092 --topic messages-avro-topic --property parse.key=true --property "key.separator=;" --property key.schema='{"type":"record","name":"message_key","fields":[{"name":"sender","type":"string"}, {"name":"sequence","type":"string"}]}' --property value.schema='{"type":"record","name":"message_value","fields":[{"name":"text","type":"string"}]}' --property schema.registry.url=http://localhost:8081`  

   Once started, the process will wait for you to enter messages, one per line (no prompt, just a blank line), and will send them immediately when you hit the Enter key.  
   Here are 3 examples  
   {"sender": "Pete", "sequence": "1"};{"text": "Hi"}  
   {"sender": "Pete", "sequence": "2"};{"text": "Hello"}  
   {"sender": "Roger", "sequence": "1"};{"text": "Good bye"}  
1. Exit the process with Ctrl+C
1. Quit the schema-registry shell running the exit command
1. Type `docker exec -it ksqldb-cli ksql http://ksqldb-server:8088` to create a stream linked to the topic 
1. From the ksqldb> prompt run
   `CREATE STREAM messages (SENDER VARCHAR KEY, SEQUENCE VARCHAR KEY) WITH (KAFKA_TOPIC = 'messages-avro-topic', KEY_FORMAT = 'AVRO', VALUE_FORMAT = 'AVRO');`
1. Run `select * from messages;` just to check that the 3 records published to the kafka topic are in the stream too


### Run the web application
1. Start the application running the command `./mvnw spring-boot:run`
1. Open a new terminal and run `curl -v http://localhost:8080`; you will get 3 records previously published to the topic
1. Execute the command `docker compose -f ./docker-compose.yml -p ksqldb-demo stop` if you want to reuse generated sample data or `docker compose -f ./docker-compose.yml down` if you don't need to





