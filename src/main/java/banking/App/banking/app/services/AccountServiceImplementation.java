package banking.App.banking.app.services;

import banking.App.banking.app.dto.AccountDetails;
import banking.App.banking.app.dto.mapper.AccountMapper;
import banking.App.banking.app.dto.CreateAccountRequest;
import banking.App.banking.app.entity.Account;
import banking.App.banking.app.exception.AccountNotFoundException;
import banking.App.banking.app.exception.DuplicateAccountNumberException;
import banking.App.banking.app.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImplementation implements AccountService {

    private final AccountRepository accountRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final int ACCOUNT_NUMBER_LENGTH = 12;
    private static final int MAX_GENERATION_ATTEMPTS = 5;

    public AccountServiceImplementation(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public AccountDetails createAccount(CreateAccountRequest createAccountRequest) {
        Account account = new Account();
        account.setAccountHolderName(createAccountRequest.getAccountHolderName());

        String accountNumber = generateUniqueAccountNumber();
        account.setAccountNumber(accountNumber);

        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDetails(savedAccount);
    }

    @Override
    public AccountDetails getAccountById(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " does not exist"));

        return AccountMapper.mapToAccountDetails(account);
    }

    @Override
    @Transactional
    public AccountDetails deposit(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " does not exist"));

        BigDecimal totalBalance = account.getBalance().add(amount); // Use amount directly
        account.setBalance(totalBalance.setScale(2, RoundingMode.HALF_UP));
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDetails(savedAccount);
    }


    @Override
    @Transactional
    public AccountDetails withdraw(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " does not exist"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        BigDecimal totalBalance = account.getBalance().subtract(amount); // Use amount directly
        account.setBalance(totalBalance.setScale(2, RoundingMode.HALF_UP));
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDetails(savedAccount);
    }

    @Override
    public List<AccountDetails> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(AccountMapper::mapToAccountDetails)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Account with ID " + id + " does not exist");
        }
        accountRepository.deleteById(id);
    }

    /**
     * Generates a unique account number.
     *
     * @return a unique 12-digit account number
     */
    private String generateUniqueAccountNumber() {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            String accountNumber = generateAccountNumber();
            if (isAccountNumberUnique(accountNumber)) {
                return accountNumber;
            }
        }
        throw new DuplicateAccountNumberException("Failed to generate a unique account number after " + MAX_GENERATION_ATTEMPTS + " attempts");
    }

    /**
     * Generates a random 12-digit account number using SecureRandom.
     *
     * @return a 12-digit account number as a String
     */
    private String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            accountNumber.append(secureRandom.nextInt(10));
        }
        return accountNumber.toString();
    }

    /**
     * Checks if the generated account number is unique.
     *
     * @param accountNumber the account number to check
     * @return true if unique, false otherwise
     */
    private boolean isAccountNumberUnique(String accountNumber) {
        return !accountRepository.existsByAccountNumber(accountNumber);
    }
}
