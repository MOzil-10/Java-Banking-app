package banking.App.banking.app.dto.mapper;

import banking.App.banking.app.dto.AccountDetails;
import banking.App.banking.app.dto.CreateAccountRequest;
import banking.App.banking.app.entity.Account;

public class AccountMapper {

    /**
     * Maps CreateAccountRequest DTO to Account entity.
     *
     * @param createAccountRequest the DTO containing account creation details
     * @return the Account entity
     */
    public static Account mapToAccount(CreateAccountRequest createAccountRequest) {
        Account account = new Account();
        account.setAccountHolderName(createAccountRequest.getAccountHolderName());
        return account;
    }

    /**
     * Maps Account entity to AccountDetails DTO with masked account number.
     *
     * @param account the Account entity
     * @return the AccountDetails DTO with masked account number
     */
    public static AccountDetails mapToAccountDetails(Account account) {
        String maskedAccountNumber = maskAccountNumber(account.getAccountNumber());

        return new AccountDetails(
                account.getId(),
                account.getAccountHolderName(),
                account.getBalance(),
                maskedAccountNumber
        );
    }

    /**
     * Masks the account number, showing only the last four digits.
     *
     * @param accountNumber the original account number
     * @return the masked account number
     */
    private static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }

        int unmaskedDigits = 4;
        StringBuilder masked = new StringBuilder();

        for (int i = 0; i < accountNumber.length() - unmaskedDigits; i++) {
            masked.append("*");
        }

        masked.append(accountNumber.substring(accountNumber.length() - unmaskedDigits));

        return masked.toString();
    }
}
