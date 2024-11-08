package org.smartregister.chw.vmmc.interactor;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.vmmc.VmmcLibrary;
import org.smartregister.chw.vmmc.actionhelper.VmmcNotifiableAdverseActionHelper;
import org.smartregister.chw.vmmc.contract.BaseVmmcVisitContract;
import org.smartregister.chw.vmmc.contract.BaseVmmcVisitContract.InteractorCallBack;
import org.smartregister.chw.vmmc.dao.VmmcDao;
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

public class BaseVmmcFollowUpInteractor extends BaseVmmcVisitInteractor {

    protected BaseVmmcVisitContract.InteractorCallBack callBack;

    String visitType;
    private final VmmcLibrary vmmcLibrary;
    private final LinkedHashMap<String, BaseVmmcVisitAction> actionList;
    protected AppExecutors appExecutors;
    private ECSyncHelper syncHelper;
    private Context mContext;

    @VisibleForTesting
    public BaseVmmcFollowUpInteractor(AppExecutors appExecutors, VmmcLibrary VmmcLibrary, ECSyncHelper syncHelper) {
        this.appExecutors = appExecutors;
        this.vmmcLibrary = VmmcLibrary;
        this.syncHelper = syncHelper;
        this.actionList = new LinkedHashMap<>();
    }

    public BaseVmmcFollowUpInteractor(String visitType) {
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
    protected void populateActionList(InteractorCallBack callBack) {
        this.callBack = callBack;
        final Runnable runnable = () -> {
            try {
                evaluateVisitType(details);

            } catch (BaseVmmcVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void evaluateVisitType(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcFollowUpActionHelper actionHelper = new VmmcFollowUpActionHelper(memberObject.getBaseEntityId());
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_followup_visit))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.FORMS.VMMC_FOLLOW_UP_VISIT)
                .build();
        actionList.put(context.getString(R.string.vmmc_followup_visit), action);

    }

    private void evaluateVmmcNAE(Map<String, List<VisitDetail>> details) throws BaseVmmcVisitAction.ValidationException {

        VmmcNotifiableAdverseActionHelper actionHelper = new VmmcNotifiableAdverseActionHelper(mContext, memberObject);
        BaseVmmcVisitAction action = getBuilder(context.getString(R.string.vmmc_notifiable_adverse))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withProcessingMode(BaseVmmcVisitAction.ProcessingMode.SEPARATE)
                .withFormName(Constants.FORMS.VMMC_NOTIFIABLE)
                .build();
        actionList.put(context.getString(R.string.vmmc_notifiable_adverse), action);

    }

    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.VMMC_FOLLOW_UP_VISIT;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.VMMC_FOLLOW_UP;
    }

    private class VmmcFollowUpActionHelper extends org.smartregister.chw.vmmc.actionhelper.VmmcFollowUpActionHelper {
        public VmmcFollowUpActionHelper(String baseEntityId) {
            super(baseEntityId);
        }

        @Override
        public String postProcess(String s) {
            if (notifiable_adverse_event_occured.equalsIgnoreCase("yes")) {
                try {
                    evaluateVmmcNAE(details);
                } catch (BaseVmmcVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            } else {
                actionList.remove(context.getString(R.string.vmmc_notifiable_adverse));
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }

    }
}
