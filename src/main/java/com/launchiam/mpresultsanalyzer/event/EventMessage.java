package com.launchiam.mpresultsanalyzer.event;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventMessage {
    private String eventId;
    private EventType eventType;
    private List<String> topicNamesToDeleted;
}
