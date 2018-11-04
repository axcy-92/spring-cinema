package com.axcy.springcinema.entity.user;

import com.axcy.springcinema.service.Roles;
import com.axcy.springcinema.entity.Role;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {

    private Long id;
    private String name;
    private String email;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    private String password;
    private List<Role> roles = new ArrayList<>();

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(long id, String name, String email) {
        this(name, email);
        this.id = id;
    }

    public User(String name, String email, LocalDate birthday) {
        this(name, email);
        this.birthday = birthday;
    }

    public User(String name, String email, LocalDate birthday, String password) {
        this(name, email, birthday);
        this.password = password;
    }

    public User(long id, String name, String email, LocalDate birthday) {
        this(name, email, birthday);
        this.id = id;
    }

    public User() {
        this(null, null);
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public boolean isManager() {
        return getRoles().stream()
                .anyMatch(role -> role.getName().equals(Roles.BOOKING_MANAGER.getDesc()));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", password='[PROTECTED]'" +
                ", roles=" + roles +
                '}';
    }
}
