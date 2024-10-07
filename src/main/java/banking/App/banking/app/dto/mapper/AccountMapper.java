package banking.App.banking.app.dto.mapper;

import banking.App.banking.app.dto.AccountDetails;
import banking.App.banking.app.entity.Account;

public class AccountMapper {

    public static Account mapToAccount(AccountDetails accountDetails) {
        Long id = accountDetails.getId();
        Account account = new Account(
                id != null ? id : 0L,
                accountDetails.getAccountHolderName(),
                accountDetails.getBalance(),
                accountDetails.getAccountNumber()
        );
        return account;
    }

    public static AccountDetails mapToAccountDetails(Account account) {
        Long id = account.getId();
        AccountDetails accountDetails = new AccountDetails(
                id != null ? id : 0L,
                account.getAccountHolderName(),
                account.getBalance(),
                account.getAccountNumber()
        );
        return accountDetails;
    }
}
