package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.domain.AggregateRoot;
import com.techbank.cqrs.core.events.BaseEvent;
import com.techbank.cqrs.core.handlers.EventSourcingHandler;
import com.techbank.cqrs.core.infrastructure.EventStore;
import com.techbank.cqrs.core.producers.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {

    private final EventStore eventStore;
    private final EventProducer eventProducer;

    @Override
    public void save(AggregateRoot aggregate) {
        eventStore.saveEvents(aggregate.getId(), aggregate.getUncommittedChanges(), aggregate.getVersion());
        aggregate.markChangesAsCommitted();
    }

    @Override
    public AccountAggregate getById(String aggregateId) {
        AccountAggregate accountAggregate = new AccountAggregate();
        List<BaseEvent> savedEvents = eventStore.getEvents(aggregateId);
        if (!CollectionUtils.isEmpty(savedEvents)) {
            accountAggregate.replayEvents(savedEvents);
            Optional<Integer> latestVersionOpt = savedEvents.stream()
                                                            .map(BaseEvent::getVersion)
                                                            .max(Comparator.naturalOrder());
            accountAggregate.setVersion(latestVersionOpt.get());
        }
        return accountAggregate;
    }

    @Override
    public void republishEvents() {
        List<String> aggregateIds = eventStore.getAggregateIds();
        aggregateIds.stream()
                    .map(this::getById)
                    .filter(aggregate -> Objects.nonNull(aggregate) && aggregate.getActive())
                    .map(aggregate -> eventStore.getEvents(aggregate.getId()))
                    .flatMap(Collection::stream)
                    .forEach(event -> eventProducer.produce(event.getClass().getSimpleName(), event));
    }

}
