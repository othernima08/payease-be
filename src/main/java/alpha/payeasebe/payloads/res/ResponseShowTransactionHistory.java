package alpha.payeasebe.payloads.res;

import java.time.LocalDateTime;

public interface ResponseShowTransactionHistory {
    String getId();
    String getName();
    String getProfile_picture_url();
    String getType();
    LocalDateTime getTransaction_time();
    Double getAmount();
}
