package com.matchalab.travel_todo_api.model.Reservation;

import java.util.UUID;

import javax.validation.constraints.Size;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.mapper.ReservationDetailMapper;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.model.Todo.Todo;

import io.micrometer.common.lang.NonNull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class Reservation implements Persistable<UUID> {

    @Enumerated(EnumType.STRING)
    ReservationCategory category;

    @Id
    @NonNull
    @Builder.Default
    private UUID id = UUID.randomUUID();
    @Builder.Default
    private Boolean isCompleted = false;
    @Basic(fetch = FetchType.LAZY)
    private String rawText;

    @Nullable
    @Column(length = 2048)
    @Size(max = 2048, message = "primaryHrefLink cannot exceed 2048 characters.")
    private String primaryHrefLink;

    @Nullable
    private String code;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detail", columnDefinition = "jsonb")
    private String detailJson;

    private String note;

    @Transient
    private ReservationDetail detail;

    @PostLoad
    private void deserializeDetail() {
        if (this.detailJson != null && this.category != null) {
            this.detail = ReservationDetailMapper.fromJson(this.detailJson, this.category);
        }
    }

    @PrePersist
    @PreUpdate
    private void serializeDetail() {
        if (this.detail != null) {
            this.detailJson = ReservationDetailMapper.toJson(this.detail);
        }
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Nullable
    private Todo todo;

    // @Nullable
    // private String serverFileUri;

    // @Nullable
    // private String localAppStorageFileUri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    public Reservation(Reservation reservation) {
        this.id = UUID.randomUUID();
        this.isCompleted = reservation.getIsCompleted();
        this.category = reservation.getCategory();
        // this.rawText = reservation.getRawText();
        this.primaryHrefLink = reservation.getPrimaryHrefLink();
        this.detail = reservation.detail;
        // this.serverFileUri = reservation.getServerFileUri();
        // this.localAppStorageFileUri = reservation.getLocalAppStorageFileUri();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}
