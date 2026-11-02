package com.finance.infra;

import com.finance.core.model.User;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStorage implements Storage {
    private Map<String, User> cache = new HashMap<>();

    @Override
    public void save(Map<String, User> users) { this.cache = new HashMap<>(users); }

    @Override
    public Map<String, User> load() { return new HashMap<>(cache); }
}
