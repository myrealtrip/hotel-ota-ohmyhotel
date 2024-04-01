package com.myrealtrip.ohmyhotel.core.domain.reservation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor TODO 필드 추가 후 주석 제거
@SuperBuilder
public class AdditionalOrderInfo {
}
