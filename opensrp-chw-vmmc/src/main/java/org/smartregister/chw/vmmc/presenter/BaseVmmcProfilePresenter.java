package org.smartregister.chw.vmmc.presenter;

import android.content.Context;

import androidx.annotation.Nullable;

import org.smartregister.chw.vmmc.contract.VmmcProfileContract;
import org.smartregister.chw.vmmc.domain.MemberObject;

import java.lang.ref.WeakReference;

import timber.log.Timber;


public class BaseVmmcProfilePresenter implements VmmcProfileContract.Presenter {
    protected WeakReference<VmmcProfileContract.View> view;
    protected MemberObject memberObject;
    protected VmmcProfileContract.Interactor interactor;
    protected Context context;

    public BaseVmmcProfilePresenter(VmmcProfileContract.View view, VmmcProfileContract.Interactor interactor, MemberObject memberObject) {
        this.view = new WeakReference<>(view);
        this.memberObject = memberObject;
        this.interactor = interactor;
    }

    @Override
    public void fillProfileData(MemberObject memberObject) {
        if (memberObject != null && getView() != null) {
            getView().setProfileViewWithData();
        }
    }

    @Override
    public void recordVmmcButton(@Nullable String visitState) {
        if (getView() == null) {
            return;
        }

        if (("OVERDUE").equals(visitState) || ("DUE").equals(visitState)) {
            if (("OVERDUE").equals(visitState)) {
                getView().setOverDueColor();
            }
        } else {
            getView().hideView();
        }
    }

    @Override
    @Nullable
    public VmmcProfileContract.View getView() {
        if (view != null && view.get() != null)
            return view.get();

        return null;
    }

    @Override
    public void refreshProfileBottom() {
        interactor.refreshProfileInfo(memberObject, getView());
    }

    @Override
    public void saveForm(String jsonString) {
        try {
            interactor.saveRegistration(jsonString, getView());
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
