package com.myrealtrip.ohmyhotel.consumer.reservation;

import com.myrealtrip.ohmyhotel.consumer.reservation.service.CancelConsumeService;
import com.myrealtrip.ohmyhotel.consumer.reservation.service.ReserveConfirmConsumeService;
import com.myrealtrip.ohmyhotel.core.service.reservation.BookingMessageKafkaSendService;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackEvent;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackSender;
import com.myrealtrip.srtcommon.exceptions.JacksonProcessingException;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.common.constant.ProviderType;
import com.myrealtrip.unionstay.common.message.booking.BookingOrderMessage;
import com.myrealtrip.unionstay.common.message.booking.dlt.BookingDetailDeadLetterMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.myrealtrip.ohmyhotel.consumer.ConsumerConstants.GROUP_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingConsumer {

    private final ReserveConfirmConsumeService reserveConfirmConsumeService;
    private final BookingMessageKafkaSendService bookingMessageKafkaSendService;
    private final ReservationSlackSender reservationSlackSender;
    private final CancelConsumeService cancelConsumeService;

    @KafkaListener(topics = "#{'${myrealtrip.kafka.common.topics.unionstay-booking-order}'}", groupId = GROUP_ID, containerFactory = "manualAckKafkaListenerContainerFactory")
    public void listenBookingOrder(@Payload String payload, Acknowledgment acknowledgment) {
        BookingOrderMessage message = null;
        try {
            message = ObjectMapperUtils.readString(payload, BookingOrderMessage.class);
            if (isOhMyHotelEvent(message.getProviderType(), message.getProviderCode())) {
                consume(message);
            }
        } catch (JacksonProcessingException exception) {
            log.error("BookingOrderMessage 파싱 실패", exception);
        } catch (Throwable t) {
            log.error("{} - booking order message consume fail", message.getMrtReservationNo(), t);
            reservationSlackSender.sendToSrtWithMention(ReservationSlackEvent.BOOKING_ORDER_CONSUME_FAIL, message.getMrtReservationNo(), payload);
        } finally {
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "#{'${myrealtrip.kafka.common.topics.unionstay-booking-detail-upsert-dlt}'}", groupId = GROUP_ID, containerFactory = "manualAckKafkaListenerContainerFactory")
    public void listenBookingDetailUpsertDeadLetter(@Payload String payload, Acknowledgment acknowledgment) {
        try {
            BookingDetailDeadLetterMessage message = ObjectMapperUtils.readString(payload, BookingDetailDeadLetterMessage.class);
            if (!isOhMyHotelEvent(message.getProviderType(), message.getProviderCode())) {
                return;
            }
            log.info("BookingDetailDeadLetterMessage 메세지 : {}", message.toString());
            bookingMessageKafkaSendService.sendByMrtReservationNo(message.getMrtReservationNo(), null, message.getRetryCount() + 1);
        } catch (Exception e) {
            log.error("예약 데드레터 처리 실패", e);
        } finally {
            acknowledgment.acknowledge();
        }
    }

    private boolean isOhMyHotelEvent(ProviderType providerType, ProviderCode providerCode) {
        return providerType == ProviderType.GDS &&
               providerCode == ProviderCode.OH_MY_HOTEL;
    }

    public void consume(BookingOrderMessage message) {
        switch (message.getBookingStatus()) {
            case CONFIRM_ACCEPTED: // 예약 생성 이벤트
                reserveConfirmConsumeService.consume(message);
                bookingMessageKafkaSendService.sendByMrtReservationNo(message.getMrtReservationNo(), List.of(ReservationStatus.RESERVE_CONFIRM, ReservationStatus.RESERVE_CONFIRM_FAIL), 0);
                return;
            case ALL_CANCEL_ACCEPTED: // 예약 취소 이벤트\
                cancelConsumeService.consume(message);
                bookingMessageKafkaSendService.sendByMrtReservationNo(message.getMrtReservationNo(), List.of(ReservationStatus.CANCEL_SUCCESS, ReservationStatus.CANCEL_FAIL), 0);
                return;
            default:
                log.warn("이곳에서 처리할 수 있는 메시지가 아닙니다. 메시지 수신 추가 구현이 필요합니다.");
        }
    }
}
