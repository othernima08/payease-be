package alpha.payeasebe.payloads.res;

import java.time.LocalDateTime;

public interface ResponseShowTopUpHistory {
    String getId();
    String getType();
    String getName();
    String getProfile_picture_url();
    String getStatus();
    LocalDateTime getTransaction_time();
    Double getAmount();
}
