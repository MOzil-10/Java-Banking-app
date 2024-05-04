package banking.App.banking.app.controller;

import banking.App.banking.app.dto.AccountDetails;
import banking.App.banking.app.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    AccountController (AccountService accountService) {
        this.accountService = accountService;
    }

    //Add account REST API
    @PostMapping
    public ResponseEntity<AccountDetails> addAccount(@RequestBody AccountDetails accountDetails) {
        return new ResponseEntity<>(accountService.createAccount(accountDetails), HttpStatus.CREATED);
    }

    //Get account by ID Rest API
    @GetMapping("/{id}")
    public ResponseEntity<AccountDetails> getAccountById(@PathVariable long id) {
        AccountDetails accountDetails = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDetails);
    }

    //deposit Rest API
    @PutMapping("/{id}/deposit")
    public ResponseEntity<AccountDetails> deposit(@PathVariable Long id, @RequestBody Map<String, Double> request) {

        Double amount = request.get("amount");

       AccountDetails accountDetails = accountService.deposit(id, amount);
       return ResponseEntity.ok(accountDetails);
    }

    //withdraw Rest Api
    @PutMapping("{id}/withdraw")
    public ResponseEntity<AccountDetails> withdraw(@PathVariable  Long id,@RequestBody Map<String,Double> request) {
       Double amount = request.get("amount");

       AccountDetails accountDetails = accountService.withdraw(id, amount);
       return ResponseEntity.ok(accountDetails);
    }

    //Get all accounts Rest API
    @GetMapping
    public ResponseEntity<List<AccountDetails>> getAllAccounts() {
       List <AccountDetails> accounts = accountService.getAllAccounts();
       return ResponseEntity.ok(accounts);
    }

    //Delete Rest API
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account Deleted Successfully");
    }
}
