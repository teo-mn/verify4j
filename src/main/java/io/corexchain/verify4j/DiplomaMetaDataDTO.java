package io.corexchain.verify4j;

import java.util.HashMap;
import java.util.Map;

public class DiplomaMetaDataDTO {
    public String getDEGREE_NUMBER() {
        return DEGREE_NUMBER;
    }

    public void setDEGREE_NUMBER(String DEGREE_NUMBER) {
        this.DEGREE_NUMBER = DEGREE_NUMBER;
    }

    public String getPRIMARY_IDENTIFIER_NUMBER() {
        return PRIMARY_IDENTIFIER_NUMBER;
    }

    public void setPRIMARY_IDENTIFIER_NUMBER(String PRIMARY_IDENTIFIER_NUMBER) {
        this.PRIMARY_IDENTIFIER_NUMBER = PRIMARY_IDENTIFIER_NUMBER;
    }

    public Long getINSTITUTION_ID() {
        return INSTITUTION_ID;
    }

    public void setINSTITUTION_ID(Long INSTITUTION_ID) {
        this.INSTITUTION_ID = INSTITUTION_ID;
    }

    public String getINSTITUTION_NAME() {
        return INSTITUTION_NAME;
    }

    public void setINSTITUTION_NAME(String INSTITUTION_NAME) {
        this.INSTITUTION_NAME = INSTITUTION_NAME;
    }

    public String getEDUCATION_LEVEL_NAME() {
        return EDUCATION_LEVEL_NAME;
    }

    public void setEDUCATION_LEVEL_NAME(String EDUCATION_LEVEL_NAME) {
        this.EDUCATION_LEVEL_NAME = EDUCATION_LEVEL_NAME;
    }

    public String getEDUCATION_FIELD_CODE() {
        return EDUCATION_FIELD_CODE;
    }

    public void setEDUCATION_FIELD_CODE(String EDUCATION_FIELD_CODE) {
        this.EDUCATION_FIELD_CODE = EDUCATION_FIELD_CODE;
    }

    public String getEDUCATION_FIELD_NAME() {
        return EDUCATION_FIELD_NAME;
    }

    public void setEDUCATION_FIELD_NAME(String EDUCATION_FIELD_NAME) {
        this.EDUCATION_FIELD_NAME = EDUCATION_FIELD_NAME;
    }

    public Float getTOTAL_GPA() {
        return TOTAL_GPA;
    }

    public void setTOTAL_GPA(Float TOTAL_GPA) {
        this.TOTAL_GPA = TOTAL_GPA;
    }

    public String getLAST_NAME() {
        return LAST_NAME;
    }

    public void setLAST_NAME(String LAST_NAME) {
        this.LAST_NAME = LAST_NAME;
    }

    public String getFIRST_NAME() {
        return FIRST_NAME;
    }

    public void setFIRST_NAME(String FIRST_NAME) {
        this.FIRST_NAME = FIRST_NAME;
    }

    public String getCONFER_YEAR_NAME() {
        return CONFER_YEAR_NAME;
    }

    public void setCONFER_YEAR_NAME(String CONFER_YEAR_NAME) {
        this.CONFER_YEAR_NAME = CONFER_YEAR_NAME;
    }

    String DEGREE_NUMBER;
    String PRIMARY_IDENTIFIER_NUMBER;
    Long INSTITUTION_ID;
    String INSTITUTION_NAME;
    String EDUCATION_LEVEL_NAME;
    String EDUCATION_FIELD_CODE;
    String EDUCATION_FIELD_NAME;
    Float TOTAL_GPA;
    String LAST_NAME;
    String FIRST_NAME;
    String CONFER_YEAR_NAME;

    public Map<String , Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("CONFER_YEAR_NAME", this.CONFER_YEAR_NAME);
        map.put("DEGREE_NUMBER", this.DEGREE_NUMBER);
        map.put("EDUCATION_FIELD_CODE", this.EDUCATION_FIELD_CODE);
        map.put("EDUCATION_FIELD_NAME", this.EDUCATION_FIELD_NAME);
        map.put("EDUCATION_LEVEL_NAME", this.EDUCATION_LEVEL_NAME);
        map.put("FIRST_NAME", this.FIRST_NAME);
        map.put("INSTITUTION_ID", this.INSTITUTION_ID);
        map.put("INSTITUTION_NAME", this.INSTITUTION_NAME);
        map.put("LAST_NAME", this.LAST_NAME);
        map.put("PRIMARY_IDENTIFIER_NUMBER", this.PRIMARY_IDENTIFIER_NUMBER);
        map.put("TOTAL_GPA", this.TOTAL_GPA);
        return map;
    }
}
