package org.smartregister.chw.vmmc_sample.interactor;

import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.interactor.BaseVmmcVisitProcedureInteractor;
import org.smartregister.chw.vmmc_sample.activity.EntryActivity;


public class VmmcVisitProcedureInteractor extends BaseVmmcVisitProcedureInteractor {

    public VmmcVisitProcedureInteractor(String visitType) {
        super(visitType);
    }

    @Override
    public MemberObject getMemberClient(String memberID, String profileType) {
        return EntryActivity.getSampleMember();
    }
}

