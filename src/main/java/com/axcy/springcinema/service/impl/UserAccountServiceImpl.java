package com.axcy.springcinema.service.impl;

import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.entity.user.UserAccount;
import com.axcy.springcinema.repository.UserAccountRepository;
import com.axcy.springcinema.service.UserAccountService;
import com.axcy.springcinema.service.exception.LackOfFundsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author Aleksei_Cherniavskii
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {

    private static final Logger LOG = LoggerFactory.getLogger(UserAccountService.class);

    @Autowired
    UserAccountRepository userAccountRepository;

    @Override
    @Transactional("txManager")
    public UserAccount create(User user, float cash) {
        UserAccount userAccount = new UserAccount(user, cash);
        LOG.info(userAccount + " created");
        return userAccountRepository.save(userAccount);
    }

    @Override
    @Transactional("txManager")
    public void remove(UserAccount userAccount) {
        if (userAccount != null && userAccount.getId() != null) {
            LOG.info(userAccount + " removed");
            userAccountRepository.delete(userAccount.getId());
        }
    }

    @Override
    public UserAccount getById(long id) {
        return userAccountRepository.findById(id);
    }

    @Override
    public UserAccount getAccountByUser(User user) {
        return userAccountRepository.findByUser(user);
    }

    @Override
    public Collection<UserAccount> getAll() {
        return userAccountRepository.getAll();
    }

    @Override
    @Transactional("txManager")
    public UserAccount doPayment(UserAccount userAccount, float amount) throws LackOfFundsException {
        if (userAccount != null && userAccount.getId() != null) {
            LOG.info("withdrawal from an " + userAccount + " in the amount of " + amount);
            float cash = userAccount.getCash();
            if (cash >= amount) {
                float balance = cash - amount;
                userAccount.setCash(balance);
                return userAccountRepository.save(userAccount);
            } else {
                throw new LackOfFundsException(userAccount);
            }
        }
        return userAccount;
    }

    @Override
    @Transactional("txManager")
    public UserAccount topUpAnAccount(UserAccount userAccount, float amount) {
        if (userAccount != null && userAccount.getId() != null) {
            LOG.info("to top up an " + userAccount + " in the amount of " + amount);
            userAccount.setCash(userAccount.getCash() + amount);
            return userAccountRepository.save(userAccount);
        }
        return userAccount;
    }

}
