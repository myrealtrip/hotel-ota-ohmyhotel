package com.myrealtrip.ohmyhotel.core.domain.reservation.entity;

import com.myrealtrip.ohmyhotel.core.domain.BaseEntity;
import com.myrealtrip.ohmyhotel.core.domain.reservation.converter.JpaAdditionalOrderInfoConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.converter.JpaGuestCountConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.converter.JpaGuestDetailConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestDetail;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reservation")
public class ReservationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "mrt_reservation_no")
    private String mrtReservationNo;

    @Column(name = "omh_book_code")
    private String omhBookCode;

    @Column(name = "hotel_confirm_no")
    private String hotelConfirmNo;

    @Column(name = "omh_book_status")
    @Enumerated(value = EnumType.STRING)
    private OmhBookingStatus omhBookStatus;

    @Column(name = "reservation_status")
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus reservationStatus;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "hotel_name")
    private String hotelName;

    @Column(name = "room_type_code")
    private String roomTypeCode;

    @Column(name = "room_type_name")
    private String roomTypeName;

    @Column(name = "rate_plan_code")
    private String ratePlanCode;

    @Column(name = "rate_plan_name")
    private String ratePlanName;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "deposit_price")
    private BigDecimal depositPrice;

    @Column(name = "zero_margin_apply")
    private Boolean zeroMarginApply;

    @Column(name = "zero_margin_apply_price")
    private BigDecimal zeroMarginApplyPrice;

    @Column(name = "mrt_commission_rate")
    private BigDecimal mrtCommissionRate;

    @Column(name = "guest_count")
    @Convert(converter = JpaGuestCountConverter.class)
    private GuestCount guestCount;

    @Column(name = "additional_info")
    @Convert(converter = JpaAdditionalOrderInfoConverter.class)
    private AdditionalOrderInfo additionalInfo;

    @Column(name = "reservation_user")
    @Convert(converter = JpaGuestDetailConverter.class)
    private GuestDetail reservationUser;

    @Column(name = "check_in_user")
    @Convert(converter = JpaGuestDetailConverter.class)
    private GuestDetail checkInUser;

    @Column(name = "specialRequest")
    private String specialRequest;

    @Column(name = "cancel_penalty_amount")
    private BigDecimal cancelPenaltyAmount;

    @Column(name = "booking_error_code")
    private String bookingErrorCode;

    @Column(name = "logs")
    private String logs;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "canceled_by")
    private String canceledBy;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "cancel_reason_type")
    private String cancelReasonType;
}
