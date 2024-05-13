package com.myrealtrip.ohmyhotel.core.domain.reservation.entity;

import com.myrealtrip.ohmyhotel.core.domain.BaseEntity;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStepApi;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reservation_api_log")
public class ReservationApiLogEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long reservationApiLogId;

    @Column(name = "log_key")
    private String logKey;

    @Column(name = "api")
    @Enumerated(value = EnumType.STRING)
    private ReservationStepApi api;

    @Column(name = "url")
    private String url;

    @Column(name = "log_type")
    @Enumerated(value = EnumType.STRING)
    private ApiLogType logType;

    @Column(name = "log")
    private String log;
}
