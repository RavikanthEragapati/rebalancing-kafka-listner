package com.launchiam.mpresultsanalyzer.service;

import com.launchiam.mpresultsanalyzer.listener.TenantResponseProcessorListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListenerStartStopService {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory;
    private final RetrieveResponseTopicNameService retrieveResponseTopicNameService;
    private final BeanFactory beanFactory;

    public void deleteTopic(List<String> topicNames){

    }

    public void unregisterListener(String listenerId) {
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
        if (Objects.nonNull(listenerContainer) && listenerContainer.isRunning()) {
            listenerContainer.stop();
            kafkaListenerEndpointRegistry.unregisterListenerContainer(listenerId);
        }
    }

    public void createNewListener(String listenerId, String groupId) throws ExecutionException, InterruptedException {
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
        if (Objects.isNull(listenerContainer)) {
            TenantResponseProcessorListener bean = beanFactory.getBean(TenantResponseProcessorListener.class);
            Map<Method, Set<KafkaListener>> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<Set<KafkaListener>>) method -> {
                        Set<KafkaListener> listenerMethods = findListenerAnnotations(method);
                        return (!listenerMethods.isEmpty() ? listenerMethods : null);
                    });
            List<Method> list = annotatedMethods.keySet().stream().toList();
            Method method = list.get(0);

            MethodKafkaListenerEndpoint<String, String> kafkaListenerEndpoint = new MethodKafkaListenerEndpoint<>();
            kafkaListenerEndpoint.setId(listenerId);
            kafkaListenerEndpoint.setTopics(retrieveResponseTopicNameService.getListResponseEntity("response-").toArray(new String[0]));
            kafkaListenerEndpoint.setGroupId(groupId);
            kafkaListenerEndpoint.setBean(bean);
            kafkaListenerEndpoint.setMethod(method);
            kafkaListenerEndpoint.setMessageHandlerMethodFactory(new DefaultMessageHandlerMethodFactory());
            kafkaListenerEndpointRegistry.registerListenerContainer(kafkaListenerEndpoint, concurrentKafkaListenerContainerFactory, true);
        } else {
            log.error("#################### Container Still Running ######################");
        }
        verifyListenerRunning(listenerId);
    }

    private void verifyListenerRunning(String listenerId) {
        MessageListenerContainer listenerContainer;
        listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
        if(Objects.nonNull(listenerContainer))
            log.info("{} status: {} ", listenerContainer.getListenerId(), listenerContainer.isRunning());
        else
            log.error("listenerContainer was NULL");
    }

    private Set<KafkaListener> findListenerAnnotations(Method method) {
        Set<KafkaListener> listeners = new HashSet<>();
        KafkaListener ann = AnnotatedElementUtils.findMergedAnnotation(method, KafkaListener.class);
        if (ann != null) {
            listeners.add(ann);
        }
        KafkaListeners anns = AnnotationUtils.findAnnotation(method, KafkaListeners.class);
        if (anns != null) {
            listeners.addAll(Arrays.stream(anns.value())
                    .toList());
        }
        return listeners;
    }
}
