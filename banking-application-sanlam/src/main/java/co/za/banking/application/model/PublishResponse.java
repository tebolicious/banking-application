package co.za.banking.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishResponse {
    boolean success;
    public PublishResponse sdkHttpResponse() {
        return new PublishResponse();
    }
    public boolean isSuccess() {
        return true;
    }
    }


