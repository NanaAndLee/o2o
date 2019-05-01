package com.lee.o2o.exceptions;

public class ProductOperationException extends RuntimeException{

	private static final long serialVersionUID = 2983615139491513879L;
	
	public ProductOperationException(String msg) {
		super(msg);
	}
}
