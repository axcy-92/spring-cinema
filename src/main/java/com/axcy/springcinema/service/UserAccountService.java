package com.axcy.springcinema.service;

import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.entity.user.UserAccount;
import com.axcy.springcinema.service.exception.LackOfFundsException;

import java.util.Collection;

/**
 * @author Aleksei_Cherniavskii
 */
public interface UserAccountService {

    UserAccount create(User user, float cash);
    void remove(UserAccount userAccount);
    UserAccount getById(long id);
    UserAccount getAccountByUser(User user);
    Collection<UserAccount> getAll();
    UserAccount doPayment(UserAccount userAccount, float amount) throws LackOfFundsException;
    UserAccount topUpAnAccount(UserAccount userAccount, float amount);

}
