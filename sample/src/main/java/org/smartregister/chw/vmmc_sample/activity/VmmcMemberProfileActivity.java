package org.smartregister.chw.vmmc_sample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import org.json.JSONObject;
import org.smartregister.chw.vmmc.activity.BaseVmmcProfileActivity;
import org.smartregister.chw.vmmc.dao.VmmcDao;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.domain.Visit;
import org.smartregister.chw.vmmc.util.Constants;

import timber.log.Timber;


public class VmmcMemberProfileActivity extends BaseVmmcProfileActivity {
    private Visit serviceVisit = null;
    private Visit procedureVisit = null;
    private Visit dischargeVisit = null;
    private Visit followupVisit = null;
    private Visit notifiableVisit = null;

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, VmmcMemberProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivity(intent);
    }

    @Override
    protected MemberObject getMemberObject(String baseEntityId) {
        return EntryActivity.getSampleMember();
    }

    @Override
    public void openFollowupVisit() {
        VmmcServiceActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    public void startServiceForm() {
        VmmcServiceActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    public void startNotifiableForm() {
        VmmcNotifiableAdverseActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    public void startFollowUp() {
        VmmcFollowUpActivity.startVmmcVisitActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    public void startProcedure() {
        VmmcProcedureActivity.startVmmcVisitProcedureActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    public void startDischarge() {
        VmmcDischargeActivity.startVmmcVisitDischargeActivity(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    public void continueService() {

    }

    @Override
    public void continueProcedure() {

    }

    @Override
    public void continueDischarge() {

    }

    @Override
    protected Visit getServiceVisit() {
        return serviceVisit;
    }

    @Override
    protected Visit getVmmcProcedureVisit() {
        return procedureVisit;
    }

    @Override
    protected Visit getVmmcDischargeVisit() {
        return dischargeVisit;
    }

    @Override
    protected Visit getFollowupVisit() {
        return followupVisit;
    }

    @Override
    protected Visit getNotifiableVisit() {
        return notifiableVisit;
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        delayRefreshSetupViews();
    }


    private void delayRefreshSetupViews() {
        try {
            new Handler(Looper.getMainLooper()).postDelayed(this::setupViews, 300);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected boolean checkContraIndications() {
        return false;
    }

    @Override
    protected void processVmmcProcedure() {
        textViewRecordVmmc.setVisibility(View.GONE);
        textViewProcedureVmmc.setVisibility(View.GONE);
        textViewDischargeVmmc.setVisibility(View.VISIBLE);
    }

    @Override
    protected void processVmmcDischarge() {
            textViewRecordVmmc.setVisibility(View.GONE);
            textViewProcedureVmmc.setVisibility(View.GONE);
            textViewDischargeVmmc.setVisibility(View.GONE);
            textViewFollowUpVmmc.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(Constants.JSON_FORM_EXTRA.EVENT_TYPE);
                switch (encounterType) {
                    case Constants.EVENT_TYPE.VMMC_SERVICES:
                        serviceVisit = new Visit();
                        serviceVisit.setProcessed(true);
                        serviceVisit.setJson(jsonString);
                        break;
                    case Constants.EVENT_TYPE.VMMC_PROCEDURE:
                        procedureVisit = new Visit();
                        procedureVisit.setProcessed(true);
                        procedureVisit.setProcessed(true);
                        procedureVisit.setJson(jsonString);
                        break;
                    case Constants.EVENT_TYPE.VMMC_DISCHARGE:
                        dischargeVisit = new Visit();
                        dischargeVisit.setProcessed(true);
                        dischargeVisit.setJson(jsonString);

                        notifiableVisit = new Visit();
                        notifiableVisit.setProcessed(true);
                        notifiableVisit.setJson(jsonString);
                        break;
                    case Constants.EVENT_TYPE.VMMC_FOLLOW_UP_VISIT:
                        followupVisit = new Visit();
                        followupVisit.setProcessed(true);
                        followupVisit.setJson(jsonString);

                        notifiableVisit = new Visit();
                        notifiableVisit.setProcessed(true);
                        notifiableVisit.setJson(jsonString);
                        break;


                }
            } catch (Exception e) {
                Timber.e(e);
            }

        }

    }
}