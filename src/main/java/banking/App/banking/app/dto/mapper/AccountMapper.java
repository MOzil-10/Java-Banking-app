package banking.App.banking.app.dto.mapper;

import banking.App.banking.app.dto.AccountDetails;
import banking.App.banking.app.entity.Account;

public class AccountMapper {

    public static Account mapToAccount(AccountDetails accountDetails) {
        Long id = accountDetails.getId(); // Check if id is null
        Account account = new Account(
                id != null ? id : 0L, // Set default value if id is null
                accountDetails.getAccountHolderName(),
                accountDetails.getBalance()
        );
        return account;
    }

    public static AccountDetails mapToAccountDetails(Account account) {
        Long id = account.getId(); // Check if id is null
        AccountDetails accountDetails = new AccountDetails(
                id != null ? id : 0L, // Set default value if id is null
                account.getAccountHolderName(),
                account.getBalance()
        );
        return accountDetails;
    }
}
