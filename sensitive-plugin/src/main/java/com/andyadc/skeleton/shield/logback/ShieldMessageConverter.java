package com.andyadc.skeleton.shield.logback;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.andyadc.skeleton.shield.utils.ReflectUtils;
import org.slf4j.helpers.MessageFormatter;

import java.util.stream.Stream;

/**
 * 脱敏转换器
 */
public class ShieldMessageConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        if (event.getArgumentArray() != null) {
            return MessageFormatter.arrayFormat(
                    event.getMessage(),
                    Stream.of(event.getArgumentArray())
                            .map(ReflectUtils::reflectToLogStringByFields).toArray()
            ).getMessage();
        }
        return super.convert(event);
    }

}
