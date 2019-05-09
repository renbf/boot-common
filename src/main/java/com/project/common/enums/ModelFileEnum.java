package com.project.common.enums;

/**
 * 文件模板枚举
 */
public enum ModelFileEnum {
    ZSHT("制式合同","/model/word/systemContract.docx"),
    SHRMB("收货人模板","/model/excel/ConsigneeModel.xlsx"),
    SCDWW("生产单位模板","/model/excel/manufactureFactoryModel.xlsx"),
    FHR("发货人模板","/model/excel/consignorModel.xlsx"),
    CP("产品模板","/model/excel/productModel.xlsx"),
    SBYS("申报要素模板","/model/excel/declareElementModel.xlsx"),
    CKHT("出口合同模板","/model/excel/contractExpModel.xlsx"),
    JKHT("进口合同模板","/model/excel/contractImpModel.xlsx"),
    HTCP("合同产品模板","/model/excel/contractProductModel.xlsx");


    private String modelName;//模板名称
    private String modelPath;//模板文件路径


    ModelFileEnum(String modelName, String modelPath) {
        this.modelName = modelName;
        this.modelPath = modelPath;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }
}
