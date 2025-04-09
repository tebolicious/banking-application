package co.za.banking.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawalEvent {
    private Double amount;
    private Long accountId;
    private String status;

    public String toJson() {
        return String.format("{\"accountId\":%d,\"amount\":%.2f,\"status\":\"%s\"}",
                accountId, amount, status);
    }
}