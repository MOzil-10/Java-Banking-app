package banking.App.banking.app.services;

import banking.App.banking.app.dto.AccountDetails;
import banking.App.banking.app.dto.mapper.AccountMapper;
import banking.App.banking.app.dto.CreateAccountRequest;
import banking.App.banking.app.entity.Account;
import banking.App.banking.app.entity.Transaction;
import banking.App.banking.app.exception.AccountNotFoundException;
import banking.App.banking.app.exception.DuplicateAccountNumberException;
import banking.App.banking.app.repository.AccountRepository;
import banking.App.banking.app.repository.TransactionRepository;
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
    private final TransactionRepository transactionRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final int ACCOUNT_NUMBER_LENGTH = 12;
    private static final int MAX_GENERATION_ATTEMPTS = 5;

    public AccountServiceImplementation(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Creates a new account based on the provided account creation request.
     *
     * @param createAccountRequest the request object containing account creation details
     * @return AccountDetails containing the details of the newly created account
     */
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

    /**
     * Retrieves account details by account ID.
     *
     * @param id the ID of the account to retrieve
     * @return AccountDetails containing the account details
     * @throws AccountNotFoundException if the account with the given ID does not exist
     */
    @Override
    public AccountDetails getAccountById(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " does not exist"));

        return AccountMapper.mapToAccountDetails(account);
    }

    /**
     * Deposits an amount into the specified account.
     *
     * @param id     the ID of the account to deposit into
     * @param amount the amount to deposit
     * @return AccountDetails containing the updated account details
     * @throws IllegalArgumentException if the deposit amount is non-positive
     * @throws AccountNotFoundException if the account with the given ID does not exist
     */
    @Override
    @Transactional
    public AccountDetails deposit(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " does not exist"));

        BigDecimal totalBalance = account.getBalance().add(amount);
        account.setBalance(totalBalance.setScale(2, RoundingMode.HALF_UP));
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType("DEPOSIT");

        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDetails(savedAccount);
    }

    /**
     * Withdraws an amount from the specified account.
     *
     * @param id     the ID of the account to withdraw from
     * @param amount the amount to withdraw
     * @return AccountDetails containing the updated account details
     * @throws IllegalArgumentException if the withdrawal amount is non-positive or exceeds the account balance
     * @throws AccountNotFoundException if the account with the given ID does not exist
     */
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

        BigDecimal totalBalance = account.getBalance().subtract(amount);
        account.setBalance(totalBalance.setScale(2, RoundingMode.HALF_UP));
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType("WITHDRAW");

        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDetails(savedAccount);
    }

    /**
     * Retrieves all accounts in the system.
     *
     * @return a list of AccountDetails containing details of all accounts
     */
    @Override
    public List<AccountDetails> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(AccountMapper::mapToAccountDetails)
                .collect(Collectors.toList());
    }

    /**
     * Deletes the specified account.
     *
     * @param id the ID of the account to delete
     * @throws AccountNotFoundException if the account with the given ID does not exist
     */
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
     * @throws DuplicateAccountNumberException if a unique account number cannot be generated after the maximum number of attempts
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
