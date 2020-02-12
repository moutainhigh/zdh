package com.zyc.zdh.dao;

import com.zyc.notscan.BaseMapper;
import com.zyc.zdh.entity.QuartzJobInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


public interface QuartzJobMapper extends BaseMapper<QuartzJobInfo> {

    @Update({ "update quartz_job_info set status = #{status} where job_id = #{job_id}" })
    public int updateStatus(@Param("job_id") String job_id,@Param("status") String status);

    @Select(value="select * from quartz_job_info where owner=#{owner}")
    public List<QuartzJobInfo> selectByOwner(@Param("owner") String owner);
}