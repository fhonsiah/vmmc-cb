package org.smartregister.chw.vmmc.custom_views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.fragment.BaseVmmcCallDialogFragment;
import org.smartregister.chw.vmmc.R;

public class BaseVmmcFloatingMenu extends LinearLayout implements View.OnClickListener {
    private MemberObject MEMBER_OBJECT;

    public BaseVmmcFloatingMenu(Context context, MemberObject MEMBER_OBJECT) {
        super(context);
        initUi();
        this.MEMBER_OBJECT = MEMBER_OBJECT;
    }

    protected void initUi() {
        inflate(getContext(), R.layout.view_vmmc_floating_menu, this);
        FloatingActionButton fab = findViewById(R.id.vmmc_fab);
        if (fab != null)
            fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.vmmc_fab) {
            Activity activity = (Activity) getContext();
            BaseVmmcCallDialogFragment.launchDialog(activity, MEMBER_OBJECT);
        }  else if (view.getId() == R.id.refer_to_facility_layout) {
            Activity activity = (Activity) getContext();
            BaseVmmcCallDialogFragment.launchDialog(activity, MEMBER_OBJECT);
        }
    }
}