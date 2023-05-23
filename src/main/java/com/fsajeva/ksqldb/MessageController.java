package com.fsajeva.ksqldb;

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
    public List<Message> findAll() throws ExecutionException, InterruptedException {
        String pullQuery = "SELECT * FROM MESSAGES;";
        BatchedQueryResult batchedQueryResult = ksqlClient.executeQuery(pullQuery);
        List<Row> resultRows = batchedQueryResult.get();
        System.out.println("Received results. Num rows: " + resultRows.size());
        for (Row row : resultRows) {
            System.out.println("Row: " + row.values());
        }
        return map(resultRows);
    }

    @GetMapping("/messages/{sender}/{sequence}")
    public List<Message> findByKey(@PathVariable String sender, @PathVariable String sequence) throws ExecutionException, InterruptedException {
        ksqlClient.define("sender", sender);
        ksqlClient.define("sequence", sequence);
        String pullQuery = "SELECT * FROM MESSAGES WHERE SENDER = '${sender}' AND SEQUENCE ='${sequence}';";
        BatchedQueryResult batchedQueryResult = ksqlClient.executeQuery(pullQuery);
        List<Row> resultRows = batchedQueryResult.get();
        return map(resultRows);
    }
    @GetMapping("/message/{sender}/{sequence}")
    public ResponseEntity<Message> findOne(@PathVariable String sender, @PathVariable String sequence) throws ExecutionException, InterruptedException {
        ksqlClient.define("sender", sender);
        ksqlClient.define("sequence", sequence);
        String pullQuery = "SELECT * FROM MESSAGES WHERE SENDER = '${sender}' AND SEQUENCE ='${sequence}';";
        BatchedQueryResult batchedQueryResult = ksqlClient.executeQuery(pullQuery);
        List<Row> resultRows = batchedQueryResult.get();
        Message message = null;
        if (resultRows.size() == 1) {
            Row row = resultRows.get(0);
            message = map(row);
            return ResponseEntity.ok().body(message);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    private List<Message> map(List<Row> resultRows) {
        List<Message> messages = new ArrayList<>();
        for (Row row : resultRows) {
            messages.add(map(row));
        }
        return messages;
    }
    private Message map(Row row) {
        Message message = new Message();
        message.setSender(row.getString("SENDER"));
        message.setSequence(row.getString("SEQUENCE"));
        message.setText(row.getString("TEXT"));
        return message;
    }

}
