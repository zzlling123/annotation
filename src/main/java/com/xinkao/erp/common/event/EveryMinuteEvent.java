package com.xinkao.erp.common.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EveryMinuteEvent extends ApplicationEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EveryMinuteEvent(Object source) {
		super(source);
	}

}
