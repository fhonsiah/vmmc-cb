package org.smartregister.chw.vmmc_sample.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.vmmc.contract.VmmcRegisterFragmentContract;
import org.smartregister.chw.vmmc.presenter.BaseVmmcRegisterFragmentPresenter;
import org.smartregister.chw.vmmc.util.Constants;
import org.smartregister.chw.vmmc.util.DBConstants;
import org.smartregister.configurableviews.model.View;

import java.util.Set;
import java.util.TreeSet;

public class BaseVmmcRegisterFragmentPresenterTest {
    @Mock
    protected VmmcRegisterFragmentContract.View view;

    @Mock
    protected VmmcRegisterFragmentContract.Model model;

    private BaseVmmcRegisterFragmentPresenter baseVmmcRegisterFragmentPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        baseVmmcRegisterFragmentPresenter = new BaseVmmcRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void assertNotNull() {
        Assert.assertNotNull(baseVmmcRegisterFragmentPresenter);
    }

    @Test
    public void getMainCondition() {
        Assert.assertEquals(" ec_vmmc_enrollment.is_closed = 0 ", baseVmmcRegisterFragmentPresenter.getMainCondition());
    }

    @Test
    public void getDueFilterCondition() {
        Assert.assertEquals(" (cast( julianday(STRFTIME('%Y-%m-%d', datetime('now'))) -  julianday(IFNULL(SUBSTR(vmmc_test_date,7,4)|| '-' || SUBSTR(vmmc_test_date,4,2) || '-' || SUBSTR(vmmc_test_date,1,2),'')) as integer) between 7 and 14) ", baseVmmcRegisterFragmentPresenter.getDueFilterCondition());
    }

    @Test
    public void getDefaultSortQuery() {
        Assert.assertEquals(Constants.TABLES.VMMC_ENROLLMENT + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ", baseVmmcRegisterFragmentPresenter.getDefaultSortQuery());
    }

    @Test
    public void getMainTable() {
        Assert.assertEquals(Constants.TABLES.VMMC_ENROLLMENT, baseVmmcRegisterFragmentPresenter.getMainTable());
    }

    @Test
    public void initializeQueries() {
        Set<View> visibleColumns = new TreeSet<>();
        baseVmmcRegisterFragmentPresenter.initializeQueries(null);
        Mockito.doNothing().when(view).initializeQueryParams(Constants.TABLES.VMMC_ENROLLMENT, null, null);
        Mockito.verify(view).initializeQueryParams(Constants.TABLES.VMMC_ENROLLMENT, null, null);
        Mockito.verify(view).initializeAdapter(visibleColumns);
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
    }

}