package com.launchiam.mpresultsanalyzer.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TenantResponseProcessorListener {

    @KafkaListener(
            id = "${mp.results.analyzer.consumer.tenantResponseProcessorListener.id}",
            groupId = "${mp.results.analyzer.consumer.tenantResponseProcessorListener.group-id}",
            topics = "#{retrieveResponseTopicNameService.getListResponseEntity(\"response-\")}"
    )
    public void consumeMessage(String message) {
        log.info("Message received : -> {}", message);
    }
}
