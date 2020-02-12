package com.zyc.zdh.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


@Table
public class QuartzJobInfo implements Serializable {

    @Id
    @Column
    private String job_id;//任务id,
    private String job_context;//任务说明
    private String job_type;// 任务类型,SHELL,FTP,CLASS
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date start_time;// 起始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date end_time;// 结束时间
    private String job_model;// 执行模式(顺时间执行1，执行一次2，重复执行3),
    private String plan_count;//计划执行次数
    private long count=0;//执行次数
    private String command;// command,
    private String params;// 参数,
    private String last_status;// 上次任务是否执行完必
    private Date last_time;// 上次任务执行时间,
    private Date next_time;// 下次任务执行时间,
    private String expr;// quartz 表达式
    private String status;// 任务状态,create,running,pause,finish,remove,error
    private String ip;//服务器地址,
    private String user;//用户名，
    private String password;//密码
    private String etl_task_id;
    private String etl_context;
    private String  owner;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getEtl_context() {
        return etl_context;
    }

    public void setEtl_context(String etl_context) {
        this.etl_context = etl_context;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


    public String getPlan_count() {
        return plan_count;
    }

    public void setPlan_count(String plan_count) {
        this.plan_count = plan_count;
    }

    public String getJob_context() {
        return job_context;
    }

    public void setJob_context(String job_context) {
        this.job_context = job_context;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getJob_type() {
        return job_type;
    }

    public void setJob_type(String job_type) {
        this.job_type = job_type;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public String getJob_model() {
        return job_model;
    }

    public void setJob_model(String job_model) {
        this.job_model = job_model;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getLast_status() {
        return last_status;
    }

    public void setLast_status(String last_status) {
        this.last_status = last_status;
    }

    public Date getLast_time() {
        return last_time;
    }

    public void setLast_time(Date last_time) {
        this.last_time = last_time;
    }

    public Date getNext_time() {
        return next_time;
    }

    public void setNext_time(Date next_time) {
        this.next_time = next_time;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getEtl_task_id() {
        return etl_task_id;
    }

    public void setEtl_task_id(String etl_task_id) {
        this.etl_task_id = etl_task_id;
    }
}
