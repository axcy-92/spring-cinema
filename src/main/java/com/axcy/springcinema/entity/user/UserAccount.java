package com.axcy.springcinema.entity.user;

/**
 * @author Aleksei_Cherniavskii
 */

public class UserAccount {

    private Long id;
    private User user;
    private float cash;

    public UserAccount() {}

    public UserAccount(Long id, User user, float cash) {
        this.id = id;
        this.user = user;
        this.cash = cash;
    }

    public UserAccount(User user, float cash) {
        this.user = user;
        this.cash = cash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public float getCash() {
        return cash;
    }

    public void setCash(float cash) {
        this.cash = cash;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", user=" + user +
                ", cash=" + cash +
                '}';
    }
}
