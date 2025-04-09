package co.za.banking.application.dao;


import java.math.BigDecimal;
import java.util.Optional;

public interface BalanceDao {

    Optional<Double> getAccountBalance(Long accountId);
    int updateAccountBalance(Long accountId, Double balance);
}
