package com.andyadc.summer.jdbc.tx;

import com.andyadc.summer.annotation.Transactional;
import com.andyadc.summer.aop.AnnotationProxyBeanPostProcessor;

public class TransactionalBeanPostProcessor extends AnnotationProxyBeanPostProcessor<Transactional> {

}
