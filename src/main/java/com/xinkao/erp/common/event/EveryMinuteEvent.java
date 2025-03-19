package com.xinkao.erp.common.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

/**
 *  自定义每分钟事件
 * @author hys_thanks
 */
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
