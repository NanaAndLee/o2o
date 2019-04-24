package com.lee.o2o.entity;

import java.util.Date;

public class Area {
	
	/**
	 * 使用引用类型不适用基础类型
	 * 因为基础类型不赋值的话会赋予默认值
	 * 而引用类型的话就是空     在其它类里面如果不赋值
	 * 我们希望它是空的   使用引用类型
	 */
	//ID
	private Integer areaId;
	
	//名称
	private String areaName;

	//权重
	private Integer priority;

	//创建时间
	private Date createTime;

	//更新时间
	private Date lastEditTime;

	
	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(Date lastEditTime) {
		this.lastEditTime = lastEditTime;
	}

}
