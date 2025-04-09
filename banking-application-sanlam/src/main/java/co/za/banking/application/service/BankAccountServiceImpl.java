package co.za.banking.application.service;

import co.za.banking.application.dao.BalanceDao;
import co.za.banking.application.exception.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service class responsible for handling bank account operations such as withdrawals
 * and publishing corresponding events to AWS SNS.
 */
@Slf4j
@Service
public class BankAccountServiceImpl implements BankAccountService {

    @Autowired
    private BalanceDao balanceDao;

    @Autowired
    private SnsClientService snsClientService;


    /**
     * Withdraws money from a given account and publishes an event to SNS.
     *
     * @param accountId the ID of the account
     * @param amount    the amount to withdraw
     * @return a response message indicating the result
     */
    public String withdraw(Long accountId, Double amount) {
        try {
            Optional<Double> balance = balanceDao.getAccountBalance(accountId);

            if (balance.isEmpty()) {
                throw new AccountNotFoundException("Account not found");
            }
            if (balance.get().compareTo(amount) < 0) {
                return "Insufficient funds";
            }

            int updated = balanceDao.updateAccountBalance(accountId,amount);

            if (updated == 0) {
                return "Withdrawal failed to update";
            }

            // The system should proceed with publishing the withdrawal event, even if publishing to SNS fails. In the event of a failure:
            // Log the error details for further inspection.
            // Notify the development team about the failure using the appropriate channel (e.g., log, email, Slack, etc.).
            // Persist the failed message to a file (e.g., failed_messages.log) to allow for future reprocessing
            snsClientService.publishWithdrawalEvent(accountId,amount,"SUCCESSFUL");

            return "Withdrawal successful";

        }
        catch (AccountNotFoundException e) {
            throw new AccountNotFoundException("Account not found");
        }
        catch (Exception e) {
            log.error("Error during withdrawal", e);
            snsClientService.notifyDevelopers("Withdrawal process failed: " + e.getMessage());
            return "Error occurred during withdrawal";
        }
    }



}
