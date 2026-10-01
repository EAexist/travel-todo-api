package com.matchalab.travel_todo_api.jobs;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public abstract class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.PROCESSING;

    protected Job(){
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
