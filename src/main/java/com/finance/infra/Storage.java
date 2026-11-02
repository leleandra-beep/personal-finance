package com.finance.infra;

import com.finance.core.model.User;

import java.util.Map;

public interface Storage {
    void save(Map<String, User> users);
    Map<String, User> load();
}