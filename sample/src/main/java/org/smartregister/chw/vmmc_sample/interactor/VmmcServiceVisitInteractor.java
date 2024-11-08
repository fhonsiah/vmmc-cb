package org.smartregister.chw.vmmc_sample.interactor;

import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.interactor.BaseVmmcServiceVisitInteractor;
import org.smartregister.chw.vmmc.interactor.BaseVmmcVisitInteractor;
import org.smartregister.chw.vmmc_sample.activity.EntryActivity;


public class VmmcServiceVisitInteractor extends BaseVmmcServiceVisitInteractor {
    public VmmcServiceVisitInteractor(String visitType) {
        super(visitType);
    }

    @Override
    public MemberObject getMemberClient(String memberID, String profileType) {
        return EntryActivity.getSampleMember();
    }
}
