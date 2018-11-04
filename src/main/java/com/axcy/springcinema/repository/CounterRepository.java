package com.axcy.springcinema.repository;

public interface CounterRepository {
    void save(String counterName, int count);

    int getByName(String counterName);

    void increment(String counterName);
}
