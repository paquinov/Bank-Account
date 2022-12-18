package com.techbank.account.cmd.api.controllers;

import com.techbank.account.cmd.api.commands.CloseAccountCommand;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.exceptions.AggregateNotFoundException;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/closeBankAccount")
public class CloseAccountController {

    @Autowired
    private CommandDispatcher commandDispatcher;

    private final Logger logger = Logger.getLogger(CloseAccountController.class.getName());

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> closeBankAccount(@PathVariable("id") String id) {
        try {
            commandDispatcher.send(new CloseAccountCommand(id));
            return ResponseEntity.ok(new BaseResponse("Bank account closure request successfully done !"));
        } catch (IllegalStateException | AggregateNotFoundException e) {
            logger.log(Level.WARNING, MessageFormat.format("Client made a bad request: {0}", e.toString()));
            return ResponseEntity.badRequest().body(new BaseResponse(e.toString()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, MessageFormat.format("Error while processing request to withdraw funds to bank account with id: {0}", id));
            return ResponseEntity.internalServerError().body(new BaseResponse(e.toString()));
        }
    }

}
