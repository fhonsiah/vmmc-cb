package org.smartregister.chw.vmmc.interactor;


import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.vmmc.VmmcLibrary;
import org.smartregister.chw.vmmc.actionhelper.VmmcProcedureActionHelper;
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

public class BaseVmmcVisitProcedureInteractor extends BaseVmmcVisitInteractor {

    protected BaseVmmcVisitContract.InteractorCallBack callBack;

    String visitType;
    private final VmmcLibrary vmmcLibrary;
    private final LinkedHashMap<String, BaseVmmcVisitAction> actionList;
    protected AppExecutors appExecutors;
    private ECSyncHelper syncHelper;
    private Context mContext;


    @VisibleForTesting
    public BaseVmmcVisitProcedureInteractor(AppExecutors appExecutors, VmmcLibrary VmmcLibrary, ECSyncHelper syncHelper) {
        this.appExecutors = appExecutors;
        this.vmmcLibrary = VmmcLibrary;
        this.syncHelper = syncHelper;
        this.actionList = new LinkedHashMap<>();
    }

    public BaseVmmcVisitProcedureInteractor(String visitType) {
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
                evaluateConsentForm(details);

            } catch (BaseVmmcVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void evaluateConsentForm(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcConsentFormActionHelper actionHelper = new VmmcConsentFormActionHelper(mContext, memberObject);
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.consent_form))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.VMMC_FOLLOWUP_FORMS.CONSENT_FORM)
                .build();
        actionList.put(context.getString(R.string.consent_form), action);

    }

    private void evaluateMcProcedure(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcProcedureActionHelper actionHelper = new VmmcProcedureActionHelper(mContext, memberObject);
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_mc_procedure))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.VMMC_FOLLOWUP_FORMS.MC_PROCEDURE)
                .build();
        actionList.put(context.getString(R.string.vmmc_mc_procedure), action);

    }


    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.VMMC_PROCEDURE;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.VMMC_PROCEDURE;
    }

    private class VmmcConsentFormActionHelper extends org.smartregister.chw.vmmc.actionhelper.VmmcConsentFormActionHelper {

        public VmmcConsentFormActionHelper(Context context, MemberObject memberObject) {
            super(context,memberObject);
        }

        @Override
        public String postProcess(String s) {
            if (consent_form.equalsIgnoreCase("yes")) {
                try {
                    evaluateMcProcedure(details);
                } catch (BaseVmmcVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }

            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }

    }

}

