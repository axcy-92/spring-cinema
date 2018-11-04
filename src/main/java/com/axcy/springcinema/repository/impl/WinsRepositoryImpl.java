package com.axcy.springcinema.repository.impl;

import com.axcy.springcinema.entity.Win;
import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.repository.WinsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Repository
public class WinsRepositoryImpl implements WinsRepository {

    private static final String SELECT_ALL = "select w.*, u.* from wins w, user u WHERE u.id=w.user_id";
    private static final String SELECT_BY_USER_ID = "select * from wins w, user u WHERE w.user_id=u.id and u.id=?";
    private static final String DELETE_WINS = "DELETE from wins WHERE user_id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional("txManager")
    public Win save(Win win) {
        if (win != null && win.getUser() != null) {
            User user = win.getUser();
            if (user.getId() != null) {
                SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("wins");
                insert.setGeneratedKeyName("id");
                Map<String, Object> args = new HashMap<>();
                args.put("user_id", user.getId());
                args.put("date", win.getDate());
                win.setId(insert.executeAndReturnKey(args).longValue());
            }
        }
        return win;
    }

    @Override
    public List<Win> getAll() {
        return jdbcTemplate.query(SELECT_ALL, winMapper());
    }


    @Override
    public List<Win> getByUserId(long userId) {
        return jdbcTemplate.query(SELECT_BY_USER_ID, winMapper(), userId);
    }

    @Override
    @Transactional("txManager")
    public void delete(long userId) {
        jdbcTemplate.update(DELETE_WINS, userId);
    }

    private RowMapper<Win> winMapper() {
        return (rs, rowNum) -> {
            Win win = new Win();
            win.setId(rs.getLong(1));
            win.setDate(rs.getDate(3));
            win.setUser(
                    new User(
                            rs.getLong(4),
                            rs.getString(5),
                            rs.getString(6),
                            ofNullable(rs.getDate(7)).map(Date::toLocalDate).orElse(null)));
            return win;
        };
    }
}
