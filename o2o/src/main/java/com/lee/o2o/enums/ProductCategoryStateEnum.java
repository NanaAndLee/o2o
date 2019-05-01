package com.lee.o2o.enums;

public enum ProductCategoryStateEnum {
	SUCCESS(1, "创建成功"),INNER_ERROR(-1001, "操作失败"),EMPTY_LIST(-1002, "添加数少于1");
	private int state;
	private String stateInfo;

	private ProductCategoryStateEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public static ProductCategoryStateEnum stateof(int index) {
		for(ProductCategoryStateEnum stateEnum : values()) {
			if(stateEnum.getState() == index) {
				return stateEnum;
			}
		}
		return null;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

}
