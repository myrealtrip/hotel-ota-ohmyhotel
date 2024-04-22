package com.myrealtrip.ohmyhotel.core.config.cache.aop;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public interface CacheAspectHandleable {

    Object handleCacheable(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;
    Object handleCachePut(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;
    Object handleCacheEvict(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;

    default String parseParam(String[] parameterNames, Object[] args, String key) {
        if (StringUtils.isBlank(key)) {
            return StringUtils.EMPTY;
        }
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }
}
