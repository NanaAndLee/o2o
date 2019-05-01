package com.lee.o2o.exceptions;

public class ProductCategoryOperationException extends RuntimeException{

	private static final long serialVersionUID = 2461417945412516538L;
	
	public ProductCategoryOperationException(String msg) {
		super(msg);
	}
}
