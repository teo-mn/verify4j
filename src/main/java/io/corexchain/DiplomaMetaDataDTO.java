package io.corexchain;

import java.util.HashMap;
import java.util.Map;

public class DiplomaMetaDataDTO {
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
