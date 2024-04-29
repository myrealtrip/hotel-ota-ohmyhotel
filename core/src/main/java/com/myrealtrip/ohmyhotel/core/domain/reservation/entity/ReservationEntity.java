package com.myrealtrip.ohmyhotel.core.domain.reservation.entity;

import com.myrealtrip.ohmyhotel.core.domain.BaseEntity;
import com.myrealtrip.ohmyhotel.core.domain.reservation.converter.JpaAdditionalOrderInfoConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.converter.JpaGuestCountConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.converter.JpaGuestDetailConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestDetail;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.OrderFormInfo;
import com.myrealtrip.ohmyhotel.enumarate.CanceledBy;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

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

    private static final String LOG_SEPARATOR = ":-:";

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

    @Column(name = "cancel_penalty_sale_price")
    private BigDecimal cancelPenaltySalePrice;

    @Column(name = "cancel_penalty_deposit_price")
    private BigDecimal cancelPenaltyDepositPrice;

    @Column(name = "booking_error_code")
    private String bookingErrorCode;

    @Column(name = "logs")
    private String logs;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "canceled_by")
    @Enumerated(value = EnumType.STRING)
    private CanceledBy canceledBy;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "cancel_reason_type")
    private String cancelReasonType;

    public void confirmFail(String bookingErrorCode) {
        changeStatus(ReservationStatus.RESERVE_CONFIRM_FAIL);
        this.bookingErrorCode = bookingErrorCode;
        appendLog("예약확정 실패");
    }

    public void confirm(String omhBookCode, String hotelConfirmNo) {
        changeStatus(ReservationStatus.RESERVE_CONFIRM);
        appendLog("예약확정 성공");
        this.confirmedAt = LocalDateTime.now();
        this.omhBookCode = omhBookCode;
        this.hotelConfirmNo = hotelConfirmNo;
    }

    public void confirmPending(String omhBookCode, String hotelConfirmNo) {
        changeStatus(ReservationStatus.RESERVE_CONFIRM_PENDING);
        appendLog("예약 확정 보류");
        this.omhBookCode = omhBookCode;
        this.hotelConfirmNo = hotelConfirmNo;
    }

    public void updateOrderFormInfo(OrderFormInfo orderFormInfo) {
        this.reservationUser = orderFormInfo.getReservationUser();
        this.checkInUser = orderFormInfo.getCheckInUser();
        this.specialRequest = orderFormInfo.getSpecialRequest();
    }

    public void changeStatus(ReservationStatus reservationStatus) {
        if (!this.reservationStatus.canChangeTo(reservationStatus)) {
            throw new IllegalStateException(String.format("상태전이가 불가능합니다. before: %s, after: %s", this.reservationStatus, reservationStatus));
        }
        this.reservationStatus = reservationStatus;
    }

    public void appendLog(String appendLog) {
        LocalDateTime now = LocalDateTime.now();
        if (StringUtils.isBlank(this.logs)) {
            this.logs = now + " " + appendLog;
            return;
        }
        this.logs = this.logs + LOG_SEPARATOR + now + " " + appendLog;
    }
}
