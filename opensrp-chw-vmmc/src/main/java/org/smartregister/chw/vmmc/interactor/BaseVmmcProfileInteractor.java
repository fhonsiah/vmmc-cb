package org.smartregister.chw.vmmc.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.vmmc.contract.VmmcProfileContract;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.util.AppExecutors;
import org.smartregister.chw.vmmc.util.VmmcUtil;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

public class BaseVmmcProfileInteractor implements VmmcProfileContract.Interactor {
    protected AppExecutors appExecutors;

    @VisibleForTesting
    BaseVmmcProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseVmmcProfileInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void refreshProfileInfo(MemberObject memberObject, VmmcProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshFamilyStatus(AlertStatus.normal);
            callback.refreshMedicalHistory(true);
            callback.refreshUpComingServicesStatus("Vmmc Visit", AlertStatus.normal, new Date());
        });
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final String jsonString, final VmmcProfileContract.InteractorCallBack callback) {

        Runnable runnable = () -> {
            try {
                VmmcUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        appExecutors.diskIO().execute(runnable);
    }
}
