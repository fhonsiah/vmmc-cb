package org.smartregister.chw.vmmc.interactor;


import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.vmmc.VmmcLibrary;
import org.smartregister.chw.vmmc.actionhelper.VmmcFirstVitalActionHelper;
import org.smartregister.chw.vmmc.actionhelper.VmmcNotifiableAdverseActionHelper;
import org.smartregister.chw.vmmc.actionhelper.VmmcPostOpActionHelper;
import org.smartregister.chw.vmmc.actionhelper.VmmcSecondVitalActionHelper;
import org.smartregister.chw.vmmc.contract.BaseVmmcVisitContract;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.domain.VisitDetail;
import org.smartregister.chw.vmmc.model.BaseVmmcVisitAction;
import org.smartregister.chw.vmmc.util.AppExecutors;
import org.smartregister.chw.vmmc.util.Constants;
import org.smartregister.chw.vmmc.R;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class BaseVmmcVisitDischargeInteractor extends BaseVmmcVisitInteractor {

    protected BaseVmmcVisitContract.InteractorCallBack callBack;

    String visitType;
    private final VmmcLibrary vmmcLibrary;
    private final LinkedHashMap<String, BaseVmmcVisitAction> actionList;
    protected AppExecutors appExecutors;
    private ECSyncHelper syncHelper;
    private Context mContext;

    @VisibleForTesting
    public BaseVmmcVisitDischargeInteractor(AppExecutors appExecutors, VmmcLibrary VmmcLibrary, ECSyncHelper syncHelper) {
        this.appExecutors = appExecutors;
        this.vmmcLibrary = VmmcLibrary;
        this.syncHelper = syncHelper;
        this.actionList = new LinkedHashMap<>();
    }

    public BaseVmmcVisitDischargeInteractor(String visitType) {
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
                evaluatePostForm(details);
                evaluateFirstVitalProcedure(details);
                evaluateSecondVital(details);
                evaluateVmmcDischarge(details);

            } catch (BaseVmmcVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void evaluatePostForm(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcPostOpActionHelper actionHelper = new VmmcPostOpActionHelper(mContext, memberObject);
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_post))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.VMMC_FOLLOWUP_FORMS.POST_OP)
                .build();
        actionList.put(context.getString(R.string.vmmc_post), action);

    }

    private void evaluateFirstVitalProcedure(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcFirstVitalActionHelper actionHelper = new VmmcFirstVitalActionHelper();
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_first_vital))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.VMMC_FOLLOWUP_FORMS.FIRST_VITAL_SIGN)
                .build();
        actionList.put(context.getString(R.string.vmmc_first_vital), action);
    }

    private void evaluateSecondVital(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcSecondVitalActionHelper actionHelper = new VmmcSecondVitalActionHelper();
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_second_vital))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.VMMC_FOLLOWUP_FORMS.SECOND_VITAL_SIGN)
                .build();
        actionList.put(context.getString(R.string.vmmc_second_vital), action);
    }

    private void evaluateVmmcDischarge(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcDischargeActionHelper actionHelper = new VmmcDischargeActionHelper();
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_post_discharge))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.VMMC_FOLLOWUP_FORMS.DISCHARGE)
                .build();

        actionList.put(context.getString(R.string.vmmc_post_discharge), action);
    }

    private void evaluateVmmcNAE(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcNotifiableAdverseActionHelper actionHelper = new VmmcNotifiableAdverseActionHelper(mContext, memberObject);
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_notifiable_adverse))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withProcessingMode(BaseVmmcVisitAction.ProcessingMode.SEPARATE)
                .withFormName(Constants.FORMS.VMMC_NOTIFIABLE)
                .build();
        actionList.put(context.getString(R.string.vmmc_notifiable_adverse), action);

    }

    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.VMMC_DISCHARGE;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.VMMC_ENROLLMENT;
    }

    private class VmmcDischargeActionHelper extends org.smartregister.chw.vmmc.actionhelper.VmmcDischargeActionHelper {
        @Override
        public String postProcess(String s) {
            if (notifiable_adverse_event_occured.equalsIgnoreCase("yes")) {
                try {
                    evaluateVmmcNAE(details);
                } catch (BaseVmmcVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }
            else {
                actionList.remove(context.getString(R.string.vmmc_notifiable_adverse));
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }
    }

    private class VmmcFirstVitalActionHelper extends org.smartregister.chw.vmmc.actionhelper.VmmcFirstVitalActionHelper {

        @Override
        public String postProcess(String s) {
            if (StringUtils.isNotBlank(time_taken)) {
                try {
                    evaluateSecondVital(details);
                } catch (BaseVmmcVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }
    }
}
