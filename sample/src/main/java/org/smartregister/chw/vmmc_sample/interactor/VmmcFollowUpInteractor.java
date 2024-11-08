package org.smartregister.chw.vmmc_sample.interactor;

import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.interactor.BaseVmmcFollowUpInteractor;
import org.smartregister.chw.vmmc_sample.activity.EntryActivity;


public class VmmcFollowUpInteractor extends BaseVmmcFollowUpInteractor {

    public VmmcFollowUpInteractor(String visitType) {
        super(visitType);
    }

    @Override
    public MemberObject getMemberClient(String memberID, String profileType) {
        return EntryActivity.getSampleMember();
    }
}
