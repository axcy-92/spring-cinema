package com.axcy.springcinema.repository.impl;

import com.axcy.springcinema.entity.Role;
import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.repository.TicketRepository;
import com.axcy.springcinema.repository.UserAccountRepository;
import com.axcy.springcinema.repository.UserRepository;
import com.axcy.springcinema.repository.WinsRepository;
import com.axcy.springcinema.utils.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;

import static java.util.Optional.ofNullable;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

    private static final String UPDATE_USER_BY_ID = "UPDATE user SET name=?, email=?, birthDay=?, password=? WHERE id=?";
    private static final String UPDATE_USER_BY_NAME = "UPDATE user SET name=?, email=?, birthDay=?, password=? WHERE name=?";
    private static final String SELECT_BY_USER_ID = "SELECT * FROM user WHERE id=?";
    private static final String SELECT_BY_USER_EMAIL = "SELECT * FROM user WHERE email=?";
    private static final String SELECT_BY_USER_NAME = "SELECT * FROM user WHERE name=?";
    private static final String DELETE_USER = "DELETE FROM user WHERE id=?";
    private static final String DELETE_USER_ROLE = "DELETE FROM roles WHERE user_id=?";
    private static final String SELECT_ALL = "SELECT * FROM user";
    private static final String SELECT_USER_ROLES =
            "select * from role r\n" +
                    "join roles rs on rs.role_id = r.id\n" +
                    "where user_id=?";
    private static final String SELECT_ROLE_BY_NAME = "select * from role where name = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private WinsRepository winsRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional("txManager")
    public User save(User user) {
        if (user != null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            User updatedUser = null;
            List<Role> roles = user.getRoles();
            if (user.getId() != null) {
                // try update users by id
                LOG.info("try update users by id " + user.getId());
                jdbcTemplate.update(UPDATE_USER_BY_ID,
                        user.getName(),
                        user.getEmail(),
                        Convert.toTimestamp(user.getBirthday()),
                        user.getPassword(),
                        user.getId());
                updatedUser = findById(user.getId());
            } else if (user.getName() != null && !user.getName().isEmpty()) {
                // try update users by name
                LOG.info("try update users by name " + user.getName());
                jdbcTemplate.update(UPDATE_USER_BY_NAME,
                        user.getName(),
                        user.getEmail(),
                        Convert.toTimestamp(user.getBirthday()),
                        user.getPassword(),
                        user.getName());
                updatedUser = findByName(user.getName());
            }

            if (updatedUser == null) {
                // insert if users not saved yet
                LOG.info("insert user");
                SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("user");
                insert.setGeneratedKeyName("id");
                Map<String, Object> args = new HashMap<>();
                args.put("name", user.getName());
                args.put("email", user.getEmail());
                args.put("birthDay", Convert.toTimestamp(user.getBirthday()));
                args.put("password", user.getPassword());
                user.setId(insert.executeAndReturnKey(args).longValue());
                LOG.info("inserted user: " + user.toString());
            } else {
                user = updatedUser;
            }
            updateRoles(user.getId(), roles);
        }

        return user;
    }

    @Transactional("txManager")
    private void updateRoles(long userId, List<Role> roles) {
        LOG.info("update Role for user " + userId);
        roles.forEach(role -> LOG.info("role: " + role));
        jdbcTemplate.update(DELETE_USER_ROLE, userId);
        for (Role role : roles) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("roles");
            Map<String, Object> args = new HashMap<>();
            args.put("user_id", userId);
            args.put("role_id", getRoleIdByName(role.getName()));
            insert.execute(args);
        }
    }

    private Integer getRoleIdByName(String name) {
        try {
            return jdbcTemplate.queryForObject(SELECT_ROLE_BY_NAME, roleMapper(), name).getId();
        } catch (EmptyResultDataAccessException ignored) {}
        return null;
    }

    @Override
    @Transactional("txManager")
    public void delete(long id) {
        winsRepository.delete(id);
        ticketRepository.deleteBookedTicketByUserId(id);//jdbcTemplate.update(DELETE_TICKETS, id);
        jdbcTemplate.update(DELETE_USER_ROLE, id);
        userAccountRepository.deleteByUserId(id);
        jdbcTemplate.update(DELETE_USER, id);
    }

    @Override
    public User findById(long id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_BY_USER_ID, userMapper(), id);
        } catch (EmptyResultDataAccessException ignored) {
        }
        return null;
    }

    @Override
    public User findByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(SELECT_BY_USER_EMAIL, userMapper(), email);
        } catch (EmptyResultDataAccessException ignored) {
        }
        return null;
    }

    @Override
    public User findByName(String name) {
        try {
            return jdbcTemplate.queryForObject(SELECT_BY_USER_NAME, userMapper(), name);
        } catch (EmptyResultDataAccessException ignored) {
        }
        return null;
    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query(SELECT_ALL, userMapper());
    }


    private List<Role> getRoles(long userId) {
        return jdbcTemplate.query(SELECT_USER_ROLES, roleMapper(), userId);
    }

    private RowMapper<Role> roleMapper() {
        return (rs, rowNum) -> new Role(rs.getInt(1), rs.getString(2));
    }

    private RowMapper<User> userMapper() {
        return (rs, rowNum) -> {
            User user = new User(
                    rs.getLong(1),
                    rs.getString(2),
                    rs.getString(3),
                    ofNullable(rs.getDate(4)).map(Date::toLocalDate).orElse(null)
            );
            user.setPassword(rs.getString(5));
            user.setRoles(getRoles(rs.getLong(1)));
            return user;
        };
    }
}
