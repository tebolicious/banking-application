package co.za.banking.application.controller;

import co.za.banking.application.exception.AccountNotFoundException;
import co.za.banking.application.service.BankAccountServiceImpl;
import co.za.banking.application.service.BankAccountService;
import co.za.banking.application.service.MessageReprocessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST controller for managing bank account operations such as withdrawals.
 * This controller delegates business logic to {@link BankAccountServiceImpl}.
 */
@RestController
@RequestMapping("/bank")
@Tag(name = "Bank Account Controller", description = "APIs for managing bank account transactions.")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private MessageReprocessorService messageReprocessorService;

    /**
     * Withdraws a specific amount from the specified bank account.
     *
     * @param accountId the ID of the account from which to withdraw
     * @param amount    the amount to withdraw
     * @return a success or failure message wrapped in a {@link ResponseEntity}
     */
    @PostMapping("/withdraw")
    @Operation(
            summary = "Withdraw money from account",
            description = "Withdraws a given amount from a bank account if the balance is sufficient."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient balance"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> withdraw(
            @Parameter(description = "ID of the bank account") @RequestParam("accountId") Long accountId,
            @Parameter(description = "Amount to withdraw") @RequestParam("amount") Double amount
    ) {
        try {
            String message = bankAccountService.withdraw(accountId, amount);
            return ResponseEntity.ok(message);
        }catch (AccountNotFoundException exception){
         return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
    @PostMapping("/reprocess-failed")
    @Operation(
            summary = "Re process all failed messages",
            description = "This endpoint is for reprocessing all failed messages it can be runned manually or automatically using cron job"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reprocessing triggered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public String reprocessFailedMessages() {
        messageReprocessorService.reprocessFailedMessages();
        return "Reprocessing triggered.";
    }
}
