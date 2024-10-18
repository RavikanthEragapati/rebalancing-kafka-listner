package com.launchiam.mpresultsanalyzer.listener;

import com.launchiam.mpresultsanalyzer.event.EventMessage;
import com.launchiam.mpresultsanalyzer.event.EventType;
import com.launchiam.mpresultsanalyzer.service.ListenerStartStopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.ExecutionException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RefreshRemoveTopicListener {

    private final ListenerStartStopService listenerStartStopService;

    @Value("${mp.results.analyzer.consumer.tenantResponseProcessorListener.id}")
    private String tenantResponseProcessorListenerName;

    @Value("${mp.results.analyzer.consumer.tenantResponseProcessorListener.group-id}")
    private String tenantResponseProcessorListenerGroupId;

    @KafkaListener(
            id = "${mp.results.analyzer.consumer.refreshRemoveTopicListener.id}",
            groupId = "${mp.results.analyzer.consumer.refreshRemoveTopicListener.group-id}",
            topics = "${mp.results.analyzer.consumer.refreshRemoveTopicListener.topicName}"
    )
    public void consumeMessage(EventMessage eventMessage) {

        log.info("RefreshRemoveTopicListener Event message : -> {}", eventMessage);
        try {
            if (EventType.REFRESH_CONSUMER_LIST.equals(eventMessage.getEventType())) {
                listenerStartStopService.unregisterListener(tenantResponseProcessorListenerName);
                listenerStartStopService.createNewListener(tenantResponseProcessorListenerName, tenantResponseProcessorListenerGroupId);
            } else if (EventType.REMOVE_DELETE_TOPIC.equals(eventMessage.getEventType())) {
                listenerStartStopService.unregisterListener(tenantResponseProcessorListenerName);
                listenerStartStopService.deleteTopic(eventMessage.getTopicNamesToDeleted());
            }
        } catch (ExecutionException e) {
            log.error("ExecutionException : ", e);
        } catch (InterruptedException e) {
            log.error("InterruptedException : ", e);
        }
    }
}
