package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.account.cmd.domain.EventStoreRepository;
import com.techbank.cqrs.core.events.BaseEvent;
import com.techbank.cqrs.core.events.EventModel;
import com.techbank.cqrs.core.exceptions.AggregateNotFoundException;
import com.techbank.cqrs.core.exceptions.ConcurrencyException;
import com.techbank.cqrs.core.infrastructure.EventStore;
import com.techbank.cqrs.core.producers.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountEventStore implements EventStore {

    private final EventStoreRepository eventStoreRepository;
    private final EventProducer eventProducer;

    @Override
    public void saveEvents(String aggregateId, Iterable<BaseEvent> events, int expectedVersion) {
        final List<EventModel> savedEvents = eventStoreRepository.findByAggregateIdentifier(aggregateId);
        if ((expectedVersion != -1) && (savedEvents.get(savedEvents.size()-1).getVersion() != expectedVersion)) {
            throw new ConcurrencyException();
        }
        int version = expectedVersion;
        for (BaseEvent event : events) {
            version++;
            event.setVersion(version);
            EventModel savedEvent = eventStoreRepository.save(EventModel.builder()
                                                                        .timestamp(new Date())
                                                                        .aggregateIdentifier(aggregateId)
                                                                        .aggregateType(AccountAggregate.class.getTypeName())
                                                                        .version(version)
                                                                        .eventType(event.getClass().getTypeName())
                                                                        .eventData(event)
                                                                        .build());
            if (!savedEvent.getId().isEmpty()) { // If the event was persisted correctly
                eventProducer.produce(event.getClass().getSimpleName(), event);
            }
        }
    }

    @Override
    public List<BaseEvent> getEvents(String aggregateId) {
        final List<EventModel> savedEvents = eventStoreRepository.findByAggregateIdentifier(aggregateId);
        if (CollectionUtils.isEmpty(savedEvents)) {
            throw new AggregateNotFoundException("Incorrect account ID provided");
        }
        return savedEvents.stream()
                            .map(EventModel::getEventData)
                            .collect(Collectors.toList());
    }

    @Override
    public List<String> getAggregateIds() {
        List<EventModel> eventsList = eventStoreRepository.findAll();
        if (CollectionUtils.isEmpty(eventsList)) {
            throw new IllegalStateException("Could not retrieve event stream from event store");
        }
        return eventsList.stream()
                        .map(EventModel::getAggregateIdentifier)
                        .distinct()
                        .collect(Collectors.toList());
    }

}
