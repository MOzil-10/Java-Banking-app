package banking.App.banking.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountDetails {

    private Long id;
    private String accountHolderName;
    private BigDecimal balance;
    private String accountNumber;
}
