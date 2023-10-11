package alpha.payeasebe.payloads.res;

import java.time.LocalDateTime;

public interface ResponseShowTopUpHistory {
    String getId();
    Double getAmount();
    LocalDateTime getTransaction_time();
    String getStatus();
}
