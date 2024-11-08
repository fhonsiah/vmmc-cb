package org.smartregister.chw.vmmc.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
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

public class VmmcHtsActionHelper implements BaseVmmcVisitAction.VmmcVisitActionHelper {

    protected String medical_history;

    protected String jsonPayload;

    private HashMap<String, Boolean> checkObject = new HashMap<>();

    protected Context context;

    protected MemberObject memberObject;


    public VmmcHtsActionHelper(Context context, MemberObject memberObject) {
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

            int age = new Period(new DateTime(memberObject.getAge()),
                    new DateTime()).getYears();

            JSONArray fields = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);

            JSONObject actualAge = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "actual_age");
            actualAge.put(JsonFormUtils.VALUE, age);

            String is_client_diagnosed_with_any = VmmcMedicalHistoryActionHelper.is_client_diagnosed_with_any;
            String any_complaints = VmmcMedicalHistoryActionHelper.any_complaints;
            String known_allergies = VmmcMedicalHistoryActionHelper.known_allergies;
            String any_hematological_disease_symptoms = VmmcMedicalHistoryActionHelper.any_hematological_disease_symptoms;
            String complications_previous_surgical = VmmcMedicalHistoryActionHelper.complications_previous_surgical;
            String type_of_blood_for_glucose_test = VmmcMedicalHistoryActionHelper.type_of_blood_for_glucose_test;
            String blood_for_glucose = VmmcMedicalHistoryActionHelper.blood_for_glucose;
            String blood_for_glucose_test = VmmcMedicalHistoryActionHelper.blood_for_glucose_test;
            String diagonised_others = VmmcMedicalHistoryActionHelper.client_diagnosed_other;
            String genital_examination = VmmcPhysicalExamActionHelper.genital_examination;
            String diastolic = VmmcPhysicalExamActionHelper.diastolic;
            String systolic = VmmcPhysicalExamActionHelper.systolic;

            global.put("is_client_diagnosed_with_any", is_client_diagnosed_with_any);
            global.put("any_complaints", any_complaints);
            global.put("known_allergies", known_allergies);
            global.put("any_hematological_disease_symptoms", any_hematological_disease_symptoms);
            global.put("complications_previous_surgical", complications_previous_surgical);
            global.put("type_of_blood_for_glucose_test", type_of_blood_for_glucose_test);
            global.put("blood_for_glucose", blood_for_glucose);
            global.put("blood_for_glucose_test", blood_for_glucose_test);
            global.put("genital_examination", genital_examination);
            global.put("diastolic", diastolic);
            global.put("systolic", systolic);


            JSONObject toaster_hematological_disease_symptoms = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "toaster_notes_any_hematological_disease_symptoms");
            toaster_hematological_disease_symptoms.put("text", "Client has history of hematological disease "+ any_hematological_disease_symptoms);

            JSONObject toaster_genital_examination = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "toaster_notes_genital_examination");
            toaster_genital_examination.put("text", "After genital examination, client has a "+ genital_examination);

            JSONObject toaster_any_complaints = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "toaster_notes_any_complaints");
            toaster_any_complaints.put("text", " client has history of complaints "+ any_complaints);

            JSONObject toaster_known_allergies = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "toaster_notes_known_allergies");
            toaster_known_allergies.put("text", "Client is allerged to "+ known_allergies);

            JSONObject toaster_notes_is_client_diagnosed_with_any_other = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "toaster_notes_is_client_diagnosed_with_any_other");
            toaster_notes_is_client_diagnosed_with_any_other.put("text", "Client has "+ diagonised_others);

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
            medical_history = JsonFormUtils.getValue(jsonObject, "was_client_referred");

            checkObject.clear();

            checkObject.put("was_client_referred", StringUtils.isNotBlank(JsonFormUtils.getValue(jsonObject, "was_client_referred")));

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
            JSONObject htsCompletionStatus = JsonFormUtils.getFieldJSONObject(fields, "hts_completion_status");
            assert htsCompletionStatus != null;
            htsCompletionStatus.put(JsonFormConstants.VALUE, VisitUtils.getActionStatus(checkObject));
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
