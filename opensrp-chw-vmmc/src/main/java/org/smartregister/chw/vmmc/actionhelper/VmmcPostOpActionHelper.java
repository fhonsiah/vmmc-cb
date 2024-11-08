package org.smartregister.chw.vmmc.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.vmmc.dao.VmmcDao;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.domain.VisitDetail;
import org.smartregister.chw.vmmc.model.BaseVmmcVisitAction;
import org.smartregister.chw.vmmc.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

public class VmmcPostOpActionHelper implements BaseVmmcVisitAction.VmmcVisitActionHelper {

    protected String dressing_condition_in_relation_to_bleeding;

    protected String jsonPayload;

    protected Context context;

    protected MemberObject memberObject;

    public VmmcPostOpActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            JSONObject global = jsonObject.getJSONObject("global");

            String method_used_notify = VmmcDao.getMcMethodUsed(memberObject.getBaseEntityId());

            global.put("method_used", method_used_notify);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            if (JsonFormUtils.getValue(jsonObject, "dressing_condition_in_relation_to_bleeding").isEmpty()) {
                dressing_condition_in_relation_to_bleeding = JsonFormUtils.getValue(jsonObject, "device_mc");
            } else {
                dressing_condition_in_relation_to_bleeding = JsonFormUtils.getValue(jsonObject, "dressing_condition_in_relation_to_bleeding");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseVmmcVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return null;
    }

    @Override
    public String postProcess(String s) {
        return s;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseVmmcVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(dressing_condition_in_relation_to_bleeding))
            return BaseVmmcVisitAction.Status.PENDING;
        else {
            return BaseVmmcVisitAction.Status.COMPLETED;
        }
    }

    @Override
    public void onPayloadReceived(BaseVmmcVisitAction baseVmmcVisitAction) {
        //overridden
    }
}
