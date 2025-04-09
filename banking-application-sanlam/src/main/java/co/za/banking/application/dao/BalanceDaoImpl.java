package co.za.banking.application.dao;

import co.za.banking.application.model.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class BalanceDaoImpl implements BalanceDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    String GET_ACCOUNT_BALANCE = "SELECT account_id,balance FROM accounts WHERE account_id = ?";
    String UPDATE_ACCOUNT_BALANCE = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";

    private Balance balance(ResultSet resultSet, int numRow) throws SQLException {
        return Balance.builder().accountId(resultSet.getLong(1)).amount(resultSet.getDouble(2)).build();
    }
    @Override
    public Optional<Double> getAccountBalance(Long accountId) {
        List<Balance> balance = jdbcTemplate.query(GET_ACCOUNT_BALANCE, this::balance,accountId);

        return balance.isEmpty()? Optional.empty() : Optional.of(balance.get(0).getAmount());
    }

    @Override
    public  int updateAccountBalance(Long accountId, Double balance) {
        return jdbcTemplate.update(UPDATE_ACCOUNT_BALANCE, balance, accountId);
    }


}
