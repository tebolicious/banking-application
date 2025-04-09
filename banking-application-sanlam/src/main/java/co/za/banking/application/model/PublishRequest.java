package co.za.banking.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishRequest {
    private String topicArn;
    private String message;

//    private PublishRequest(Builder builder) {
//        this.topicArn = builder.topicArn;
//        this.message = builder.message;
//    }

//    public static Builder builder() {
//        return new Builder();
//    }
//
//    public String getTopicArn() {
//        return topicArn;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public static class Builder {
//        private String topicArn;
//        private String message;
//
//        public Builder topicArn(String topicArn) {
//            this.topicArn = topicArn;
//            return this;
//        }
//
//        public Builder message(String message) {
//            this.message = message;
//            return this;
//        }
//
//        public PublishRequest build() {
//            return new PublishRequest(this);
//        }
//    }
}
