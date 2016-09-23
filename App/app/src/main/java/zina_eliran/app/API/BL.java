package zina_eliran.app.API;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEUser;

public class BL {

    public static BEResponse registerUser(BEUser user){
      DAL.registerUser(user);
    return null;

    }

    public static BEResponse verifyUser(BEUser user){
        return null;
    }

    public static BEResponse getUser(String userId){
        return null;
    }

    public static BEResponse updateUser(BEUser user){
        return null;
    }


}
