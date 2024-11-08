package org.smartregister.chw.vmmc.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.domain.VisitDetail;
import org.smartregister.chw.vmmc.model.BaseVmmcVisitAction;
import org.smartregister.chw.vmmc.util.JsonFormUtils;
import org.smartregister.chw.vmmc.util.VisitUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class VmmcProcedureActionHelper implements BaseVmmcVisitAction.VmmcVisitActionHelper {

    public static String method_used;

    protected String medical_history;

    protected String jsonPayload;

    protected Context context;

    protected MemberObject memberObject;

    private HashMap<String, Boolean> checkObject = new HashMap<>();

    public VmmcProcedureActionHelper(Context context, MemberObject memberObject) {
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

            checkObject.clear();

            checkObject.put("is_male_procedure_circumcision_conducted", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "is_male_procedure_circumcision_conducted")));
            checkObject.put("start_time", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "start_time")));
            checkObject.put("end_time", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "end_time")));
            checkObject.put("male_circumcision_method", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "male_circumcision_method")));
            checkObject.put("intraoperative_adverse_event_occured", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "intraoperative_adverse_event_occured")));


            method_used = JsonFormUtils.getValue(jsonObject, "male_circumcision_method");

            medical_history = JsonFormUtils.getValue(jsonObject, "is_male_procedure_circumcision_conducted");
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
    public String postProcess(String jsonPayload) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonPayload);
            JSONArray fields = JsonFormUtils.fields(jsonObject);
            JSONObject mcProcedureCompletionStatus = JsonFormUtils.getFieldJSONObject(fields, "mc_procedure_completion_status");
            assert mcProcedureCompletionStatus != null;
            mcProcedureCompletionStatus.put(com.vijay.jsonwizard.constants.JsonFormConstants.VALUE, VisitUtils.getActionStatus(checkObject));
        } catch (JSONException e) {
            Timber.e(e);
        }

        if (jsonObject != null) {
            return jsonObject.toString();
        }
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseVmmcVisitAction.Status evaluateStatusOnPayload() {
        String status = VisitUtils.getActionStatus(checkObject);

        if (status.equalsIgnoreCase(VisitUtils.Complete)) {
            return BaseVmmcVisitAction.Status.COMPLETED;
        }
        if (status.equalsIgnoreCase(VisitUtils.Ongoing)) {
            return BaseVmmcVisitAction.Status.PARTIALLY_COMPLETED;
        }
        return BaseVmmcVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseVmmcVisitAction baseVmmcVisitAction) {
        //overridden
    }
}
