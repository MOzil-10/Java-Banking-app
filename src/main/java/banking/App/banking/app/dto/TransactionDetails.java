package banking.App.banking.app.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetails {
    private Long id;
    private BigDecimal amount;
    private String transactionType;
    private LocalDateTime timestamp;
}
