package org.smartregister.chw.vmmc_sample.interactor;

import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.interactor.BaseVmmcVisitDischargeInteractor;
import org.smartregister.chw.vmmc.interactor.BaseVmmcVisitInteractor;
import org.smartregister.chw.vmmc_sample.activity.EntryActivity;


public class VmmcVisitDischargeInteractor extends BaseVmmcVisitDischargeInteractor {

    public VmmcVisitDischargeInteractor(String visitType) {
        super(visitType);
    }

    @Override
    public MemberObject getMemberClient(String memberID, String profileType) {
        return EntryActivity.getSampleMember();
    }}
