package com.axcy.springcinema.service;

import com.axcy.springcinema.entity.Ticket;
import com.axcy.springcinema.entity.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDate;
import java.util.Collection;

public interface UserService extends UserDetailsService {
    User create(String name, String email, String password, LocalDate birthDay);
    User register(User user);
    void remove(User user);
    User getById(long id);
    User getUserByEmail(String email);
    User getUserByName(String name);
    Collection<User> getAll();
    Collection<Ticket> getBookedTickets();
    Collection<Ticket> getBookedTicketsByUserId(long userId);
}
