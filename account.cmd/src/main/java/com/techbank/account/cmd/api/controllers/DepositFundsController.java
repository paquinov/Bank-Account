package com.techbank.account.cmd.api.controllers;

import com.techbank.account.cmd.api.commands.DepositFundsCommand;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.exceptions.AggregateNotFoundException;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/depositFunds")
public class DepositFundsController {

    @Autowired
    private CommandDispatcher commandDispatcher;

    private final Logger logger = Logger.getLogger(DepositFundsController.class.getName());

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> depositFunds(@PathVariable("id") String id, @RequestBody DepositFundsCommand command) {
        try {
            command.setId(id);
            commandDispatcher.send(command);
            return ResponseEntity.ok(new BaseResponse("Deposit funds request was successful !"));
        } catch (IllegalStateException | AggregateNotFoundException e) {
            logger.log(Level.WARNING, MessageFormat.format("Client made a bad request: {0}", e.toString()));
            return ResponseEntity.badRequest().body(new BaseResponse(e.toString()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, MessageFormat.format("Error while processing request to deposit funds to bank account with id: {0}", id));
            return ResponseEntity.internalServerError().body(new BaseResponse(e.toString()));
        }
    }

}
