
create database if NOT EXISTS mydb;

drop table if EXISTS account_info;
create table account_info(
id int not null AUTO_INCREMENT,
user_name varchar(50),
user_password varchar(100),
email varchar(100),
PRIMARY KEY (id)
);

INSERT INTO `account_info` VALUES (2,'zyc','123456','zyc@qq.com');

drop table data_sources_info;
create table data_sources_info(
 id int not null AUTO_INCREMENT,
 data_source_context varchar(100),
 data_source_type varchar(100),
 driver varchar(100),
 url varchar(100),
 username varchar(100),
 password varchar(100),
 owner varchar(100),
  PRIMARY KEY (id)
);

INSERT INTO `data_sources_info` VALUES (53,'mydb','JDBC','com.mysql.cj.jdbc.Driver','jdbc:mysql://127.0.0.1:3306/mydb?serverTimezone=GMT%2B8&useSSL=false','zyc','123456','2'),(54,'csv','HDFS','','','zyc@qq.com','123456','2'),(55,'mydb2','JDBC','com.mysql.cj.jdbc.Driver','jdbc:mysql://127.0.0.1:3306/mydb?serverTimezone=GMT%2B8&useSSL=false','zyc','123456','2'),(56,'HIVE1','HIVE','','','','','2');

drop table if EXISTS etl_task_info;
create table etl_task_info(
 id int not null AUTO_INCREMENT,
 etl_context VARCHAR(200),
 data_sources_choose_input varchar(100),
 data_source_type_input varchar(100),
 data_sources_table_name_input varchar(100),
 data_sources_file_name_input varchar(100),
 data_sources_file_columns text,
 data_sources_table_columns text,
 data_sources_params_input varchar(500),
 data_sources_filter_input varchar(500),

 data_sources_choose_output varchar(100),
 data_source_type_output varchar(100),
 data_sources_table_name_output varchar(100),
 data_sources_file_name_output varchar(100),
 data_sources_params_output varchar(500),
 column_datas text,
 data_sources_clear_output varchar(500),
  owner varchar(100),
   PRIMARY KEY (id)
);

INSERT INTO `etl_task_info` VALUES (2,'我的第一个ETL任务','53','JDBC','data_sources_info','','','id,data_source_context,data_source_type,driver,url,username,password','','','53','JDBC','t2','t2','','[{\"column_expr\":\"id\",\"column_alias\":\"id\"},{\"column_expr\":\"data_source_context\",\"column_alias\":\"data_source_context\"},{\"column_expr\":\"data_source_type\",\"column_alias\":\"data_source_type\"},{\"column_expr\":\"driver\",\"column_alias\":\"driver\"},{\"column_expr\":\"url\",\"column_alias\":\"url\"},{\"column_expr\":\"username\",\"column_alias\":\"username\"},{\"column_expr\":\"password\",\"column_alias\":\"password\"}]','','2'),(3,'mydb.data_source_info -> csv','53','JDBC','data_sources_info','','','id,data_source_context,data_source_type,driver,url,username,password','','','54','HDFS','','/mydb.data_source_info','{\"fileType\":\"csv\",\"model\":\"overwrite\"}','[{\"column_expr\":\"id\",\"column_alias\":\"id\"},{\"column_expr\":\"data_source_context\",\"column_alias\":\"data_source_context\"},{\"column_expr\":\"data_source_type\",\"column_alias\":\"data_source_type\"},{\"column_expr\":\"driver\",\"column_alias\":\"driver\"},{\"column_expr\":\"url\",\"column_alias\":\"url\"},{\"column_expr\":\"username\",\"column_alias\":\"username\"},{\"column_expr\":\"password\",\"column_alias\":\"password\"}]','','2'),(4,'csv->mydb.t6','54','HDFS','','/mydb.data_source_info','id,data_source_context,data_source_type,driver,url,username,password','','{\"fileType\":\"orc\"}','','53','JDBC','t6','t6','','[{\"column_expr\":\"id\",\"column_alias\":\"id\"},{\"column_expr\":\"data_source_context\",\"column_alias\":\"data_source_context\"},{\"column_expr\":\"data_source_type\",\"column_alias\":\"data_source_type\"},{\"column_expr\":\"driver\",\"column_alias\":\"driver\"},{\"column_expr\":\"url\",\"column_alias\":\"url\"},{\"column_expr\":\"username\",\"column_alias\":\"username\"},{\"column_expr\":\"concat(password,\'abc\')\",\"column_alias\":\"password\"}]','delete from t6 where username=\'zdh.params.username\'','2'),(5,'2020->db','54','HDFS','','/2020.csv','name,sex,age,job,addr','','{\"encoding\":\"GBK\"}','','53','JDBC','t7','t7','','[{\"column_expr\":\"name\",\"column_alias\":\"name\"},{\"column_expr\":\"sex\",\"column_alias\":\"sex\"},{\"column_expr\":\"age\",\"column_alias\":\"age\"},{\"column_expr\":\"job\",\"column_alias\":\"job\"},{\"column_expr\":\"addr\",\"column_alias\":\"addr\"}]','delete from t7','2'),(6,'HIVE1','53','JDBC','t7','','','name,sex,age,job,addr','','','56','HIVE','','hive_t7','','[{\"column_expr\":\"name\",\"column_alias\":\"name\"},{\"column_expr\":\"sex\",\"column_alias\":\"sex\"},{\"column_expr\":\"age\",\"column_alias\":\"age\"},{\"column_expr\":\"job\",\"column_alias\":\"job\"},{\"column_expr\":\"addr\",\"column_alias\":\"addr\"}]','','2'),(7,'BOC_SCHEMA->TXT','54','HDFS','','/BOC_SCHEMA.sql','TB_NAME,TB_CONTENT,FILE_TYPE,FILE_PATH,FILE_SEP,FILE_NAMES,IS_INCRE,SYSTEM_NAME,ENCODER_TYPE,SCHEMA,BATCH_ID,IS_ENABLE,UPDATE_DATE','','{\"sep\":\",\\\\$\"}','','54','HDFS','','/BOC_TMP','','[{\"column_expr\":\"TB_NAME\",\"column_alias\":\"TB_NAME\"},{\"column_expr\":\"TB_CONTENT\",\"column_alias\":\"TB_CONTENT\"},{\"column_expr\":\"FILE_TYPE\",\"column_alias\":\"FILE_TYPE\"},{\"column_expr\":\"FILE_PATH\",\"column_alias\":\"FILE_PATH\"},{\"column_expr\":\"FILE_SEP\",\"column_alias\":\"FILE_SEP\"},{\"column_expr\":\"FILE_NAMES\",\"column_alias\":\"FILE_NAMES\"},{\"column_expr\":\"IS_INCRE\",\"column_alias\":\"IS_INCRE\"},{\"column_expr\":\"SYSTEM_NAME\",\"column_alias\":\"SYSTEM_NAME\"},{\"column_expr\":\"ENCODER_TYPE\",\"column_alias\":\"ENCODER_TYPE\"},{\"column_expr\":\"SCHEMA\",\"column_alias\":\"SCHEMA\"},{\"column_expr\":\"BATCH_ID\",\"column_alias\":\"BATCH_ID\"},{\"column_expr\":\"IS_ENABLE\",\"column_alias\":\"IS_ENABLE\"},{\"column_expr\":\"UPDATE_DATE\",\"column_alias\":\"UPDATE_DATE\"}]','','2');

drop table if EXISTS dispatch_task_info;
create table dispatch_task_info(
id int not null AUTO_INCREMENT,
dispatch_context varchar(100),
etl_task_id varchar(100),
etl_context varchar(100),
 owner varchar(100),
PRIMARY KEY (id)
);

INSERT INTO `dispatch_task_info` VALUES (5,'我的第2个调度任务','2','我的第一个ETL任务',NULL,'2'),(6,'mydb.data_source_info -> csv','3','mydb.data_source_info -> csv',NULL,'2'),(7,'csv->mydb.t6','4','csv->mydb.t6','{\"zdh.params.username\":\"zyc\"}','2'),(8,'调度2020->db','5','2020->db','','2'),(9,'HIVE1','6','HIVE1','','2'),(10,'BOC_SCHEMA->TXT','7','BOC_SCHEMA->TXT','','2');

drop table if EXISTS quartz_job_info;
create table quartz_job_info(
job_id VARCHAR(100) ,
job_context VARCHAR(100) ,
job_type VARCHAR(100),
start_time DATE ,
end_time date,
job_model VARCHAR(2),
plan_count VARCHAR(5),
count int,
command VARCHAR(100),
params text,
last_status VARCHAR(100),
last_time date,
next_time date,
expr VARCHAR(100),
status VARCHAR(100),
ip VARCHAR(100),
user VARCHAR(100),
password VARCHAR(100),
etl_task_id VARCHAR(100),
etl_context varchar(100),
owner varchar(100),
PRIMARY key(job_id)
);

INSERT INTO `quartz_job_info` VALUES ('675001061114642432','fdfdsfd','SHELL','2020-01-27','2020-02-10','1','-1',7,' if [ ! -f \"/data/filename\" ];then echo \"文件不存在\"; else echo \"true\"; fi','',NULL,NULL,'2020-01-27','0/10 * * * * ? *','remove',NULL,NULL,NULL,'3','mydb.data_source_info -> csv','2'),('677259226690617344','第一个调度任务','SHELL','2019-01-01','2020-02-12','1','5',0,'command',NULL,NULL,NULL,NULL,'10s','pause',NULL,NULL,NULL,'1',NULL,NULL);



drop table if EXISTS task_info;
create table task_info(
TASK_ID varchar(100),
TASK_NAME varchar(100),
TASK_GROUP varchar(100),
TASK_TRIGGER varchar(100),
TASK_EXPRESSION varchar(100),
TASK_STATUS varchar(100),
TASK_TABLENAME varchar(100),
TASK_BEANMAPPER varchar(100),
TASK_PARAM varchar(100),
TASK_DESC varchar(100),
LAST_UPDATE_TIME date,
TASK_PLAN_COUNT int,
TASK_COUNT varchar(100),
CREATE_TIME date,
PRIMARY KEY (TASK_ID)
);


drop TABLE if EXISTS zdh_logs;
create table zdh_logs(
job_id VARCHAR(100),
log_time TIMESTAMP ,
msg text
);


create database if NOT EXISTS quartz;

-- quartz
DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

CREATE TABLE QRTZ_JOB_DETAILS(
SCHED_NAME VARCHAR(120) NOT NULL,
JOB_NAME VARCHAR(200) NOT NULL,
JOB_GROUP VARCHAR(200) NOT NULL,
DESCRIPTION VARCHAR(250) NULL,
JOB_CLASS_NAME VARCHAR(250) NOT NULL,
IS_DURABLE VARCHAR(1) NOT NULL,
IS_NONCONCURRENT VARCHAR(1) NOT NULL,
IS_UPDATE_DATA VARCHAR(1) NOT NULL,
REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
JOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
JOB_NAME VARCHAR(200) NOT NULL,
JOB_GROUP VARCHAR(200) NOT NULL,
DESCRIPTION VARCHAR(250) NULL,
NEXT_FIRE_TIME BIGINT(13) NULL,
PREV_FIRE_TIME BIGINT(13) NULL,
PRIORITY INTEGER NULL,
TRIGGER_STATE VARCHAR(16) NOT NULL,
TRIGGER_TYPE VARCHAR(8) NOT NULL,
START_TIME BIGINT(13) NOT NULL,
END_TIME BIGINT(13) NULL,
CALENDAR_NAME VARCHAR(200) NULL,
MISFIRE_INSTR SMALLINT(2) NULL,
JOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
REPEAT_COUNT BIGINT(7) NOT NULL,
REPEAT_INTERVAL BIGINT(12) NOT NULL,
TIMES_TRIGGERED BIGINT(10) NOT NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_CRON_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
CRON_EXPRESSION VARCHAR(120) NOT NULL,
TIME_ZONE_ID VARCHAR(80),
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_BLOB_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
BLOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_CALENDARS (
SCHED_NAME VARCHAR(120) NOT NULL,
CALENDAR_NAME VARCHAR(200) NOT NULL,
CALENDAR BLOB NOT NULL,
PRIMARY KEY (SCHED_NAME,CALENDAR_NAME))
ENGINE=InnoDB;

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP))
ENGINE=InnoDB;

CREATE TABLE QRTZ_FIRED_TRIGGERS (
SCHED_NAME VARCHAR(120) NOT NULL,
ENTRY_ID VARCHAR(95) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
INSTANCE_NAME VARCHAR(200) NOT NULL,
FIRED_TIME BIGINT(13) NOT NULL,
SCHED_TIME BIGINT(13) NOT NULL,
PRIORITY INTEGER NOT NULL,
STATE VARCHAR(16) NOT NULL,
JOB_NAME VARCHAR(200) NULL,
JOB_GROUP VARCHAR(200) NULL,
IS_NONCONCURRENT VARCHAR(1) NULL,
REQUESTS_RECOVERY VARCHAR(1) NULL,
PRIMARY KEY (SCHED_NAME,ENTRY_ID))
ENGINE=InnoDB;

CREATE TABLE QRTZ_SCHEDULER_STATE (
SCHED_NAME VARCHAR(120) NOT NULL,
INSTANCE_NAME VARCHAR(200) NOT NULL,
LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
CHECKIN_INTERVAL BIGINT(13) NOT NULL,
PRIMARY KEY (SCHED_NAME,INSTANCE_NAME))
ENGINE=InnoDB;

CREATE TABLE QRTZ_LOCKS (
SCHED_NAME VARCHAR(120) NOT NULL,
LOCK_NAME VARCHAR(40) NOT NULL,
PRIMARY KEY (SCHED_NAME,LOCK_NAME))
ENGINE=InnoDB;

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);





