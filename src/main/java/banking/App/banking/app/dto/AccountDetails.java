package banking.App.banking.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDetails {

    private Long id;
    private String accountHolderName;
    private double balance;
    private String accountNumber;
}
