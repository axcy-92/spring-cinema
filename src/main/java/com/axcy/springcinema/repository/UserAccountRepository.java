package com.axcy.springcinema.repository;

import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.entity.user.UserAccount;

import java.util.Collection;

/**
 * @author Aleksei_Cherniavskii
 */

public interface UserAccountRepository {

    UserAccount save(UserAccount userAccount);
    void delete(long id);
    void deleteByUserId(long user);
    UserAccount findById(long id);
    UserAccount findByUser(User user);

    Collection<UserAccount> getAll();

}
