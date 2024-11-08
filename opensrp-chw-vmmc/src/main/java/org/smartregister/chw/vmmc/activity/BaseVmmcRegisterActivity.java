package org.smartregister.chw.vmmc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.MenuRes;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.chw.vmmc.contract.VmmcRegisterContract;
import org.smartregister.chw.vmmc.fragment.BaseVmmcRegisterFragment;
import org.smartregister.chw.vmmc.interactor.BaseVmmcRegisterInteractor;
import org.smartregister.chw.vmmc.listener.VmmcBottomNavigationListener;
import org.smartregister.chw.vmmc.model.BaseVmmcRegisterModel;
import org.smartregister.chw.vmmc.presenter.BaseVmmcRegisterPresenter;
import org.smartregister.chw.vmmc.util.Constants;
import org.smartregister.chw.vmmc.util.DBConstants;
import org.smartregister.chw.vmmc.util.VmmcJsonFormUtils;
import org.smartregister.chw.vmmc.util.VmmcUtil;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.repository.BaseRepository;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;
import org.smartregister.chw.vmmc.R;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class BaseVmmcRegisterActivity extends BaseRegisterActivity implements VmmcRegisterContract.View {

    protected String BASE_ENTITY_ID;
    protected String FAMILY_BASE_ENTITY_ID;
    protected String FORM_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BASE_ENTITY_ID = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID);
        FAMILY_BASE_ENTITY_ID = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.FAMILY_BASE_ENTITY_ID);
        FORM_NAME = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.VMMC_FORM_NAME);
        onStartActivityWithAction();
    }

    /**
     * Process a payload when an activity is started with an action
     */
    protected void onStartActivityWithAction() {
        if (FORM_NAME != null) {
            startFormActivity(FORM_NAME, BASE_ENTITY_ID, null);
        }
    }

    @Override
    public void startRegistration() {
        startFormActivity(FORM_NAME, null, null);
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            if (mBaseFragment instanceof BaseVmmcRegisterFragment) {
                String locationId = Context.getInstance().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                presenter().startForm(formName, entityId, metaData, locationId);
            }
        } catch (Exception e) {
            Timber.e(e);
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, BaseVmmcRegisterActivity.class);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        if (getFormConfig() != null) {
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, getFormConfig());
        }
        startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    public Form getFormConfig() {
        return null;
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            presenter().saveForm(data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON));
            finish();
        }
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(Constants.CONFIGURATION.VMMC_ENROLLMENT);
    }

    /**
     * Override this to subscribe to bottom navigation
     */
    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(R.id.action_register);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_search);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_library);
            bottomNavigationView.inflateMenu(getMenuResource());
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);
            BottomNavigationListener familyBottomNavigationListener = new VmmcBottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(familyBottomNavigationListener);
        }
    }

    @MenuRes
    public int getMenuResource() {
        return R.menu.bottom_nav_vmmc;
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseVmmcRegisterPresenter(this, new BaseVmmcRegisterModel(), new BaseVmmcRegisterInteractor());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new BaseVmmcRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public VmmcRegisterContract.Presenter presenter() {
        return (VmmcRegisterContract.Presenter) presenter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_GET_JSON) {

            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                JSONArray fieldsOne = VmmcJsonFormUtils.fields(form, Constants.STEP_ONE);
                updateFormField(fieldsOne, DBConstants.KEY.RELATIONAL_ID, FAMILY_BASE_ENTITY_ID);
//                process vmmc form
                presenter().saveForm(form.toString());
            } catch (JSONException e) {
                Timber.e(e);
                displayToast(getString(R.string.error_unable_to_save_form));
            }
            startClientProcessing();
        }
    }

    private void updateFormField(JSONArray formFieldArrays, String formFieldKey, String updateValue) {
        if (updateValue != null) {
            JSONObject formObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(formFieldArrays, formFieldKey);
            if (formObject != null) {
                try {
                    formObject.remove(org.smartregister.util.JsonFormUtils.VALUE);
                    formObject.put(org.smartregister.util.JsonFormUtils.VALUE, updateValue);
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        }
    }

    public void startClientProcessing() {
        try {
            long lastSyncTimeStamp = Utils.getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            VmmcUtil.getClientProcessorForJava().processClient(VmmcUtil.getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            Utils.getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.d(e);
        }

    }
}
