package org.smartregister.chw.vmmc.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.smartregister.chw.vmmc.R;
import org.smartregister.chw.vmmc.VmmcLibrary;
import org.smartregister.chw.vmmc.contract.VmmcProfileContract;
import org.smartregister.chw.vmmc.custom_views.BaseVmmcFloatingMenu;
import org.smartregister.chw.vmmc.dao.VmmcDao;
import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.chw.vmmc.domain.Visit;
import org.smartregister.chw.vmmc.interactor.BaseVmmcProfileInteractor;
import org.smartregister.chw.vmmc.presenter.BaseVmmcProfilePresenter;
import org.smartregister.chw.vmmc.util.Constants;
import org.smartregister.chw.vmmc.util.VmmcUtil;
import org.smartregister.chw.vmmc.util.VmmcVisitsUtil;
import org.smartregister.domain.AlertStatus;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.activity.BaseProfileActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;


public abstract class BaseVmmcProfileActivity
        extends BaseProfileActivity
        implements VmmcProfileContract.View,
        VmmcProfileContract.InteractorCallBack {

    protected MemberObject memberObject;
    protected VmmcProfileContract.Presenter profilePresenter;
    protected CircleImageView imageView;
    protected TextView textViewName;
    protected TextView textViewGender;
    protected TextView textViewLocation;
    protected TextView textViewUniqueID;
    protected TextView textViewRecordVmmc;
    protected TextView textViewProcedureVmmc;
    protected TextView textViewNotifiableVmmc;
    protected TextView textViewDischargeVmmc;
    protected TextView textViewFollowUpVmmc;
    protected TextView textViewRecordAnc;
    protected TextView textViewContinueVmmc;
    protected TextView textViewContinueVmmcService;
    protected TextView textViewContinueVmmcProcedure;
    protected TextView manualProcessVisit;
    protected TextView textview_positive_date;
    protected View view_last_visit_row;
    protected View view_most_due_overdue_row;
    protected View view_family_row;
    protected View view_positive_date_row;
    protected RelativeLayout rlLastVisit;
    protected RelativeLayout rlUpcomingServices;
    protected RelativeLayout rlFamilyServicesDue;
    protected RelativeLayout visitStatus;
    protected RelativeLayout visitInProgress;
    protected RelativeLayout vmmcServiceInProgress;
    protected RelativeLayout vmmcProcedureInProgress;
    protected ImageView imageViewCross;
    protected TextView textViewUndo;
    protected RelativeLayout rlVmmcPositiveDate;
    protected TextView textViewVisitDone;
    protected RelativeLayout visitDone;
    protected LinearLayout recordVisits;
    protected TextView textViewVisitDoneEdit;
    protected TextView textViewRecordAncNotDone;
    protected String profileType;
    protected BaseVmmcFloatingMenu baseVmmcFloatingMenu;
    private TextView tvUpComingServices;
    private TextView tvFamilyStatus;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
    private ProgressBar progressBar;

    public static void startProfileActivity(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, BaseVmmcProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_vmmc_profile);
        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        String baseEntityId = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID);
        profileType = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.PROFILE_TYPE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }

        toolbar.setNavigationOnClickListener(v -> BaseVmmcProfileActivity.this.finish());
        appBarLayout = this.findViewById(R.id.collapsing_toolbar_appbarlayout);
        if (Build.VERSION.SDK_INT >= 21) {
            appBarLayout.setOutlineProvider(null);
        }

        textViewName = findViewById(R.id.textview_name);
        textViewGender = findViewById(R.id.textview_gender);
        textViewLocation = findViewById(R.id.textview_address);
        textViewUniqueID = findViewById(R.id.textview_id);
        view_last_visit_row = findViewById(R.id.view_last_visit_row);
        view_most_due_overdue_row = findViewById(R.id.view_most_due_overdue_row);
        view_family_row = findViewById(R.id.view_family_row);
        view_positive_date_row = findViewById(R.id.view_positive_date_row);
        imageViewCross = findViewById(R.id.tick_image);
        tvUpComingServices = findViewById(R.id.textview_name_due);
        tvFamilyStatus = findViewById(R.id.textview_family_has);
        textview_positive_date = findViewById(R.id.textview_positive_date);
        rlLastVisit = findViewById(R.id.rlLastVisit);
        rlUpcomingServices = findViewById(R.id.rlUpcomingServices);
        rlFamilyServicesDue = findViewById(R.id.rlFamilyServicesDue);
        rlVmmcPositiveDate = findViewById(R.id.rlVmmcPositiveDate);
        textViewVisitDone = findViewById(R.id.textview_visit_done);
        visitStatus = findViewById(R.id.record_visit_not_done_bar);
        visitDone = findViewById(R.id.visit_done_bar);
        visitInProgress = findViewById(R.id.record_visit_in_progress);
        vmmcServiceInProgress = findViewById(R.id.record_vmmc_service_visit_in_progress);
        vmmcProcedureInProgress = findViewById(R.id.record_vmmc_procedure_visit_in_progress);
        recordVisits = findViewById(R.id.record_visits);
        progressBar = findViewById(R.id.progress_bar);
        textViewRecordAncNotDone = findViewById(R.id.textview_record_anc_not_done);
        textViewVisitDoneEdit = findViewById(R.id.textview_edit);
        textViewRecordVmmc = findViewById(R.id.textview_record_vmmc);
        textViewProcedureVmmc = findViewById(R.id.textview_procedure_vmmc);
        textViewNotifiableVmmc = findViewById(R.id.textview_notifiable_vmmc);
        textViewDischargeVmmc = findViewById(R.id.textview_discharge_vmmc);
        textViewFollowUpVmmc = findViewById(R.id.textview_followup_vmmc);
        textViewContinueVmmc = findViewById(R.id.textview_continue);
        textViewContinueVmmcService = findViewById(R.id.continue_vmmc_service);
        textViewContinueVmmcProcedure = findViewById(R.id.continue_vmmc_procedure);
        manualProcessVisit = findViewById(R.id.textview_manual_process);
        textViewRecordAnc = findViewById(R.id.textview_record_anc);
        textViewUndo = findViewById(R.id.textview_undo);
        imageView = findViewById(R.id.imageview_profile);

        textViewRecordAncNotDone.setOnClickListener(this);
        textViewVisitDoneEdit.setOnClickListener(this);
        rlLastVisit.setOnClickListener(this);
        rlUpcomingServices.setOnClickListener(this);
        rlFamilyServicesDue.setOnClickListener(this);
        rlVmmcPositiveDate.setOnClickListener(this);
        textViewRecordVmmc.setOnClickListener(this);
        textViewProcedureVmmc.setOnClickListener(this);
        textViewNotifiableVmmc.setOnClickListener(this);
        textViewDischargeVmmc.setOnClickListener(this);
        textViewFollowUpVmmc.setOnClickListener(this);
        textViewContinueVmmc.setOnClickListener(this);
        textViewContinueVmmcService.setOnClickListener(this);
        textViewContinueVmmcProcedure.setOnClickListener(this);
        manualProcessVisit.setOnClickListener(this);
        textViewRecordAnc.setOnClickListener(this);
        textViewUndo.setOnClickListener(this);

        imageRenderHelper = new ImageRenderHelper(this);
        memberObject = getMemberObject(baseEntityId);
        initializePresenter();
        profilePresenter.fillProfileData(memberObject);
        setupViews();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setupViews();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        setupViews();
    }

    @Override
    protected void setupViews() {
        initializeFloatingMenu();
        setupButtons();
    }

    protected void setupButtons() {
        try {

            if (getServiceVisit() != null) {
                if (!getServiceVisit().getProcessed() && VmmcVisitsUtil.getVmmcServiceVisitStatus(getServiceVisit()).equalsIgnoreCase(VmmcVisitsUtil.Complete)) {
                    manualProcessVisit.setVisibility(View.VISIBLE);
                    textViewContinueVmmcService.setText(R.string.edit_visit);
                    manualProcessVisit.setOnClickListener(view -> {
                        try {
                            VmmcVisitsUtil.manualProcessVisit(getServiceVisit());
                            displayToast(R.string.vmmc_visit_conducted);
                            setupViews();
                        } catch (Exception e) {
                            Timber.d(e);
                        }
                    });
                } else {
                    manualProcessVisit.setVisibility(View.GONE);
                }
                if (isVisitOnProgress(getServiceVisit())) {
                    textViewRecordVmmc.setVisibility(View.GONE);
                    vmmcServiceInProgress.setVisibility(View.VISIBLE);
                } else {
                    textViewRecordVmmc.setVisibility(View.VISIBLE);
                    vmmcServiceInProgress.setVisibility(View.GONE);
                }

                processVmmcService();

                if (isVisitOnProgress(getServiceVisit())) {
                    findViewById(R.id.family_vmmc_head).setVisibility(View.GONE);
                }

            }

            if (getVmmcProcedureVisit() != null) {
                if (!getVmmcProcedureVisit().getProcessed() && VmmcVisitsUtil.getVmmcProcedureVisitStatus(getVmmcProcedureVisit()).equalsIgnoreCase(VmmcVisitsUtil.Complete)) {
                    manualProcessVisit.setVisibility(View.VISIBLE);
                    textViewContinueVmmcProcedure.setText(R.string.edit_visit);
                    manualProcessVisit.setOnClickListener(view -> {
                        try {
                            VmmcVisitsUtil.manualProcessVisit(getVmmcProcedureVisit());
                            displayToast(R.string.vmmc_visit_conducted);
                            setupViews();
                        } catch (Exception e) {
                            Timber.d(e);
                        }
                    });
                } else {
                    manualProcessVisit.setVisibility(View.GONE);
                }

                if (isVisitOnProgress(getVmmcProcedureVisit())) {
                    textViewProcedureVmmc.setVisibility(View.GONE);
                    vmmcProcedureInProgress.setVisibility(View.VISIBLE);
                } else {
                    textViewProcedureVmmc.setVisibility(View.VISIBLE);
                    vmmcProcedureInProgress.setVisibility(View.GONE);
                }

                processVmmcProcedure();
            }

            if (getVmmcDischargeVisit() != null) {
                if ((!getVmmcDischargeVisit().getProcessed() && VmmcVisitsUtil.getVmmcVisitStatus(getVmmcDischargeVisit()).equalsIgnoreCase(VmmcVisitsUtil.Complete))) {
                    manualProcessVisit.setVisibility(View.VISIBLE);
                    textViewContinueVmmc.setText(R.string.edit_visit);
                    manualProcessVisit.setOnClickListener(view -> {
                        try {
                            VmmcVisitsUtil.manualProcessVisit(getVmmcDischargeVisit());
                            if (getNotifiableVisit() != null) {
                                VmmcVisitsUtil.manualProcessVisit(getNotifiableVisit());
                            }
                            displayToast(R.string.vmmc_visit_conducted);
                            setupViews();
                        } catch (Exception e) {
                            Timber.d(e);
                        }
                    });
                } else {
                    manualProcessVisit.setVisibility(View.GONE);
                }

                if (isVisitOnProgress(getVmmcDischargeVisit())) {
                    textViewDischargeVmmc.setVisibility(View.GONE);
                    visitInProgress.setVisibility(View.VISIBLE);
                } else {
                    textViewDischargeVmmc.setVisibility(View.VISIBLE);
                    visitInProgress.setVisibility(View.GONE);
                }

                processVmmcDischarge();
            }

            if (getFollowupVisit() != null) {
                VmmcVisitsUtil.manualProcessVisit(getFollowupVisit());
                if (getNotifiableVisit() != null) {
                    VmmcVisitsUtil.manualProcessVisit(getNotifiableVisit());
                }
                displayToast(R.string.vmmc_visit_conducted);
                // processVmmcDischarge();
            }

        } catch (Exception e) {
            Timber.d(e);
        }
    }

    protected Visit getServiceVisit() {
        return VmmcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.VMMC_SERVICES);
    }

    protected Visit getVmmcProcedureVisit() {
        return VmmcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.VMMC_PROCEDURE);
    }

    protected Visit getVmmcDischargeVisit() {
        return VmmcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.VMMC_DISCHARGE);
    }

    protected Visit getFollowupVisit() {
        return VmmcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.VMMC_FOLLOW_UP_VISIT);
    }


    protected Visit getNotifiableVisit() {
        return VmmcLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EVENT_TYPE.VMMC_NOTIFIABLE_EVENTS);
    }

    protected void processVmmcDischarge() {
        String dischargeCondition = VmmcDao.getDischargeCondition(memberObject.getBaseEntityId());
        if (dischargeCondition.equalsIgnoreCase(Constants.VALUES.SATISFACTORY) || dischargeCondition.equalsIgnoreCase(Constants.VALUES.NEEDS_FOLLOWUP)) {
            textViewRecordVmmc.setVisibility(View.GONE);
            textViewProcedureVmmc.setVisibility(View.GONE);
            textViewDischargeVmmc.setVisibility(View.GONE);
            textViewFollowUpVmmc.setVisibility(View.VISIBLE);
        }
    }

    protected void processVmmcProcedure() {
        String mcConducted = VmmcDao.getMcConducted(memberObject.getBaseEntityId());
        if (mcConducted.equalsIgnoreCase(Constants.VALUES.YES)) {
            textViewRecordVmmc.setVisibility(View.GONE);
            textViewProcedureVmmc.setVisibility(View.GONE);
            textViewDischargeVmmc.setVisibility(View.VISIBLE);
        }
    }

    protected void processVmmcService() {
        if (!checkContraIndications()) {
            textViewRecordVmmc.setVisibility(View.GONE);
            textViewProcedureVmmc.setVisibility(View.VISIBLE);
            textViewDischargeVmmc.setVisibility(View.GONE);
            textViewNotifiableVmmc.setVisibility(View.GONE);
            rlLastVisit.setVisibility(View.VISIBLE);
            findViewById(R.id.family_vmmc_head).setVisibility(View.GONE);
        } else {
            rlLastVisit.setVisibility(View.VISIBLE);
            findViewById(R.id.family_vmmc_head).setVisibility(View.VISIBLE);
        }
    }

    protected boolean checkContraIndications(){
        String gentialExaminationValue = VmmcDao.getGentialExamination(memberObject.getBaseEntityId());
        String diagnosedValue = VmmcDao.getDiagnosed(memberObject.getBaseEntityId());
        String hivTestResultValue = VmmcDao.getHivTestResult(memberObject.getBaseEntityId());
        String knownAllergiesValue = VmmcDao.getKnownAllergiesValue(memberObject.getBaseEntityId());
        String symptomsHematologicalDiseaseValue = VmmcDao.getSymptomsHematologicalDiseaseValue(memberObject.getBaseEntityId());
        String anyComplaintsValue = VmmcDao.getAnyComplaints(memberObject.getBaseEntityId());
        String viralLoad = VmmcDao.getViralLoad(memberObject.getBaseEntityId());
        String typeForBloodGlucoseTest = VmmcDao.getTypeForBloodGlucoseTest(memberObject.getBaseEntityId());
        String bloodGlucoseTest = VmmcDao.getBloodGlucoseTest(memberObject.getBaseEntityId());
        String systolic = VmmcDao.getSystolic(memberObject.getBaseEntityId());
        String diastolic = VmmcDao.getDiastolic(memberObject.getBaseEntityId());
        String isHivStatusSixMonthAgo = VmmcDao.getHivStatusSixMonthAgo(memberObject.getBaseEntityId());

        boolean isGentialExaminationValid = gentialExaminationValue.equalsIgnoreCase(Constants.VALUES.NONE) || gentialExaminationValue.contains(Constants.VALUES.CHORDAE);

        boolean isHivValid = (isHivStatusSixMonthAgo.contains(Constants.VALUES.YES) && Integer.parseInt(viralLoad) < 1000) || diagnosedValue.equalsIgnoreCase(Constants.VALUES.NONE) || !diagnosedValue.contains(Constants.VALUES.HIV);

        boolean isDiabetesValid = ((typeForBloodGlucoseTest.equalsIgnoreCase(Constants.VALUES.RBG) &&
                Double.parseDouble(bloodGlucoseTest) < 11.0 && Double.parseDouble(bloodGlucoseTest) > 4.0) ||
                (typeForBloodGlucoseTest.equalsIgnoreCase(Constants.VALUES.FBG) &&
                        (Double.parseDouble(bloodGlucoseTest) < 5.9 || Double.parseDouble(bloodGlucoseTest) > 3.9)) ||
                typeForBloodGlucoseTest.isEmpty());

        boolean isAllergiesValid = knownAllergiesValue.equalsIgnoreCase(Constants.VALUES.NONE) || knownAllergiesValue.equalsIgnoreCase(Constants.VALUES.SILICON_OR_LEXAN);

        boolean isHIVTestResultValid = hivTestResultValue.equalsIgnoreCase(Constants.VALUES.NEGATIVE) || hivTestResultValue.isEmpty();

        boolean isHematologicalDiseaseValid = symptomsHematologicalDiseaseValue.equalsIgnoreCase(Constants.VALUES.NONE);

        boolean isComplaintsValid = anyComplaintsValue.equalsIgnoreCase(Constants.VALUES.NONE);

        boolean isBloodPressureValid = (Integer.parseInt(systolic) <= 140 && Integer.parseInt(diastolic) <= 90) &&
                (Integer.parseInt(systolic) >= 90 && Integer.parseInt(diastolic) >= 60);

        return !isGentialExaminationValid || !isHivValid || !isDiabetesValid ||
                !isAllergiesValid || !isHIVTestResultValid ||
                !isHematologicalDiseaseValid || !isComplaintsValid ||
                !isBloodPressureValid;
    }

    protected MemberObject getMemberObject(String baseEntityId) {
        return VmmcDao.getMember(baseEntityId);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.title_layout) {
            onBackPressed();
        } else if (id == R.id.rlLastVisit) {
            this.openMedicalHistory();
        } else if (id == R.id.rlUpcomingServices) {
            this.openUpcomingService();
        } else if (id == R.id.rlFamilyServicesDue) {
            this.openFamilyDueServices();
        } else if (id == R.id.textview_record_vmmc) {
            this.openFollowupVisit();
        } else if (id == R.id.textview_procedure_vmmc) {
            startProcedure();
        } else if (id == R.id.textview_discharge_vmmc) {
            startDischarge();
        } else if (id == R.id.textview_notifiable_vmmc) {
            this.startNotifiableForm();
        } else if (id == R.id.textview_followup_vmmc) {
            this.startFollowUp();
        } else if (id == R.id.continue_vmmc_service) {
            this.continueService();
        }else if (id == R.id.continue_vmmc_procedure) {
            this.continueProcedure();
        }else if (id == R.id.textview_continue) {
            this.continueDischarge();
        }
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        profilePresenter = new BaseVmmcProfilePresenter(this, new BaseVmmcProfileInteractor(), memberObject);
        fetchProfileData();
        profilePresenter.refreshProfileBottom();
    }

    public void initializeFloatingMenu() {
        if (StringUtils.isNotBlank(memberObject.getPhoneNumber())) {
            baseVmmcFloatingMenu = new BaseVmmcFloatingMenu(this, memberObject);
            baseVmmcFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
            LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            addContentView(baseVmmcFloatingMenu, linearLayoutParams);
        }
    }

    @Override
    public void hideView() {
        textViewRecordVmmc.setVisibility(View.GONE);
        textViewProcedureVmmc.setVisibility(View.GONE);
    }

    @Override
    public void openFollowupVisit() {
        //Implement in application
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void setProfileViewWithData() {
        int age = new Period(new DateTime(memberObject.getAge()), new DateTime()).getYears();
        textViewName.setText(String.format("%s %s %s, %d", memberObject.getFirstName(),
                memberObject.getMiddleName(), memberObject.getLastName(), age));
        textViewGender.setText(VmmcUtil.getGenderTranslated(this, memberObject.getGender()));
        textViewLocation.setText(memberObject.getAddress());
        textViewUniqueID.setText(memberObject.getUniqueId());


        if (StringUtils.isNotBlank(memberObject.getPrimaryCareGiver()) && memberObject.getPrimaryCareGiver().equals(memberObject.getBaseEntityId())) {
            findViewById(R.id.primary_vmmc_caregiver).setVisibility(View.GONE);
        }
        if (memberObject.getVmmcTestDate() != null) {
            textview_positive_date.setText(getString(R.string.vmmc_positive) + " " + formatTime(memberObject.getVmmcTestDate()));
        }
    }

    @Override
    public void setOverDueColor() {
        textViewRecordVmmc.setBackground(getResources().getDrawable(R.drawable.record_btn_selector_overdue));
        textViewProcedureVmmc.setBackground(getResources().getDrawable(R.drawable.record_btn_selector_overdue));

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        //fetch profile data
    }

    @Override
    public void showProgressBar(boolean status) {
        progressBar.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void refreshMedicalHistory(boolean hasHistory) {
        showProgressBar(false);
        rlLastVisit.setVisibility(hasHistory ? View.VISIBLE : View.GONE);
    }

    @Override
    public void refreshUpComingServicesStatus(String service, AlertStatus status, Date date) {
        showProgressBar(false);
        if (status == AlertStatus.complete)
            return;
        view_most_due_overdue_row.setVisibility(View.GONE);
        rlUpcomingServices.setVisibility(View.GONE);

        if (status == AlertStatus.upcoming) {
            tvUpComingServices.setText(VmmcUtil.fromHtml(getString(R.string.vaccine_service_upcoming, service, dateFormat.format(date))));
        } else {
            tvUpComingServices.setText(VmmcUtil.fromHtml(getString(R.string.vaccine_service_due, service, dateFormat.format(date))));
        }
    }

    @Override
    public void refreshFamilyStatus(AlertStatus status) {
        showProgressBar(false);
        if (status == AlertStatus.complete) {
            setFamilyStatus(getString(R.string.family_has_nothing_due));
        } else if (status == AlertStatus.normal) {
            setFamilyStatus(getString(R.string.family_has_services_due));
        } else if (status == AlertStatus.urgent) {
            tvFamilyStatus.setText(VmmcUtil.fromHtml(getString(R.string.family_has_service_overdue)));
        }
    }

    private void setFamilyStatus(String familyStatus) {
        view_family_row.setVisibility(View.VISIBLE);
        rlFamilyServicesDue.setVisibility(View.GONE);
        tvFamilyStatus.setText(familyStatus);
    }

    @Override
    public void openMedicalHistory() {
        //implement
    }

    @Override
    public void openUpcomingService() {
        //implement
    }

    @Override
    public void openFamilyDueServices() {
        //implement
    }

    @Nullable
    private String formatTime(Date dateTime) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            return formatter.format(dateTime);
        } catch (Exception e) {
            Timber.d(e);
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            profilePresenter.saveForm(data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON));
            finish();
        }
    }

    protected boolean isVisitOnProgress(Visit visit) {

        return visit != null && !visit.getProcessed();
    }
}
