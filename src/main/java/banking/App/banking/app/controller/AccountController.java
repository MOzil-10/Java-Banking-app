package banking.App.banking.app.controller;

import banking.App.banking.app.dto.AccountDetails;
import banking.App.banking.app.dto.TransactionRequest;
import banking.App.banking.app.dto.CreateAccountRequest;
import banking.App.banking.app.services.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@Validated
public class AccountController {

    private final AccountService accountService;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDetails> addAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest) {
        logger.info("Received request to create account for holder: {}", createAccountRequest.getAccountHolderName());

        try {
            AccountDetails accountDetails = accountService.createAccount(createAccountRequest);
            return new ResponseEntity<>(accountDetails, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating account: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDetails> getAccountById(@PathVariable Long id) {
        logger.info("Fetching account details for ID: {}", id);
        AccountDetails accountDetails = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDetails);
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<AccountDetails> deposit(@PathVariable Long id, @Valid @RequestBody TransactionRequest transactionRequest) {
        logger.info("Depositing amount: {} to account ID: {}", transactionRequest.getAmount(), id);
        AccountDetails accountDetails = accountService.deposit(id, transactionRequest.getAmount());
        return ResponseEntity.ok(accountDetails);
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<AccountDetails> withdraw(@PathVariable Long id, @Valid @RequestBody TransactionRequest transactionRequest) {
        logger.info("Withdrawing amount: {} from account ID: {}", transactionRequest.getAmount(), id);
        AccountDetails accountDetails = accountService.withdraw(id, transactionRequest.getAmount());
        return ResponseEntity.ok(accountDetails);
    }

    @GetMapping
    public ResponseEntity<List<AccountDetails>> getAllAccounts() {
        logger.info("Fetching all accounts");
        List<AccountDetails> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        logger.info("Deleting account with ID: {}", id);
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
