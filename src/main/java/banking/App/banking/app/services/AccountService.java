package banking.App.banking.app.services;

import banking.App.banking.app.dto.AccountDetails;

import java.util.List;

public interface AccountService {

    AccountDetails createAccount(AccountDetails accountDetails);

    AccountDetails getAccountById(Long id);

    AccountDetails deposit(Long id,double amount);

    AccountDetails withdraw(Long id, double amount);

    List<AccountDetails> getAllAccounts();

    void deleteAccount(Long id);


}
