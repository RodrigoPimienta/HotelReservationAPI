package com.revature.util;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class SendGridUtil {

    @Value("${SENDGRID_API_KEY}")
    private String sendgridApiKey;

    @Value("${SENDGRID_EMAIL_FROM}")
    private String sendgridEmailFrom;

    public void SendEmail(String subject, String emailTo, String text) {
        try{

            SendGrid sg = new SendGrid(this.sendgridApiKey);
            Email from = new Email(this.sendgridEmailFrom);
            Email to = new Email(emailTo);
            Content content = new Content("text/plain", text);
            Mail mail = new Mail(from, subject, to, content);


            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());

        }catch (FileNotFoundException e) {
            // throw new RuntimeException(e);
            e.printStackTrace();
            System.out.println("Error sending the email!");
        } catch (IOException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
            System.out.println("Error sending the email!");
        }

    }

}
