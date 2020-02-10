package com.zyc.zspringboot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyc.zspringboot.dao.QuartzJobMapper;
import com.zyc.zspringboot.entity.*;
import com.zyc.zspringboot.job.SnowflakeIdWorker;
import com.zyc.zspringboot.quartz.QuartzManager2;
import com.zyc.zspringboot.service.DataSourcesService;
import com.zyc.zspringboot.service.DispatchTaskService;
import com.zyc.zspringboot.service.EtlTaskService;
import com.zyc.zspringboot.service.ZdhLogsService;
import com.zyc.zspringboot.util.DBUtil;
import com.zyc.zspringboot.util.HttpUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
public class ZdhFlowController {


    @Autowired
    DataSourcesService dataSourcesServiceImpl;
    @Autowired
    EtlTaskService etlTaskService;
    @Autowired
    DispatchTaskService dispatchTaskService;
    @Autowired
    ZdhLogsService zdhLogsService;
    @Autowired
    QuartzManager2 quartzManager2;

    @RequestMapping("/zdh_flow_index")
    public String data_sources_index() {

        return "zdh_flow/index";
    }

//    @RequestMapping(value = "/data_sources_list", produces = "text/html;charset=UTF-8")
//    @ResponseBody
//    public String data_sources_list(String[] ids) {
//
//        DataSourcesInfo dataSourcesInfo = new DataSourcesInfo();
//       // dataSourcesInfo.setOwner(getUser().getId());
//        List<DataSourcesInfo> list = dataSourcesServiceImpl.select(dataSourcesInfo);
//
//        return JSON.toJSONString(list);
//    }


}
