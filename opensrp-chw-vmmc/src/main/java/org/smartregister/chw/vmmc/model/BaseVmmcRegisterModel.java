package org.smartregister.chw.vmmc.model;

import org.json.JSONObject;
import org.smartregister.chw.vmmc.contract.VmmcRegisterContract;
import org.smartregister.chw.vmmc.util.VmmcJsonFormUtils;

public class BaseVmmcRegisterModel implements VmmcRegisterContract.Model {

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject jsonObject = VmmcJsonFormUtils.getFormAsJson(formName);
        VmmcJsonFormUtils.getRegistrationForm(jsonObject, entityId, currentLocationId);

        return jsonObject;
    }

}
