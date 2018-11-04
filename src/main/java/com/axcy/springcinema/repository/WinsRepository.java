package com.axcy.springcinema.repository;

import com.axcy.springcinema.entity.Win;

import java.util.List;

public interface WinsRepository {
    Win save(Win win);
    List<Win> getAll();
    List<Win> getByUserId(long userId);
    void delete(long userId);
}
