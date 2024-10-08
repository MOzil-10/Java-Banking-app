package banking.App.banking.app.services;

import banking.App.banking.app.dto.AccountDetails;
import banking.App.banking.app.dto.CreateAccountRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    AccountDetails createAccount(CreateAccountRequest createAccountRequest);

    AccountDetails getAccountById(Long id);

    AccountDetails deposit(Long id, BigDecimal amount);

    AccountDetails withdraw(Long id, BigDecimal amount);

    List<AccountDetails> getAllAccounts();

    void deleteAccount(Long id);


}
