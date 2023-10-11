package alpha.payeasebe.services.mail;

import alpha.payeasebe.payloads.req.MailRequest;

public interface MailService {
    void sendMail(MailRequest request);
}
