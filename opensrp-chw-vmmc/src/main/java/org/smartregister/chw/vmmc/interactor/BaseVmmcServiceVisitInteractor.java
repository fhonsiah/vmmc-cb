package org.smartregister.chw.vmmc.interactor;


import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.vmmc.R;
import org.smartregister.chw.vmmc.VmmcLibrary;
import org.smartregister.chw.vmmc.actionhelper.VmmcHtsActionHelper;
import org.smartregister.chw.vmmc.actionhelper.VmmcMedicalHistoryActionHelper;
import org.smartregister.chw.vmmc.actionhelper.VmmcPhysicalExamActionHelper;
import org.smartregister.chw.vmmc.contract.BaseVmmcVisitContract;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.domain.VisitDetail;
import org.smartregister.chw.vmmc.model.BaseVmmcVisitAction;
import org.smartregister.chw.vmmc.util.AppExecutors;
import org.smartregister.chw.vmmc.util.Constants;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class BaseVmmcServiceVisitInteractor extends BaseVmmcVisitInteractor {

    protected BaseVmmcVisitContract.InteractorCallBack callBack;

    String visitType;
    private final VmmcLibrary vmmcLibrary;
    private final LinkedHashMap<String, BaseVmmcVisitAction> actionList;
    protected AppExecutors appExecutors;
    private ECSyncHelper syncHelper;
    private Context mContext;


    @VisibleForTesting
    public BaseVmmcServiceVisitInteractor(AppExecutors appExecutors, VmmcLibrary VmmcLibrary, ECSyncHelper syncHelper) {
        this.appExecutors = appExecutors;
        this.vmmcLibrary = VmmcLibrary;
        this.syncHelper = syncHelper;
        this.actionList = new LinkedHashMap<>();
    }

    public BaseVmmcServiceVisitInteractor(String visitType) {
        this(new AppExecutors(), VmmcLibrary.getInstance(), VmmcLibrary.getInstance().getEcSyncHelper());
        this.visitType = visitType;
    }

    @Override
    protected String getCurrentVisitType() {
        if (StringUtils.isNotBlank(visitType)) {
            return visitType;
        }
        return super.getCurrentVisitType();
    }

    @Override
    protected void populateActionList(BaseVmmcVisitContract.InteractorCallBack callBack) {
        this.callBack = callBack;
        final Runnable runnable = () -> {
            try {
                evaluateVmmcMedicalHistory(details);
                evaluateVmmcPhysicalExam(details);
                evaluateVmmcHTS(details);

            } catch (BaseVmmcVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void evaluateVmmcMedicalHistory(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcMedicalHistoryActionHelper actionHelper = new VmmcMedicalHistory(mContext, memberObject);
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_medical_history))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.VMMC_FOLLOWUP_FORMS.MEDICAL_HISTORY)
                .build();
        actionList.put(context.getString(R.string.vmmc_medical_history), action);

    }

    private void evaluateVmmcPhysicalExam(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcPhysicalExamActionHelper actionHelper = new VmmcPhysicalExamActionHelper(mContext, memberObject);
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_physical_examination))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.VMMC_FOLLOWUP_FORMS.PHYSICAL_EXAMINATION)
                .build();
        actionList.put(context.getString(R.string.vmmc_physical_examination), action);
    }

    private void evaluateVmmcHTS(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcHtsActionHelper actionHelper = new VmmcHtsActionHelper(mContext, memberObject);
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_hts))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.VMMC_FOLLOWUP_FORMS.HTS)
                .build();
        actionList.put(context.getString(R.string.vmmc_hts), action);
    }

    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.VMMC_SERVICES;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.VMMC_SERVICE;
    }

    private class VmmcMedicalHistory extends org.smartregister.chw.vmmc.actionhelper.VmmcMedicalHistoryActionHelper {


        public VmmcMedicalHistory(Context context, MemberObject memberObject) {
            super(context, memberObject);
        }

        @Override
        public String postProcess(String s) {
            if (StringUtils.isNotBlank(medical_history)) {
                try {
                    evaluateVmmcPhysicalExam(details);
                    evaluateVmmcHTS(details);
                } catch (BaseVmmcVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }

    }

    private class VmmcPhysicalExamActionHelper extends org.smartregister.chw.vmmc.actionhelper.VmmcPhysicalExamActionHelper {

        public VmmcPhysicalExamActionHelper(Context context, MemberObject memberObject) {
            super(context, memberObject);
        }

        @Override
        public String postProcess(String s) {
            if (StringUtils.isNotBlank(medical_history)) {
                try {
                    evaluateVmmcHTS(details);
                } catch (BaseVmmcVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }

    }

}
