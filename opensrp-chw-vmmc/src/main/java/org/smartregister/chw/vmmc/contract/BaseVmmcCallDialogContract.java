package org.smartregister.chw.vmmc.contract;

import android.content.Context;

public interface BaseVmmcCallDialogContract {

    interface View {
        void setPendingCallRequest(Dialer dialer);
        Context getCurrentContext();
    }

    interface Dialer {
        void callMe();
    }
}
