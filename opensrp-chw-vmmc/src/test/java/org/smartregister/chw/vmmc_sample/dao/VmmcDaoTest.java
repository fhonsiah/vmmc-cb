package org.smartregister.chw.vmmc_sample.dao;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.chw.vmmc.dao.VmmcDao;
import org.smartregister.repository.Repository;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class VmmcDaoTest extends VmmcDao {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setRepository(repository);
    }

    @Test
    public void testIsRegisteredForVmmc() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        boolean registered = VmmcDao.isRegisteredForVmmc("12345");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT count(p.base_entity_id) count FROM ec_vmmc_enrollment p WHERE p.base_entity_id = '12345' AND p.is_closed = 0"), Mockito.any());
        Assert.assertFalse(registered);
    }

    @Test
    public void testGetVmmcTestDate() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        Date testDate = VmmcDao.getVmmcTestDate("34233");
        Mockito.verify(database).rawQuery(Mockito.contains("select vmmc_test_date from ec_vmmc_enrollment where base_entity_id = '34233'"), Mockito.any());
    }

    @Test
    public void testGetGentialExamination() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getGentialExamination("45678");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT genital_examination FROM ec_vmmc_services p  WHERE p.entity_id = '45678' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }


    @Test
    public void testGetSystolic() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getSystolic("78901");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT systolic FROM ec_vmmc_services p  WHERE p.entity_id = '78901' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetDiastolic() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getDiastolic("23456");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT diastolic FROM ec_vmmc_services p  WHERE p.entity_id = '23456' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetAnyComplaints() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getAnyComplaints("56789");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT any_complaints FROM ec_vmmc_services p  WHERE p.entity_id = '56789' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetDiagnosed() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getDiagnosed("67890");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT is_client_diagnosed_with_any FROM ec_vmmc_services p  WHERE p.entity_id = '67890' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetAnyComplicationsPreviousSurgicalProcedure() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getAnyComplicationsPreviousSurgicalProcedure("78901");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT type_complication FROM ec_vmmc_services p  WHERE p.entity_id = '78901' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetSymptomsHematologicalDiseaseValue() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getSymptomsHematologicalDiseaseValue("89012");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT any_hematological_disease_symptoms FROM ec_vmmc_services p  WHERE p.entity_id = '89012' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetKnownAllergiesValue() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getKnownAllergiesValue("90123");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT known_allergies FROM ec_vmmc_services p  WHERE p.entity_id = '90123' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetHivTestResult() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getHivTestResult("01234");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT hiv_result FROM ec_vmmc_services p  WHERE p.entity_id = '01234' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetViralLoad() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getViralLoad("12345");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT hiv_viral_load_text FROM ec_vmmc_services p  WHERE p.entity_id = '12345' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetTypeForBloodGlucoseTest() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getTypeForBloodGlucoseTest("34567");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT type_of_blood_for_glucose_test FROM ec_vmmc_services p  WHERE p.entity_id = '34567' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetBloodGlucoseTest() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getBloodGlucoseTest("45678");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT blood_for_glucose FROM ec_vmmc_services p  WHERE p.entity_id = '45678' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetDischargeCondition() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getDischargeCondition("56789");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT discharge_condition FROM ec_vmmc_post_op_and_discharge p  WHERE p.entity_id = '56789' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetMcConducted() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getMcConducted("67890");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT is_male_procedure_circumcision_conducted FROM ec_vmmc_procedure p  WHERE p.entity_id = '67890' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }


    @Test
    public void testGetVisitNumber() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        int result = VmmcDao.getVisitNumber("89012");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT visit_number  FROM ec_vmmc_follow_up_visit WHERE entity_id='89012' ORDER BY visit_number DESC LIMIT 1"), Mockito.any());
        Assert.assertEquals(0, result);
    }

}

