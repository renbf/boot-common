package com.project.common.enums;


/**
 * 附件来源枚举
 */
public enum FileSourceTypeEnum {
    YLR(1,"预录入"),
    DC(2,"订舱"),
    CY(3,"出运"),
    JG(4,"集港"),
    BG(5,"报关"),
    KC(6,"开船"),
    QS(7,"清算"),
    DZGL(8,"单证管理"),
    SBYS(9,"申报要素"),
    SCDW(10,"生产单位"),
    HYDL(11,"货运代理");
    private Integer sourceType;
    private String sourceName;

    FileSourceTypeEnum(Integer sourceType, String sourceName) {
        this.sourceType = sourceType;
        this.sourceName = sourceName;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
