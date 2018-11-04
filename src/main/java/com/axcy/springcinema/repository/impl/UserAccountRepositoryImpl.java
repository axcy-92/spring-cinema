package com.axcy.springcinema.repository.impl;

import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.entity.user.UserAccount;
import com.axcy.springcinema.repository.UserAccountRepository;
import com.axcy.springcinema.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aleksei_Cherniavskii
 */
@Repository
public class UserAccountRepositoryImpl implements UserAccountRepository {

    private static final Logger LOG = LoggerFactory.getLogger(UserAccountRepositoryImpl.class);

    private static final String UPDATE_USER_ACCOUNT_BY_ID = "UPDATE user_account SET user_id=?, cash=? WHERE id=?";
    private static final String UPDATE_USER_ACCOUNT_BY_USER_ID = "UPDATE user_account SET cash=? WHERE user_id=?";
    private static final String DELETE_USER_ACCOUNT = "DELETE FROM user_account WHERE id=?";
    private static final String DELETE_USER_ACCOUNT_BY_USER_ID = "DELETE FROM user_account WHERE user_id=?";

    private static final String SELECT_BY_ID = "SELECT * FROM user_account WHERE id=?";
    private static final String SELECT_BY_USER_ID = "SELECT * FROM user_account WHERE user_id=?";
    private static final String SELECT_ALL = "SELECT * FROM user_account";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional("txManager")
    public UserAccount save(UserAccount userAccount) {
        if (userAccount != null && userAccount.getUser() != null) {
            UserAccount updatedAccount = null;
            if (userAccount.getId() != null) {
                LOG.info("update user account by id " + userAccount.getId());
                jdbcTemplate.update(UPDATE_USER_ACCOUNT_BY_ID,
                        userAccount.getUser() != null ? userAccount.getUser().getId() : null,
                        userAccount.getCash(),
                        userAccount.getId());
                updatedAccount = findById(userAccount.getId());
            } else if (userAccount.getUser().getId() != null) {
                LOG.info("update user account by user id " + userAccount.getUser().getId());
                jdbcTemplate.update(UPDATE_USER_ACCOUNT_BY_USER_ID,
                        userAccount.getCash(),
                        userAccount.getUser().getId());
                updatedAccount = findByUser(userAccount.getUser());
            } else if (userAccount.getUser().getName() != null) {
                LOG.info("update user account by user name " + userAccount.getUser().getName());
                User user = userRepository.findByName(userAccount.getUser().getName());
                if (user != null) {
                    jdbcTemplate.update(UPDATE_USER_ACCOUNT_BY_USER_ID,
                            userAccount.getCash(),
                            user.getId());
                    updatedAccount = findByUser(userAccount.getUser());
                }
            } else if (userAccount.getUser().getEmail() != null) {
                LOG.info("update user account by user email " + userAccount.getUser().getEmail());
                User user = userRepository.findByEmail(userAccount.getUser().getEmail());
                if (user != null) {
                    jdbcTemplate.update(UPDATE_USER_ACCOUNT_BY_USER_ID,
                            userAccount.getCash(),
                            user.getId());
                    updatedAccount = findByUser(userAccount.getUser());
                }
            }
            if (updatedAccount == null) {
                LOG.info("insert new user account");
                SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("user_account");
                insert.setGeneratedKeyName("id");
                Map<String, Object> args = new HashMap<>();
                args.put("cash", userAccount.getCash());

                User user = userAccount.getUser();
                if (user.getId() == null) {
                    if (user.getName() != null) {
                        user = userRepository.findByName(user.getName());
                    } else if (user.getEmail() != null) {
                        user = userRepository.findByEmail(user.getEmail());
                    } else {
                        return userAccount;
                    }
                }
                args.put("user_id", user.getId());
                userAccount.setId(insert.executeAndReturnKey(args).longValue());
            } else {
                userAccount = updatedAccount;
            }
        }
        LOG.info("save user account successfully complete :\n" + userAccount);
        return userAccount;

    }

    @Override
    @Transactional("txManager")
    public void delete(long id) {
        jdbcTemplate.update(DELETE_USER_ACCOUNT, id);
    }

    @Override
    @Transactional("txManager")
    public void deleteByUserId(long userId) {
        jdbcTemplate.update(DELETE_USER_ACCOUNT_BY_USER_ID, userId);
    }

    @Override
    public UserAccount findById(long id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_BY_ID, userAccountMapper(), id);
        } catch (EmptyResultDataAccessException ignored) {
        }
        return null;
    }

    @Override
    public UserAccount findByUser(User user) {
        try {
            if (user != null) {
                if (user.getId() != null) {
                    return jdbcTemplate.queryForObject(SELECT_BY_USER_ID, userAccountMapper(), user.getId());
                }
                if (user.getName() != null) {
                    user = userRepository.findByName(user.getName());
                    return jdbcTemplate.queryForObject(SELECT_BY_USER_ID, userAccountMapper(), user.getId());
                }
                if (user.getEmail() != null) {
                    user = userRepository.findByEmail(user.getEmail());
                    return jdbcTemplate.queryForObject(SELECT_BY_USER_ID, userAccountMapper(), user.getId());
                }
            }
        } catch (EmptyResultDataAccessException ignored) {
        }
        return null;
    }

    @Override
    public Collection<UserAccount> getAll() {
        return jdbcTemplate.query(SELECT_ALL, userAccountMapper());
    }

    private RowMapper<UserAccount> userAccountMapper() {
        return (rs, rowNum) -> {
            return new UserAccount(
                    rs.getLong(1),
                    userRepository.findById(rs.getLong(2)),
                    rs.getFloat(3)
            );
        };
    }
}
