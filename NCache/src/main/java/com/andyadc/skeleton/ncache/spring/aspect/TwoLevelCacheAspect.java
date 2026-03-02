package com.andyadc.skeleton.ncache.spring.aspect;

import com.andyadc.skeleton.ncache.api.Cache;
import com.andyadc.skeleton.ncache.impl.TwoLevelCacheManager;
import com.andyadc.skeleton.ncache.spring.annotation.CacheRefresh;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * Aspect to handle custom two-level cache annotations.
 */
@Aspect
@Component
public class TwoLevelCacheAspect {

    private static final Logger logger = LoggerFactory.getLogger(TwoLevelCacheAspect.class);

    private final TwoLevelCacheManager cacheManager;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Autowired
    public TwoLevelCacheAspect(TwoLevelCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Around("@annotation(cacheRefresh)")
    public Object handleCacheRefresh(ProceedingJoinPoint joinPoint, CacheRefresh cacheRefresh) throws Throwable {
        Object result = joinPoint.proceed();

        String[] cacheNames = cacheRefresh.value();
        if (cacheNames.length == 0) {
            return result;
        }

        // Evaluate key
        Object key = evaluateKey(joinPoint, cacheRefresh.key());

        // Check condition
        if (!cacheRefresh.condition().isEmpty()) {
            Boolean condition = evaluateCondition(joinPoint, cacheRefresh.condition(), result);
            if (!Boolean.TRUE.equals(condition)) {
                return result;
            }
        }

        // Refresh caches
        for (String cacheName : cacheNames) {
            Cache<Object, Object> cache = cacheManager.getCache(cacheName);

            if (cacheRefresh.async()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        cache.put(key, result);
                        logger.debug("Async cache refresh for key {} in cache {}", key, cacheName);
                    } catch (Exception e) {
                        logger.error("Failed to refresh cache {} for key {}", cacheName, key, e);
                    }
                });
            } else {
                cache.put(key, result);
                logger.debug("Cache refresh for key {} in cache {}", key, cacheName);
            }
        }

        return result;
    }

    private Object evaluateKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        if (keyExpression == null || keyExpression.isEmpty()) {
            // Default key generation based on method parameters
            Object[] args = joinPoint.getArgs();
            if (args.length == 1) {
                return args[0];
            }
            return java.util.Arrays.deepHashCode(args);
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(), method, args, parameterNameDiscoverer
        );

        Expression expression = parser.parseExpression(keyExpression);
        return expression.getValue(context);
    }

    private Boolean evaluateCondition(ProceedingJoinPoint joinPoint, String conditionExpression, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(), method, args, parameterNameDiscoverer
        );
        context.setVariable("result", result);

        Expression expression = parser.parseExpression(conditionExpression);
        return expression.getValue(context, Boolean.class);
    }

}
