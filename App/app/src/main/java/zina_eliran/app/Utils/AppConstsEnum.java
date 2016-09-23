package zina_eliran.app.Utils;

import android.content.Context;

import zina_eliran.app.BaseActivity;
import zina_eliran.app.R;

/**
 * Created by eli on 22/09/2016.
 */

public enum AppConstsEnum {



    /*App Common values*/
    userId {
        @Override
        public String toString() {
            return BaseActivity._getString(R.string.user_id);
        }
    },

    userVerificationCode {
        @Override
        public String toString() {
            return BaseActivity._getString(R.string.user_verification_code);
        }
    },

    /*Training list activity Intent display/hide elements values*/

    manageTrainingPermission {
        @Override
        public String toString() {
            return BaseActivity._getString(R.string.manage_training_permission);
        }
    },



    /* Registration activity states values*/

    userRegistrationPermission {
        @Override
        public String toString() {
            return BaseActivity._getString(R.string.user_registration_permission);
        }
    },

    userVerificationPermission {
        @Override
        public String toString() {
            return BaseActivity._getString(R.string.user_verification_permission);
        }
    }

}
