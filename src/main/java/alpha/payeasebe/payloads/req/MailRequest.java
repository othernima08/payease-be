package alpha.payeasebe.payloads.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailRequest {
    private String recipient;
    private String subject;
    private String message;
}
