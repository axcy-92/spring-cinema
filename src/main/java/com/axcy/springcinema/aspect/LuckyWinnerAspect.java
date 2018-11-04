package com.axcy.springcinema.aspect;

import com.axcy.springcinema.entity.Ticket;
import com.axcy.springcinema.entity.Win;
import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.repository.WinsRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;

@Aspect
@Component
public class LuckyWinnerAspect {

    private static final float PRICE = 0F;
    private static final int LUCKY = 5;
    private static final Logger log = LoggerFactory.getLogger(LuckyWinnerAspect.class);
    private Random random = new Random();

    @Autowired
    private WinsRepository winsRepository;

    @Pointcut("execution(* com.axcy.springcinema.service.BookingService.bookTicket(..))")
    public void pointcutLuckyWinner() {
    }

    @Transactional("txManager")
    @Around("pointcutLuckyWinner()")
    public Object luckyWinner(ProceedingJoinPoint jp) throws Throwable {
        User user = (User) jp.getArgs()[0];
        Ticket ticket = (Ticket) jp.getArgs()[1];
        if (checkLucky() && ticket != null) {
            Win win = new Win();
            win.setDate(new Date());
            win.setUser(user);
            winsRepository.save(win);
            ticket.setPrice(PRICE);
            log.info("User " + user.getName() + " is lucky winner");
            jp.proceed();
        }
        return jp.proceed();
    }

    private boolean checkLucky() {
        int rnd = random.nextInt() % LUCKY;
        return rnd == 0;
    }

}
