package com.launchiam.mpresultsanalyzer.controller;

import com.launchiam.mpresultsanalyzer.event.EventMessage;
import com.launchiam.mpresultsanalyzer.service.RetrieveResponseTopicNameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/tenant/topic")
public class TopicDetailsController {

    private final RetrieveResponseTopicNameService retrieveResponseTopicNameService;
    private final KafkaTemplate kafkaTemplate;

    @GetMapping(path = "/list")
    public ResponseEntity<List<String>> retrieveTopicList(@RequestParam(name = "prefix", required = false) String prefix) {
        try {
            return ResponseEntity.ok(retrieveResponseTopicNameService.getListResponseEntity(prefix));
        } catch (ExecutionException | InterruptedException e) {
            log.error("TopicDetailsController::retrieveTopicList", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(path = "/publish")
    public ResponseEntity<String> publishMessage(@RequestHeader String topicName,
                                                 @RequestBody EventMessage eventMessage) {
        kafkaTemplate.send(topicName, eventMessage);
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping(path = "/refresh")
    public ResponseEntity<String> refreshTopicNameList() {
//        kafkaTemplate.send("", eventMessage);
        return ResponseEntity.ok("SUCCESS");
    }
}
