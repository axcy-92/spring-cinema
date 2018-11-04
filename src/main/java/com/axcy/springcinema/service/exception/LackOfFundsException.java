package com.axcy.springcinema.service.exception;

import com.axcy.springcinema.entity.user.UserAccount;

/**
 * @author Aleksei_Cherniavskii
 */
public class LackOfFundsException extends Exception {

    public LackOfFundsException(UserAccount userAccount) {
        super(String.format("On account #%s for user '%s' of insufficient funds to make a payment",
                userAccount.getId(), userAccount.getUser().getName()));
    }
}
