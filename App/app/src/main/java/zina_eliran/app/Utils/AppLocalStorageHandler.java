package zina_eliran.app.Utils;


import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;

public class AppLocalStorageHandler {

    static String FILENAME = "myUser.ser";
    static FileOutputStream fos;
    static FileInputStream fis;

    public static void saveUser(BEUser user) {

        try {

            // write object to file
            FileOutputStream fos = new FileOutputStream(FILENAME,false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
            oos.close();

        } catch (FileNotFoundException e1) {

        } catch (IOException e2) {

        } catch (Exception e4) {
            CMNLogHelper.logError("AppLocalStorageHandler", e4.getMessage());
        }
    }

    public static BEUser getUser() {

        BEUser user = null;
        try {

            // read object from file
            FileInputStream fis = new FileInputStream(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            user = (BEUser) ois.readObject();
            ois.close();

        } catch (FileNotFoundException e1) {

        } catch (IOException e2) {

        } catch (ClassNotFoundException e3) {

        } catch (Exception e4) {
            CMNLogHelper.logError("AppLocalStorageHandler", e4.getMessage());
        }

        return user;
    }

}
