package org.booktrace.app.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile(value = "local")
@Service
@Slf4j
public class ConsoleEmailService implements EmailService{
    @Override
    public void sendEMail(EmailMessage emailMessage) {
        log.info("sent Email : {}", emailMessage.getMessage());
    }
}
