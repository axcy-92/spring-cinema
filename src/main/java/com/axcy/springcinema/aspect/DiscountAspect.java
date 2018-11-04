package com.axcy.springcinema.aspect;

import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.repository.CounterRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class DiscountAspect {

    private static final Logger log = LoggerFactory.getLogger(DiscountAspect.class);

    @Autowired
    private CounterRepository counterRepository;

    @Pointcut("execution(* com.axcy.springcinema.service.DiscountService.getDiscount(..))")
    public void pointcutDiscount() {
    }

    @Transactional("txManager")
    @Around("pointcutDiscount()")
    public float count(ProceedingJoinPoint jp) throws Throwable {
        float discount = (float) jp.proceed();
        User user = (User) jp.getArgs()[0];
        if (discount != 0 && user != null) {
            String counterName = Counters.DISCOUNT_USER_ID.name() + "_" + user.getId();
            int count = counterRepository.getByName(counterName) + 1;
            counterRepository.save(counterName, count);
            log.info(counterName + ": " + count);

            count = counterRepository.getByName(Counters.DISCOUNT_TOTAL.name()) + 1;
            counterRepository.save(Counters.DISCOUNT_TOTAL.name(), count);
            log.info(Counters.DISCOUNT_TOTAL+": "+count);
        }
        return discount;
    }
}
