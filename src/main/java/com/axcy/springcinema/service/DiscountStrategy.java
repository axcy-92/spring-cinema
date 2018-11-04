package com.axcy.springcinema.service;

import com.axcy.springcinema.entity.user.User;

public interface DiscountStrategy {
    float calculate(User user);
}
