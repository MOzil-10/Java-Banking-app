package banking.App.banking.app.controller;

import banking.App.banking.app.dto.AccountDetails;
import banking.App.banking.app.dto.TransactionDetails;
import banking.App.banking.app.dto.TransactionRequest;
import banking.App.banking.app.dto.CreateAccountRequest;
import banking.App.banking.app.entity.Transaction;
import banking.App.banking.app.exception.AccountNotFoundException;
import banking.App.banking.app.repository.TransactionRepository;
import banking.App.banking.app.services.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
@Validated
public class AccountController {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService, TransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Creates a new account based on the provided account details.
     *
     * @param createAccountRequest the request object containing account creation details
     * @return ResponseEntity containing the created AccountDetails and HTTP status code
     */
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

    /**
     * Retrieves account details by account ID.
     *
     * @param id the ID of the account to retrieve
     * @return ResponseEntity containing the AccountDetails and HTTP status code
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountDetails> getAccountById(@PathVariable Long id) {
        logger.info("Fetching account details for ID: {}", id);
        AccountDetails accountDetails = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDetails);
    }

    /**
     * Deposits an amount into the specified account.
     *
     * @param id                 the ID of the account to deposit into
     * @param transactionRequest the request object containing deposit details
     * @return ResponseEntity containing the updated AccountDetails and HTTP status code
     */
    @PutMapping("/{id}/deposit")
    public ResponseEntity<AccountDetails> deposit(@PathVariable Long id, @Valid @RequestBody TransactionRequest transactionRequest) {
        logger.info("Depositing amount: {} to account ID: {}", transactionRequest.getAmount(), id);
        AccountDetails accountDetails = accountService.deposit(id, transactionRequest.getAmount());
        return ResponseEntity.ok(accountDetails);
    }

    /**
     * Withdraws an amount from the specified account.
     *
     * @param id                 the ID of the account to withdraw from
     * @param transactionRequest the request object containing withdrawal details
     * @return ResponseEntity containing the updated AccountDetails and HTTP status code
     */
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<AccountDetails> withdraw(@PathVariable Long id, @Valid @RequestBody TransactionRequest transactionRequest) {
        logger.info("Withdrawing amount: {} from account ID: {}", transactionRequest.getAmount(), id);
        AccountDetails accountDetails = accountService.withdraw(id, transactionRequest.getAmount());
        return ResponseEntity.ok(accountDetails);
    }

    /**
     * Retrieves transaction history for a specified account ID.
     *
     * @param id the ID of the account for which to retrieve transaction history
     * @return ResponseEntity containing a list of TransactionDetails and HTTP status code
     */
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionDetails>> getTransactionHistory(@PathVariable Long id) {
        logger.info("Fetching transaction history for account ID: {}", id);

        List<Transaction> transactions = transactionRepository.findByAccountId(id);

        if (transactions.isEmpty()) {
            throw new AccountNotFoundException("Account with ID " + id + " not found.");
        }

        List<TransactionDetails> transactionDetails = transactions.stream()
                .map(transaction -> new TransactionDetails(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getTransactionType(),
                        transaction.getTimestamp()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(transactionDetails);
    }

    /**
     * Retrieves all accounts.
     *
     * @return ResponseEntity containing a list of AccountDetails and HTTP status code
     */
    @GetMapping
    public ResponseEntity<List<AccountDetails>> getAllAccounts() {
        logger.info("Fetching all accounts");
        List<AccountDetails> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Deletes the specified account.
     *
     * @param id the ID of the account to delete
     * @return ResponseEntity with no content and HTTP status code
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        logger.info("Deleting account with ID: {}", id);
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
