package com.axcy.springcinema.service.impl;

import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.service.DiscountService;
import com.axcy.springcinema.service.DiscountStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private List<DiscountStrategy> strategies;


    @Override
    public float getDiscount(User user, Event event, LocalDateTime dateTime) {
        final double[] discount = {0};
        strategies.stream()
                .mapToDouble(strategy -> strategy.calculate(user))
                .max().ifPresent(d -> discount[0] = d);
        return (float) discount[0];
    }
}
