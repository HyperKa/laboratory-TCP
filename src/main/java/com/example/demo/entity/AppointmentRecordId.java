package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRecordId implements Serializable {

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentRecordId that = (AppointmentRecordId) o;
        return clientId == that.clientId && recordId == that.recordId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, recordId);
    }
}
