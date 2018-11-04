package com.axcy.springcinema.repository.impl;

import com.axcy.springcinema.repository.CounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CounterRepositoryImpl implements CounterRepository {

    private static final String UPDATE_COUNTER = "UPDATE counters SET counter=? WHERE name=?";
    private static final String INSERT_COUNTER = "INSERT INTO counters(name, counter) VALUES (?,?)";
    private static final String SELECT_BY_NAME = "SELECT counter FROM counters WHERE name=?";
    private static final String INCREMENT_COUNTER = "UPDATE counters SET counter = counter+1 WHERE name=?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional("txManager")
    public void save(String counterName, int count) {
        if (counterName != null && !counterName.isEmpty()) {
            if (jdbcTemplate.update(UPDATE_COUNTER, count, counterName) == 0) {
                jdbcTemplate.update(INSERT_COUNTER, counterName, count);
            }
        }
    }

    @Override
    public int getByName(String counterName) {
        try {
            return jdbcTemplate.queryForObject(SELECT_BY_NAME, Integer.class, counterName);
        } catch (EmptyResultDataAccessException ignored) {
        }
        return 0;
    }

    @Override
    @Transactional("txManager")
    public void increment(String counterName) {
        jdbcTemplate.update(INCREMENT_COUNTER, counterName);
    }
}
