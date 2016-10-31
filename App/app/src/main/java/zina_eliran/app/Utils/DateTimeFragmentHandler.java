package zina_eliran.app.Utils;

import java.util.Calendar;

import zina_eliran.app.BusinessEntities.BEFragmentResultTypeEnum;


public interface DateTimeFragmentHandler {
    public void onFragmentCallback(Calendar value, BEFragmentResultTypeEnum entityType);
}
