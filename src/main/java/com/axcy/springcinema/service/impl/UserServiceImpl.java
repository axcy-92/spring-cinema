package com.axcy.springcinema.service.impl;

import com.axcy.springcinema.entity.Role;
import com.axcy.springcinema.entity.Ticket;
import com.axcy.springcinema.entity.user.CustomUserDetails;
import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.repository.TicketRepository;
import com.axcy.springcinema.repository.UserRepository;
import com.axcy.springcinema.service.Roles;
import com.axcy.springcinema.service.UserAccountService;
import com.axcy.springcinema.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public User create(String name, String email, String password, LocalDate birthDay) {
        User user = new User(name, email, birthDay, password);
        user.addRole(new Role(Roles.REGISTERED_USER.getDesc()));
        return user;
    }

    @Override
    @Transactional("txManager")
    public User register(User user) {
        user = userRepository.save(user);
        userAccountService.create(user, 0);
        LOG.info("User <" + user.getName() + "> is registered");
        return user;
    }

    @Override
    @Transactional("txManager")
    public void remove(User user) {
        Optional.ofNullable(user).ifPresent(u -> userRepository.delete(u.getId()));
    }

    @Override
    public User getById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public Collection<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public Collection<Ticket> getBookedTickets() {
        return ticketRepository.getBookedTickets();
    }

    @Override
    public Collection<Ticket> getBookedTicketsByUserId(long userId) {
        return ticketRepository.getBookedTicketsByUserId(userId);
    }

    @Override
    @Transactional(readOnly=true)
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository.findByName(name);
        LOG.info("found user: " + user.toString());
        CustomUserDetails userDetails = new CustomUserDetails(user.getName(), user.getPassword(),
                true, true, true, true, getGrantedAuthorities(user));
        userDetails.setId(user.getId());
        userDetails.setEmail(user.getEmail());
        userDetails.setManager(user.isManager());
        return userDetails;
    }

    private List<GrantedAuthority> getGrantedAuthorities(User user){
        List<GrantedAuthority> authorities = new ArrayList<>();

        for(Role userRole : user.getRoles()){
            LOG.info("User Role : " + userRole);
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getName()));
        }
        LOG.info("authorities :" + authorities);
        return authorities;
    }
}
