package com.lee.o2o.enums;

public enum ProductStateEnum {
	SUCCESS(1, "操作成功"),INNER_ERROR(-2001, "操作失败"),EMPTY(-2002, "传入了空置");
	
	private int state;
	private String stateInfo;
	
	
	private ProductStateEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}
	
	//一依据传入的参数返回对相应的enum值
	
	public static ProductStateEnum stateOf(int state) {
		for(ProductStateEnum stateEnum : values()) {
			if(stateEnum.getState() == state) {
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
