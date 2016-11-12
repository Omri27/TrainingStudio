package zina_eliran.app.API.EmailSender;

import zina_eliran.app.BusinessEntities.CMNLogHelper;

/**
 * Created by Zina K on 10/30/2016.
 */

public class EmailSendThread implements Runnable{
    private String emailAdress;
    private String name;
    private String verificationCode;

    public EmailSendThread(String emailAdress, String name, String verificationCode){
        this.emailAdress = emailAdress;
        this.name = name;
        this.verificationCode = verificationCode;
    }

    @Override
    public void run() {
        try {
            EmailSender send = new EmailSender(emailAdress, name , verificationCode);
            //CMNLogHelper.logError("SendMailSucceed", "mail sent");
        } catch (Exception e) {
            CMNLogHelper.logError("SendMailFailed", e.getMessage());
        }
    }
}
