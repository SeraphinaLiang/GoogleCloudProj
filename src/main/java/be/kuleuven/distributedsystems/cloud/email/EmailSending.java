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
     * API KEY : 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
     */
    public static boolean sendEmail(String userEmail, String subject, String text) {

        boolean success = false;
        Email from = new Email("mofan.deng@student.kuleuven.be");
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
