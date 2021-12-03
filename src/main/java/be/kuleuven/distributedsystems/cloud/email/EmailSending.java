package be.kuleuven.distributedsystems.cloud.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;

public class EmailSending {
    public void sendEmail()  {
        Email from = new Email("yinqi.liang@student.kuleuven.be");
        String subject = "Sending with Twilio SendGrid is Fun";
        Email to = new Email("yinqi.liang@student.kuleuven.be");
        Content content = new Content("text/plain", "and easy to do anywhere, even with Java");
        Mail mail = new Mail(from, subject, to, content);

        //API Key : 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
        SendGrid sg = new SendGrid("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");
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
