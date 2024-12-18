package net.pointofviews.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributeLockAop {

    private static final String REDISSON_KEY = "LOCK:";
    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(net.pointofviews.common.lock.DistributeLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributeLock distributeLock = method.getAnnotation(DistributeLock.class);

        String key = REDISSON_KEY + CustomSpringELParser.getDynamicValue(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                distributeLock.key()
        );

        RLock lock = redissonClient.getLock(key);

        try {
            log.info("락을 시도합니다. 키: {}", key);

            boolean available = lock.tryLock(distributeLock.waitTime(), distributeLock.leaseTime(), distributeLock.timeUnit());

            if (available) {
                log.info("락을 획득했습니다. 키: {}", key);
            } else {
                log.warn("락 획득 실패. 키: {}", key);
                throw new IllegalAccessException("락을 획득할 수 없습니다.");
            }

            return aopForTransaction.proceed(joinPoint);

        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InterruptedException(ex.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()){
                log.info("락을 해제합니다.");
                lock.unlock();
            }
        }
    }
}
