package co.za.banking.application.service;

import co.za.banking.application.model.PublishRequest;
import co.za.banking.application.model.PublishResponse;
import co.za.banking.application.model.WithdrawalEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@Slf4j
@Component
public class SnsClientServiceImpl implements SnsClientService {


    private final SnsClient snsClient;
    private final String snsTopicArn = "arn:aws:sns:YOUR_REGION:YOUR_ACCOUNT_ID:YOUR_TOPIC_NAME";
    private final Path failureLogPath = Paths.get("failed_messages.log");

    /**
     * Constructor for Spring to inject SNS client.
     *
     * @param snsClient the SNS client for publishing messages
     */
    @Autowired
    public SnsClientServiceImpl(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    /**
     * Publishes a withdrawal event to AWS SNS.
     *
     * @param accountId the ID of the account
     * @param amount    the withdrawn amount
     * @return status message of the publishing result
     */
    @Async
    @Override
    public void publishWithdrawalEvent(Long accountId, Double amount, String status) {
        WithdrawalEvent event = new WithdrawalEvent(amount, accountId, status);
        String message = event.toJson();
        //Retries 3 times with a 2-second delay between attempts.
        int maxRetries = 3;
        int retryDelayMs = 2000;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                //log attempts
                //log.info("First attempt " + attempt + " of " + maxRetries);
                PublishRequest request = PublishRequest.builder()
                        .topicArn(snsTopicArn)
                        .message(message)
                        .build();

                PublishResponse response = snsClient.publish(request);

                if (response.sdkHttpResponse().isSuccess()) {
                    log.info("SNS message published successfully on attempt {}: {}", attempt, message);
                    return;
                } else {
                    log.warn("Attempt {} - SNS publish response not successful: {}", attempt, response);
                }
            } catch (Exception e) {
                log.error("Attempt {} - Error publishing to SNS: {}", attempt, e.getMessage());
            }

            // Wait before retrying
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Retry sleep interrupted", ie);
                }
            }
        }

        // Final failure handling
        saveFailedMessageToFile(message);
        notifyDevelopers("SNS publish failed after " + maxRetries + " attempts");
    }
    /**
     * Logs failed SNS messages to a file for future retry or audit.
     *
     * @param message the failed message to log
     */
    private void saveFailedMessageToFile(String message) {
        try {
            String entry = LocalDateTime.now() + " - " + message + System.lineSeparator();
            Files.write(failureLogPath, entry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Failed to write failed SNS message to file", e);
        }
    }

    /**
     * Notifies developers of any critical failure during withdrawal or publishing.
     * Placeholder for real alerting (e.g., email, Slack, PagerDuty).
     *
     * @param issue description of the issue
     */
    public void notifyDevelopers(String issue) {
        log.warn("Developer notification: {}", issue);
        // TODO: Integrate with actual notification system (email/SMS/etc.)
    }
}
