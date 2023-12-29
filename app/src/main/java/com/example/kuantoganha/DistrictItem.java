package com.example.kuantoganha;

import androidx.annotation.NonNull;

public class DistrictItem {
    private final String id;
    private final String name;

    public DistrictItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
