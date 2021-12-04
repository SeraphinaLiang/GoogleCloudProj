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
    /**
     * @param userEmail send to who
     * @param subject   the topic of email
     * @param text      the content of email
     *                  API Key : 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
     */
    public boolean sendEmail(String userEmail, String subject, String text) {

        boolean success = false;
        Email from = new Email("yinqi.liang@student.kuleuven.be");
        Email to = new Email(userEmail);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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");
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            System.out.println(response.getStatusCode());
            if (response.getStatusCode() == 202) {
                success = true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return success;
    }

}
