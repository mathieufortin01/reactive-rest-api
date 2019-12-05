package io.mfn.model;

import java.time.Instant;

public class Alarm {
    private boolean triggered;
    private String name;
    private Instant toggledAt;

    public Alarm() {

    }

    public Alarm(String name) {
        this.name = name;
        this.triggered = false;
        this.toggledAt = Instant.now();
    }

    public void toggle() {
        this.triggered = !this.triggered;
        this.toggledAt = Instant.now();
    }

    public String getName() {
        return this.name;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public Instant getToggledAt() {
        return this.toggledAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] name: %s, triggered: %s, toggledAt: %s", getClass().getName(), this.name,
                this.triggered, this.toggledAt);
    }

}