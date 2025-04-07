package com.revature.util;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SendGridUtil {

    public void SendEmail(String subject, String emailTo, String text) {
        Properties props = new Properties();

        try{
            props.load(new FileReader("src/main/resources/application.properties"));

            SendGrid sg = new SendGrid(props.getProperty("SENDGRID_API_KEY"));
            Email from = new Email(props.getProperty("SENDGRID_EMAIL_FROM"));
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
