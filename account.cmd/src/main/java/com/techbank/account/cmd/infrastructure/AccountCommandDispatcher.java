package com.techbank.account.cmd.infrastructure;

import com.techbank.cqrs.core.commands.BaseCommand;
import com.techbank.cqrs.core.commands.CommandHandlerMethod;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AccountCommandDispatcher implements CommandDispatcher {

    private final Map<Class<? extends BaseCommand>, List<CommandHandlerMethod<? extends BaseCommand>>> routes = new HashMap<>();

    @Override
    public <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> handler) {
        routes.computeIfAbsent(type, c -> new LinkedList<>())
                .add(handler);
    }

    @Override
    public <T extends BaseCommand> void send(T command) {
        List<CommandHandlerMethod<? extends BaseCommand>> handlers = routes.get(command.getClass());
        if (CollectionUtils.isEmpty(handlers)) {
            throw new RuntimeException("No command handler was registered");
        }
        if (handlers.size() > 1) {
            throw new RuntimeException("Cannot send command to more than one handler");
        }
        ((CommandHandlerMethod<T>)handlers.get(0)).handle(command);
    }

}
