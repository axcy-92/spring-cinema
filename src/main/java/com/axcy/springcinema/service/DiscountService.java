package com.axcy.springcinema.service;

import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.entity.user.User;

import java.time.LocalDateTime;

public interface DiscountService {
    float getDiscount(User user, Event event, LocalDateTime dateTime);
}
