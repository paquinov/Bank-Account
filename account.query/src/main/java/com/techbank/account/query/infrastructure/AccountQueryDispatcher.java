package com.techbank.account.query.infrastructure;

import com.techbank.cqrs.core.domain.BaseEntity;
import com.techbank.cqrs.core.infrastructure.QueryDispatcher;
import com.techbank.cqrs.core.queries.BaseQuery;
import com.techbank.cqrs.core.queries.QueryHandlerMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AccountQueryDispatcher implements QueryDispatcher {

    private final Map<Class<? extends BaseQuery>, List<QueryHandlerMethod<? extends BaseQuery>>> routes = new HashMap<>();

    @Override
    public <T extends BaseQuery> void registerHandler(Class<T> type, QueryHandlerMethod<T> handler) {
        routes.computeIfAbsent(type, c -> new LinkedList<>())
                .add(handler);
    }

    @Override
    public <U extends BaseEntity, V extends BaseQuery> List<U> send(V query) {
        List<QueryHandlerMethod<? extends BaseQuery>> handlers = routes.get(query.getClass());
        if (CollectionUtils.isEmpty(handlers)) {
            throw new RuntimeException("No query handler was registered");
        }
        if (handlers.size() > 1) {
            throw new RuntimeException("Cannot send query to more than one handler");
        }
        return (List<U>) (((QueryHandlerMethod<V>)handlers.get(0)).handle(query));
    }
}
