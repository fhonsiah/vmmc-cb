package org.smartregister.chw.vmmc.dao;

import org.smartregister.chw.vmmc.domain.MemberObject;
import org.smartregister.dao.AbstractDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VmmcDao extends AbstractDao {
    private static final SimpleDateFormat df = new SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
    );

    private static final DataMap<MemberObject> memberObjectMap = cursor -> {

        MemberObject memberObject = new MemberObject();

        memberObject.setFirstName(getCursorValue(cursor, "first_name", ""));
        memberObject.setMiddleName(getCursorValue(cursor, "middle_name", ""));
        memberObject.setLastName(getCursorValue(cursor, "last_name", ""));
        memberObject.setAddress(getCursorValue(cursor, "village_town"));
        memberObject.setGender(getCursorValue(cursor, "gender"));
        memberObject.setUniqueId(getCursorValue(cursor, "unique_id", ""));
        memberObject.setAge(getCursorValue(cursor, "dob"));
        memberObject.setFamilyBaseEntityId(getCursorValue(cursor, "relational_id", ""));
        memberObject.setRelationalId(getCursorValue(cursor, "relational_id", ""));
        memberObject.setPrimaryCareGiver(getCursorValue(cursor, "primary_caregiver"));
        memberObject.setFamilyName(getCursorValue(cursor, "family_name", ""));
        memberObject.setPhoneNumber(getCursorValue(cursor, "phone_number", ""));
        memberObject.setVmmcTestDate(getCursorValueAsDate(cursor, "vmmc_test_date", df));
        memberObject.setBaseEntityId(getCursorValue(cursor, "base_entity_id", ""));
        memberObject.setFamilyHead(getCursorValue(cursor, "family_head", ""));
        memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "pcg_phone_number", ""));
        memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "family_head_phone_number", ""));
        memberObject.setEnrollmentDate(getCursorValue(cursor,
                "enrollment_date",
                ""));

        String familyHeadName = getCursorValue(cursor, "family_head_first_name", "") + " "
                + getCursorValue(cursor, "family_head_middle_name", "");

        familyHeadName =
                (familyHeadName.trim() + " " + getCursorValue(cursor, "family_head_last_name", "")).trim();
        memberObject.setFamilyHeadName(familyHeadName);

        String familyPcgName = getCursorValue(cursor, "pcg_first_name", "") + " "
                + getCursorValue(cursor, "pcg_middle_name", "");

        familyPcgName =
                (familyPcgName.trim() + " " + getCursorValue(cursor, "pcg_last_name", "")).trim();
        memberObject.setPrimaryCareGiverName(familyPcgName);

        return memberObject;
    };

    public static Date getVmmcTestDate(String baseEntityID) {
        String sql = "select vmmc_test_date from ec_vmmc_enrollment where base_entity_id = '" + baseEntityID + "'";

        DataMap<Date> dataMap = cursor -> getCursorValueAsDate(cursor, "vmmc_test_date", getNativeFormsDateFormat());

        List<Date> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static String getGentialExamination(String baseEntityId) {
        String sql = "SELECT genital_examination FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "genital_examination");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getSystolic(String baseEntityId) {
        String sql = "SELECT systolic FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "systolic");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getDiastolic(String baseEntityId) {
        String sql = "SELECT diastolic FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "diastolic");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getAnyComplaints(String baseEntityId) {
        String sql = "SELECT any_complaints FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "any_complaints");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getDiagnosed(String baseEntityId) {
        String sql = "SELECT is_client_diagnosed_with_any FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "is_client_diagnosed_with_any");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getAnyComplicationsPreviousSurgicalProcedure(String baseEntityId) {
        String sql = "SELECT type_complication FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "type_complication");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getSymptomsHematologicalDiseaseValue(String baseEntityId) {
        String sql = "SELECT any_hematological_disease_symptoms FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "any_hematological_disease_symptoms");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getKnownAllergiesValue(String baseEntityId) {
        String sql = "SELECT known_allergies FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "known_allergies");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getHivTestResult(String baseEntityId) {
        String sql = "SELECT hiv_result FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "hiv_result");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getViralLoad(String baseEntityId) {
        String sql = "SELECT hiv_viral_load_text FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "hiv_viral_load_text");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getHivStatusSixMonthAgo(String baseEntityId) {
        String sql = "SELECT diagnosed_with_hiv_six_month_ago FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "diagnosed_with_hiv_six_month_ago");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getTypeForBloodGlucoseTest(String baseEntityId) {
        String sql = "SELECT type_of_blood_for_glucose_test FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "type_of_blood_for_glucose_test");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getBloodGlucoseTest(String baseEntityId) {
        String sql = "SELECT blood_for_glucose FROM ec_vmmc_services p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "blood_for_glucose");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getDischargeCondition(String baseEntityId) {
        String sql = "SELECT discharge_condition FROM ec_vmmc_post_op_and_discharge p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "discharge_condition");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getMcConducted(String baseEntityId) {
        String sql = "SELECT is_male_procedure_circumcision_conducted FROM ec_vmmc_procedure p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "is_male_procedure_circumcision_conducted");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static Date getVmmcFollowUpVisitDate(String baseEntityID) {
        String sql = "SELECT eventDate FROM event where eventType ='Vmmc Follow-up Visit' AND baseEntityId ='" + baseEntityID + "'";

        DataMap<Date> dataMap = cursor -> getCursorValueAsDate(cursor, "eventDate", getNativeFormsDateFormat());

        List<Date> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static int getVisitNumber(String baseEntityID) {
        String sql = "SELECT visit_number  FROM ec_vmmc_follow_up_visit WHERE entity_id='" + baseEntityID + "' ORDER BY visit_number DESC LIMIT 1";
        DataMap<Integer> map = cursor -> getCursorIntValue(cursor, "visit_number");
        List<Integer> res = readData(sql, map);

        if (res != null && res.size() > 0 && res.get(0) != null) {
            return res.get(0) + 1;
        } else
            return 0;
    }

    public static String getMcMethodUsed(String baseEntityId) {
        String sql = "SELECT male_circumcision_method FROM ec_vmmc_procedure p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "male_circumcision_method");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getMcDoneDate(String baseEntityId) {
        String sql = "SELECT mc_procedure_date FROM ec_vmmc_procedure p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "mc_procedure_date");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static String getDischargingDate(String baseEntityId) {
        String sql = "SELECT discharge_date FROM ec_vmmc_post_op_and_discharge p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "discharge_date");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static int getFollowUpVisitNumber(String baseEntityId) {
        String sql = "SELECT visit_number FROM ec_vmmc_follow_up_visit p " +
                " WHERE p.entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "visit_number");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() != 0 && res.get(0) != null) {
            return Integer.parseInt(res.get(0));
        }
        return 0;
    }

    public static boolean isRegisteredForVmmc(String baseEntityID) {
//        String sql = "SELECT count(p.base_entity_id) count FROM ec_vmmc_enrollment p " +
//                "WHERE p.base_entity_id = '" + baseEntityID + "' AND p.is_closed = 0 AND p.vmmc  = 1 " +
//                "AND datetime('NOW') <= datetime(p.last_interacted_with/1000, 'unixepoch', 'localtime','+15 days')";

        String sql = "SELECT count(p.base_entity_id) count FROM ec_vmmc_enrollment p " +
                "WHERE p.base_entity_id = '" + baseEntityID + "' AND p.is_closed = 0 ";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    public static Integer getVmmcFamilyMembersCount(String familyBaseEntityId) {
        String sql = "SELECT count(emc.base_entity_id) count FROM ec_vmmc_enrollment emc " +
                "INNER Join ec_family_member fm on fm.base_entity_id = emc.base_entity_id " +
                "WHERE fm.relational_id = '" + familyBaseEntityId + "' AND fm.is_closed = 0 " +
                "AND emc.is_closed = 0 AND emc.vmmc = 1";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return 0;
        return res.get(0);
    }

    public static MemberObject getMember(String baseEntityID) {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join ec_vmmc_enrollment mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 AND m.base_entity_id ='" + baseEntityID + "' ";
        List<MemberObject> res = readData(sql, memberObjectMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static List<MemberObject> getMembers() {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join ec_vmmc_enrollment mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 ";

        return readData(sql, memberObjectMap);
    }

}

