package com.xinkao.erp.common.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EverySecondEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = 1L;
	
	public EverySecondEvent(Object source) {
		super(source);
	}

}
