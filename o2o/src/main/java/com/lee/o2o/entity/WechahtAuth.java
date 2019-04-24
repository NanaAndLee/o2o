package com.lee.o2o.entity;

import java.util.Date;

public class WechahtAuth {
	private Long wecdhatAuthId;
	private String openId;
	private Date createTime;
	private PersonInfo personInfo;

	public Long getWecdhatAuthId() {
		return wecdhatAuthId;
	}

	public void setWecdhatAuthId(Long wecdhatAuthId) {
		this.wecdhatAuthId = wecdhatAuthId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public PersonInfo getPersonInfo() {
		return personInfo;
	}

	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}

}
