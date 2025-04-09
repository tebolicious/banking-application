package co.za.banking.application.service;

import java.math.BigDecimal;

public interface SnsClientService {
void publishWithdrawalEvent(Long accountId, Double amount, String message);
void notifyDevelopers(String issue);
}
