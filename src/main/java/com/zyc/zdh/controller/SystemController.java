package com.zyc.zdh.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.zyc.zdh.entity.PageBase;
import com.zyc.zdh.entity.Role;
import com.zyc.zdh.service.RoleService;

@RequestMapping("system")
@Controller
public class SystemController {

	@Autowired
	private RoleService roleService;
	
	@RequestMapping("index")
	public String getSystemIndex(){
		return "system/system-log";
	}
	
	@RequestMapping("role")
	public String getSystemRole(PageBase page,ModelMap model){
		/*List<Role> findList = roleService.findList(page);
		model.addAttribute("roleList", findList);*/
		return "system/system-role";
	}
	
	@RequestMapping(value="list",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String list(@RequestParam String aoData) throws UnsupportedEncodingException{
		return roleService.list(aoData);
	}
	@RequestMapping(value="addRole")
	public String addRole(String id){
		
		return "system-role-add";
	}
	@RequestMapping(value="editRole")
	public String editRole(String id,ModelMap model){
		Role role=roleService.getRole(id);
		model.addAttribute("role", role); 
		return "system/system-role-edit";
	}
	@RequestMapping(value="delRole")
	@ResponseBody
	public String delRole(String id){
		int result=roleService.delRole(id);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("result", result);
		return jsonObject.toString();
	}
	
	
}
