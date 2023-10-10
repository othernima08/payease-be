package alpha.payeasebe.services.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import alpha.payeasebe.payloads.req.MailRequest;

@Service
public class MailServiceImpl implements MailService {
    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public void sendMail(MailRequest request) {
       SimpleMailMessage mailMessage= new SimpleMailMessage();
       mailMessage.setTo(request.getRecipient());
       mailMessage.setSubject(request.getSubject());
       mailMessage.setText(request.getMessage());

       javaMailSender.send(mailMessage);
    }

}
