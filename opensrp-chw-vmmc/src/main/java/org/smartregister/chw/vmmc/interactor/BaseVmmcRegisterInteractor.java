package org.smartregister.chw.vmmc.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.vmmc.contract.VmmcRegisterContract;
import org.smartregister.chw.vmmc.util.AppExecutors;
import org.smartregister.chw.vmmc.util.VmmcUtil;

public class BaseVmmcRegisterInteractor implements VmmcRegisterContract.Interactor {

    private AppExecutors appExecutors;

    @VisibleForTesting
    BaseVmmcRegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseVmmcRegisterInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void saveRegistration(final String jsonString, final VmmcRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            try {
                VmmcUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.onRegistrationSaved());
        };
        appExecutors.diskIO().execute(runnable);
    }
}
