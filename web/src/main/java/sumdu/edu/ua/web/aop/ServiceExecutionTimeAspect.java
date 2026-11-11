package sumdu.edu.ua.web.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceExecutionTimeAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceExecutionTimeAspect.class);

    @Around("execution(* sumdu.edu.ua.core.service..*(..))")
    public Object profileServiceMethods(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = pjp.getSignature().toShortString();

        try {
            Object result = pjp.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            log.info("[AOP TIMER] Method {} executed in {} ms", methodName, elapsedTime);
            return result;
        } catch (Throwable ex) {
            long elapsedTime = System.currentTimeMillis() - start;
            log.error("[AOP TIMER] Method {} failed in {} ms with exception: {}", methodName, elapsedTime, ex.getMessage());
            throw ex;
        }
    }
}