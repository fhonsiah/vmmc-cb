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

public class VmmcPhysicalExamActionHelper implements BaseVmmcVisitAction.VmmcVisitActionHelper {

    protected String jsonPayload;

    protected String baseEntityId;

    protected String medical_history;

    protected static String genital_examination;

    protected static String diastolic;

    protected static String systolic;

    private HashMap<String, Boolean> checkObject = new HashMap<>();

    protected Context context;

    protected MemberObject memberObject;


    public VmmcPhysicalExamActionHelper(Context context, MemberObject memberObject) {
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

            String known_allergies = VmmcMedicalHistoryActionHelper
                    .known_allergies;

            global.put("known_allergies", known_allergies);

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
            JSONObject global = jsonObject.getJSONObject("global");

            genital_examination = JsonFormUtils.getValue(jsonObject, "genital_examination");
            global.put("contraindication", genital_examination);

            diastolic = JsonFormUtils.getValue(jsonObject, "diastolic");
            systolic = JsonFormUtils.getValue(jsonObject, "systolic");

            medical_history = JsonFormUtils.getValue(jsonObject, "physical_abnormality");

            checkObject.clear();

            checkObject.put("physical_abnormality", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "physical_abnormality")));
            checkObject.put("client_weight", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "client_weight")));
            checkObject.put("bmi", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "bmi")));
            checkObject.put("pulse_rate", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "pulse_rate")));
            checkObject.put("systolic", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "systolic")));
            checkObject.put("diastolic", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "diastolic")));
            checkObject.put("temperature", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "temperature")));
            checkObject.put("respiration_rate", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "respiration_rate")));
            checkObject.put("genital_examination", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "genital_examination")));
            checkObject.put("preferred_client_mc_method", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "preferred_client_mc_method")));

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
            JSONObject physcialExamCompletionStatus = JsonFormUtils.getFieldJSONObject(fields, "physical_exam_completion_status");
            assert physcialExamCompletionStatus != null;
            physcialExamCompletionStatus.put(com.vijay.jsonwizard.constants.JsonFormConstants.VALUE, VisitUtils.getActionStatus(checkObject));
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
