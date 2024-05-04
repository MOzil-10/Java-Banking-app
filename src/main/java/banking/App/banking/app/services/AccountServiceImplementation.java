package banking.App.banking.app.services;

import banking.App.banking.app.dto.AccountDetails;
import banking.App.banking.app.dto.mapper.AccountMapper;
import banking.App.banking.app.entity.Account;
import banking.App.banking.app.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImplementation implements AccountService{

    private final AccountRepository accountRepository;

    AccountServiceImplementation (AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDetails createAccount(AccountDetails accountDetails) {
        Account account = AccountMapper.mapToAccount(accountDetails);
        Account saveAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDetails(saveAccount);
    }

    @Override
    public AccountDetails getAccountById(Long id) {
       Account account = accountRepository
               .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));

    return AccountMapper.mapToAccountDetails(account);
    }

    @Override
    public AccountDetails deposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));

        double totalBalance =  account.getBalance() + amount;
        account.setBalance(totalBalance);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDetails(account);
    }

    @Override
    public AccountDetails withdraw(Long id, double amount) {
        Account account = accountRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));

        if(account.getBalance() < amount ) {
            throw new RuntimeException("Insufficient amount");
        }

        double totalBalance = account.getBalance() - amount;
        account.setBalance(totalBalance);
        Account savedAcount = accountRepository.save(account);
        return AccountMapper.mapToAccountDetails(account);
    }

    @Override
    public List<AccountDetails> getAllAccounts() {

        List <Account> account = accountRepository.findAll();
        return account.stream().map((accounts) -> AccountMapper.mapToAccountDetails(accounts))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Account does not exists"));

        accountRepository.deleteById(id);


    }
}
