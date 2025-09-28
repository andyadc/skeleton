package com.andyadc.skeleton.shield.logback;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 脱敏转换器
 */
public class ShieldMessageConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        if (event.getArgumentArray() != null) {

        }
        return super.convert(event);
    }

}
