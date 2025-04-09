package co.za.banking.application.service;

import co.za.banking.application.model.Balance;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class MessageReprocessorServiceImpl implements MessageReprocessorService {

    private final SnsClientService snsPublisherService; // Service responsible for sending to SNS
    private final Path failedLogPath = Paths.get("failed_messages.log");

    @Autowired
    public MessageReprocessorServiceImpl(SnsClientService snsPublisherService) {
        this.snsPublisherService = snsPublisherService;
    }

    public void reprocessFailedMessages() {
        try {
            if (!Files.exists(failedLogPath)) {
                log.info("No failed messages to reprocess.");
                return;
            }

            List<String> lines = Files.readAllLines(failedLogPath);

            for (String line : lines) {
                String messageJson = extractJson(line);
                messageJson = messageJson.replaceAll("(\\d+),(\\d+)", "$1.$2"); // Replace comma with dot only in numbers
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true); // Allows unquoted fields
                objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true); // Allows single quotes


                Balance balance = objectMapper.readValue(messageJson, Balance.class);
                if (messageJson != null) {
                    snsPublisherService.publishWithdrawalEvent(balance.getAccountId(),balance.getAmount(),balance.getStatus());
                    log.info("Successfully reprocessed message: {}", messageJson);

                }
            }

            // Optionally, clear the file after successful reprocessing
            Files.write(failedLogPath, new byte[0]); // clears the file

        } catch (IOException e) {
            log.error("Error reading failed messages log", e);
        }
    }

    private String extractJson(String line) {
        int jsonStart = line.indexOf("{");
        return (jsonStart >= 0) ? line.substring(jsonStart).trim() : null;
    }
}