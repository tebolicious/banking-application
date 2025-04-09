package co.za.banking.application.service;

import java.math.BigDecimal;

public interface BankAccountService {
    String withdraw(Long accountId, Double amount);
}
