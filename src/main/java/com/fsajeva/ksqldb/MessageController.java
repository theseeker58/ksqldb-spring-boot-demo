package com.fsajeva.ksqldb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.Row;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class MessageController {
    private Client ksqlClient;

    public MessageController(Client ksqlClient) {
        this.ksqlClient = ksqlClient;
    }

    @GetMapping("/")
    public List<Message> findAll() throws ExecutionException, InterruptedException, JsonProcessingException {
        String pullQuery = "SELECT * FROM MESSAGES;";
        BatchedQueryResult batchedQueryResult = ksqlClient.executeQuery(pullQuery);
        List<Row> resultRows = batchedQueryResult.get();
        System.out.println("Received results. Num rows: " + resultRows.size());
        for (Row row : resultRows) {
            System.out.println("Row: " + row.values());
        }
        return map(resultRows);
    }
    //Restituisce una lista di messaggi, data una chiave
    @GetMapping("/messages/{sender}/{sequence}")
    public List<Message> findByKey(@PathVariable String sender, @PathVariable String sequence) throws ExecutionException, InterruptedException, JsonProcessingException {
        List<Row> resultRows = findMessages(sender, sequence);
        return map(resultRows);
    }

    //Restituisce un messaggio, data una chiave
    @GetMapping("/message/{sender}/{sequence}")
    public ResponseEntity<Message> findOne(@PathVariable String sender, @PathVariable String sequence) throws ExecutionException, InterruptedException, JsonProcessingException {
        Message message = null;
        List<Row> resultRows = findMessages(sender, sequence);
        if (resultRows.size() == 1) {
            Row row = resultRows.get(0);
            message = map(row);
            return ResponseEntity.ok().body(message);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    private List<Row> findMessages(String sender, String sequence) throws ExecutionException, InterruptedException {
        ksqlClient.define("sender", sender);
        ksqlClient.define("sequence", sequence);
        String pullQuery = "SELECT * FROM MESSAGES WHERE SENDER = '${sender}' AND SEQUENCE ='${sequence}';";
        BatchedQueryResult batchedQueryResult = ksqlClient.executeQuery(pullQuery);
        return batchedQueryResult.get();

    }

    private List<Message> map(List<Row> resultRows) throws JsonProcessingException {
        List<Message> messages = new ArrayList<>();
        for (Row row : resultRows) {
            messages.add(map(row));
        }
        return messages;
    }
    private Message map(Row row) throws JsonProcessingException {
        String json = row.asObject().toJsonString();
        ObjectMapper mapper = JsonMapper
                .builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .build();
        return mapper.readValue(json, Message.class);
    }

}
