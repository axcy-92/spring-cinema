package com.axcy.springcinema.entity;

import com.axcy.springcinema.entity.user.User;

import java.util.Date;

public class Win {
    private Long id;
    private User user;
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
