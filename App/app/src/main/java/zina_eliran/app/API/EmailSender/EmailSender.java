package zina_eliran.app.API.EmailSender;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Zina K on 10/29/2016.
 */

public class EmailSender {
    private final String FROM_ADREESS = "runapp16@gmail.com";
    private final String FROM_ADREESS_PASSWORD = "c400versions";


    public EmailSender(String toAddress, String name, String code) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.debug", "true");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_ADREESS, FROM_ADREESS_PASSWORD);
                    }
                });
        session.setDebug(true);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_ADREESS));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toAddress));
            message.setSubject("Training • Studio | registration confirmation");
            String htmlBody =
                    "<div style='width:80%; height:400px; margin-auto;'>" +
                            "<div style='width: 100%; height: 28px; background:orange; color:white; font-weight:bold; text-align: center; padding-top:10px;'>" +
                            "TRAINING &middot; STUDIO" +
                            "</div>" +
                            "<div style='padding-left:25px; padding-top: 20px;'>" +
                            "Hi there, " +
                            "</div>" +
                            "<div style='padding-left:25px; padding-top: 8px;'>" +
                            "Welcome to Training Studio!" +
                            "</div>" +
                            "<div style='padding-left:25px; padding-top: 8px;'>" +
                            "You are one step to start create, join and have a lot of running using our app." +
                            "<br /><br />" +
                            "<u>To verify your email address please insert the code below: </u>" +
                            "<br />" +
                            "Verification code: <b>" + code + "</b>" +
                            "</div>" +
                            "<div style='padding-left:25px; padding-top: 25px;'>" +
                            "<b>Dear " + name + "</b>, if you have any questions, just drop us an email. We'd love to help!" +
                            "<br /><br />" +
                            "Happy training!" +
                            "</div>" +
                            "<div style='width: 100%; height: 3px; background:orange;  text-align: center; margin-top:25px; margin-bottom:25px;'>" +
                            "&middot;" +
                            "</div>" +
                            "</div>";

            //htmlBody = "Dear " + name + "\nThank you for using our app. Your verification code is " + code;
            message.setContent(htmlBody, "text/html");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}


//
//public class SendMailSSL {
//    public static void main(String[] args) {
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class",
//                "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.port", "465");
//
//        Session session = Session.getDefaultInstance(props,
//                new javax.mail.Authenticator() {
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication("username","password");
//                    }
//                });
//
//        try {
//
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress("from@no-spam.com"));
//            message.setRecipients(Message.RecipientType.TO,
//                    InternetAddress.parse("to@no-spam.com"));
//            message.setSubject("Testing Subject");
//            message.setText("Dear Mail Crawler," +
//                    "\n\n No spam to my email, please!");
//
//            Transport.send(message);
//
//            System.out.println("Done");
//
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
