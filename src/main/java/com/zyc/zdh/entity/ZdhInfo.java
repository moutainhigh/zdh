package com.zyc.zdh.entity;

class Dsi_Info{
    private String id;
    private String data_source_context;
    private String data_source_type;
    private String driver;
    private String url;
    private String user;
    private String password;
    private String dbtable;
    private String paths;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData_source_context() {
        return data_source_context;
    }

    public void setData_source_context(String data_source_context) {
        this.data_source_context = data_source_context;
    }

    public String getData_source_type() {
        return data_source_type;
    }

    public void setData_source_type(String data_source_type) {
        this.data_source_type = data_source_type;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbtable() {
        return dbtable;
    }

    public void setDbtable(String dbtable) {
        this.dbtable = dbtable;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }
}

public class ZdhInfo {

    private QuartzJobInfo quartzJobInfo;

    //输入数据源
    private Dsi_Info dsi_Input;


    //输出数据源
    private Dsi_Info dsi_Output;

    //etl 任务
    private EtlTaskInfo etlTaskInfo;

    public QuartzJobInfo getQuartzJobInfo() {
        return quartzJobInfo;
    }

    public void setQuartzJobInfo(QuartzJobInfo quartzJobInfo) {
        this.quartzJobInfo = quartzJobInfo;
    }

    public Dsi_Info getDsi_Input() {
        return dsi_Input;
    }

    public void setDsi_Input(Dsi_Info dsi_Input) {
        this.dsi_Input = dsi_Input;
    }

    public Dsi_Info getDsi_Output() {
        return dsi_Output;
    }

    public void setDsi_Output(Dsi_Info dsi_Output) {
        this.dsi_Output = dsi_Output;
    }

    public EtlTaskInfo getEtlTaskInfo() {
        return etlTaskInfo;
    }

    public void setEtlTaskInfo(EtlTaskInfo etlTaskInfo) {
        this.etlTaskInfo = etlTaskInfo;
    }

    public void setZdhInfo(DataSourcesInfo dataSourcesInfoInput , EtlTaskInfo etlTaskInfo, DataSourcesInfo dataSourcesInfoOutput,QuartzJobInfo quartzJobInfo){

       // this.dataSourcesInfoInput=dataSourcesInfoInput;

       // this.dataSourcesInfoOutput=dataSourcesInfoOutput;
        this.quartzJobInfo=quartzJobInfo;

        this.etlTaskInfo=etlTaskInfo;
        Dsi_Info dsi_Input=new Dsi_Info();
        dsi_Input.setId(dataSourcesInfoInput.getId());
        dsi_Input.setData_source_context(dataSourcesInfoInput.getData_source_context());
        dsi_Input.setData_source_type(dataSourcesInfoInput.getData_source_type());
        dsi_Input.setDbtable(etlTaskInfo.getData_sources_table_name_input());
        dsi_Input.setDriver(dataSourcesInfoInput.getDriver());
        dsi_Input.setUrl(dataSourcesInfoInput.getUrl());
        dsi_Input.setUser(dataSourcesInfoInput.getUsername());
        dsi_Input.setPassword(dataSourcesInfoInput.getPassword());
        dsi_Input.setPaths(etlTaskInfo.getData_sources_file_name_input());
        this.dsi_Input=dsi_Input;

        Dsi_Info dsi_Output=new Dsi_Info();
        dsi_Output.setId(dataSourcesInfoOutput.getId());
        dsi_Output.setData_source_context(dataSourcesInfoOutput.getData_source_context());
        dsi_Output.setData_source_type(dataSourcesInfoOutput.getData_source_type());
        dsi_Output.setDbtable(etlTaskInfo.getData_sources_table_name_output());
        dsi_Output.setDriver(dataSourcesInfoOutput.getDriver());
        dsi_Output.setUrl(dataSourcesInfoOutput.getUrl());
        dsi_Output.setUser(dataSourcesInfoOutput.getUsername());
        dsi_Output.setPassword(dataSourcesInfoOutput.getPassword());
        dsi_Output.setPaths(etlTaskInfo.getData_sources_file_name_output());

        this.dsi_Output=dsi_Output;

    }




}
