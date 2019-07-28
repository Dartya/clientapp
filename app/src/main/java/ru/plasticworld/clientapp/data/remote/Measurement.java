package ru.plasticworld.clientapp.data.remote;

import java.time.Instant;

public class Measurement {

    private Long pacientParamId;

    private int createdAt;

    private Double value;

    public Measurement(Long pacientParamId, int createdAt, Double value) {
        this.pacientParamId = pacientParamId;
        this.createdAt = createdAt;
        this.value = value;
    }

    public Long getPacientParamId() {
        return pacientParamId;
    }

    public void setPacientParamId(Long pacientParamId) {
        this.pacientParamId = pacientParamId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
