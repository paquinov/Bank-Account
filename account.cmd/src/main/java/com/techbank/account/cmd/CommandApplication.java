package com.techbank.account.cmd;

import com.techbank.account.cmd.api.commands.CloseAccountCommand;
import com.techbank.account.cmd.api.commands.CommandHandler;
import com.techbank.account.cmd.api.commands.DepositFundsCommand;
import com.techbank.account.cmd.api.commands.OpenAccountCommand;
import com.techbank.account.cmd.api.commands.RestoreReadDbCommand;
import com.techbank.account.cmd.api.commands.WithdrawFundsCommand;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommandApplication {

	@Autowired
	private CommandDispatcher commandDispatcher;

	@Autowired
	private CommandHandler commandHandler;

	public static void main(String[] args) {
		SpringApplication.run(CommandApplication.class, args);
	}

	@PostConstruct
	public void registerHandlers() {
		commandDispatcher.registerHandler(OpenAccountCommand.class, command -> commandHandler.handle(command));
		commandDispatcher.registerHandler(DepositFundsCommand.class, command -> commandHandler.handle(command));
		commandDispatcher.registerHandler(WithdrawFundsCommand.class, command -> commandHandler.handle(command));
		commandDispatcher.registerHandler(CloseAccountCommand.class, command -> commandHandler.handle(command));
		commandDispatcher.registerHandler(RestoreReadDbCommand.class, command -> commandHandler.handle(command));
	}

}
