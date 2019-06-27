package org.arxing.pagemanager.protocol;



import org.arxing.apiconnector.ResponseMap;

import java.util.Map;

public interface IPageFragment extends AnimPage {
    String getFragmentTag();

    void receiveData(ResponseMap responseMap, Map<String, Object> transferData);

    String getTargetLayoutTag();

    void setTargetLayoutTag(String layoutTag);

    void setIsBack(boolean isBack);

}
