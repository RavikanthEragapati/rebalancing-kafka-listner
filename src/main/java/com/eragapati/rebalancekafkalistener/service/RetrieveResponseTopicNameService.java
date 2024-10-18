package com.eragapati.rebalancekafkalistener.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrieveResponseTopicNameService {

    private final KafkaAdmin kafkaAdmin;

    public List<String> getListResponseEntity(String prefix) throws ExecutionException, InterruptedException {
        List<String> listOfFilteredTopicNames;
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            if (StringUtils.isNotBlank(prefix))
                listOfFilteredTopicNames = adminClient.
                        listTopics().
                        names()
                        .get()
                        .stream()
                        .filter(s -> s.startsWith(prefix)).toList();
            else
                listOfFilteredTopicNames = adminClient
                        .listTopics()
                        .names()
                        .get()
                        .stream().toList();

            return listOfFilteredTopicNames;
        } catch (Exception e) {
            log.error("Failed to retrieve Topic List", e);
            throw e;
        }
    }

}
