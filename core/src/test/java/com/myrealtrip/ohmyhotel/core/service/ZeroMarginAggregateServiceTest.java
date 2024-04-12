package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.CustomZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.GlobalZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.enumarate.SwitchValue;
import com.myrealtrip.ohmyhotel.enumarate.ZeroMarginType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ZeroMarginAggregateServiceTest {

    private ZeroMarginAggregateService zeroMarginAggregateService = new ZeroMarginAggregateService();

    @DisplayName("글로벌 스위치 참고하여 aggregate 테스트")
    @Nested
    public class withGlobalSwitch {

        @Test
        @DisplayName("글로벌 스위치 on && 커스텀 설정 없음")
        public void test_1() {
            // given
            GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.ON, BigDecimal.valueOf(1));
            CustomZeroMargin customZeroMargin = null;

            // when
            ZeroMargin result = zeroMarginAggregateService.aggregateWithGlobalSwitch(globalZeroMargin, customZeroMargin);

            // then
            assertThat(result.isOn()).isTrue();
            assertThat(result.getZeroMarginRate()).isEqualTo(globalZeroMargin.getZeroMarginRate());
            assertThat(result.getType()).isEqualTo(ZeroMarginType.GLOBAL);
        }

        @Test
        @DisplayName("글로벌 스위치 on && 커스텀 설정 MANUAL")
        public void test_3() {
            // given
            Long propertyId = 1L;

            GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.ON, BigDecimal.valueOf(1));
            CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.MANUAL, BigDecimal.valueOf(2));

            // when
            ZeroMargin result = zeroMarginAggregateService.aggregateWithGlobalSwitch(globalZeroMargin, customZeroMargin);

            // then
            assertThat(result.isOn()).isTrue();
            assertThat(result.getZeroMarginRate()).isEqualTo(customZeroMargin.getZeroMarginRate());
            assertThat(result.getType()).isEqualTo(ZeroMarginType.MANUAL);
        }

        @Test
        @DisplayName("글로벌 스위치 on && 커스텀 설정 GLOBAL")
        public void test_5() {
            // given
            Long propertyId = 1L;

            GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.ON, BigDecimal.valueOf(1));
            CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.GLOBAL, null);

            // when
            ZeroMargin result = zeroMarginAggregateService.aggregateWithGlobalSwitch(globalZeroMargin, customZeroMargin);

            // then
            assertThat(result.isOn()).isTrue();
            assertThat(result.getZeroMarginRate()).isEqualTo(globalZeroMargin.getZeroMarginRate());
            assertThat(result.getType()).isEqualTo(ZeroMarginType.GLOBAL);
        }

        @Test
        @DisplayName("글로벌 스위치 on && 커스텀 설정 NOT_APPLY")
        public void test_7() {
            // given
            Long propertyId = 1L;

            GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.ON, BigDecimal.valueOf(1));
            CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.NOT_APPLY, null);

            // when
            ZeroMargin result = zeroMarginAggregateService.aggregateWithGlobalSwitch(globalZeroMargin, customZeroMargin);

            // then
            assertThat(result.isOn()).isFalse();
            assertThat(result.getZeroMarginRate()).isNull();
            assertThat(result.getType()).isEqualTo(ZeroMarginType.NOT_APPLY);
        }

        @Test
        @DisplayName("글로벌 스위치 off && 커스텀 설정 없음")
        public void test_9() {
            // given
            GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.OFF, BigDecimal.valueOf(1));
            CustomZeroMargin customZeroMargin = null;

            // when
            ZeroMargin result = zeroMarginAggregateService.aggregateWithGlobalSwitch(globalZeroMargin, customZeroMargin);

            // then
            assertThat(result.isOn()).isFalse();
            assertThat(result.getZeroMarginRate()).isNull();
            assertThat(result.getType()).isEqualTo(ZeroMarginType.NOT_APPLY);
        }

        @Test
        @DisplayName("글로벌 스위치 off && 커스텀 설정 MANUAL")
        public void test_11() {
            // given
            Long propertyId = 1L;

            GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.OFF, BigDecimal.valueOf(1));
            CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.MANUAL, BigDecimal.valueOf(2));
            boolean discountable = true;

            // when
            ZeroMargin result = zeroMarginAggregateService.aggregateWithGlobalSwitch(globalZeroMargin, customZeroMargin);

            // then
            assertThat(result.isOn()).isFalse();
            assertThat(result.getZeroMarginRate()).isNull();
            assertThat(result.getType()).isEqualTo(ZeroMarginType.NOT_APPLY);
        }

        @Test
        @DisplayName("글로벌 스위치 off && 커스텀 설정 GLOBAL")
        public void test_13() {
            // given
            Long propertyId = 1L;

            GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.OFF, BigDecimal.valueOf(1));
            CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.GLOBAL, null);

            // when
            ZeroMargin result = zeroMarginAggregateService.aggregateWithGlobalSwitch(globalZeroMargin, customZeroMargin);

            // then
            assertThat(result.isOn()).isFalse();
            assertThat(result.getZeroMarginRate()).isNull();
            assertThat(result.getType()).isEqualTo(ZeroMarginType.NOT_APPLY);
        }

        @Test
        @DisplayName("글로벌 스위치 off && 커스텀 설정 NOT_APPLY")
        public void test_15() {
            // given
            Long propertyId = 1L;

            GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.OFF, BigDecimal.valueOf(1));
            CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.NOT_APPLY, null);

            // when
            ZeroMargin result = zeroMarginAggregateService.aggregateWithGlobalSwitch(globalZeroMargin, customZeroMargin);

            // then
            assertThat(result.isOn()).isFalse();
            assertThat(result.getZeroMarginRate()).isNull();
            assertThat(result.getType()).isEqualTo(ZeroMarginType.NOT_APPLY);
        }

        @DisplayName("글로벌 스위치 참고하지 않고 aggregate 테스트")
        @Nested
        public class withoutGlobalSwitch {

            @Test
            @DisplayName("글로벌 스위치 on && 커스텀 설정 없음")
            public void test_1() {
                // given
                GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.ON, BigDecimal.valueOf(1));
                CustomZeroMargin customZeroMargin = null;

                // when
                ZeroMargin result = zeroMarginAggregateService.aggregateWithoutGlobalSwitch(globalZeroMargin, customZeroMargin);

                // then
                assertThat(result.isOn()).isTrue();
                assertThat(result.getZeroMarginRate()).isEqualTo(globalZeroMargin.getZeroMarginRate());
                assertThat(result.getType()).isEqualTo(ZeroMarginType.GLOBAL);
            }

            @Test
            @DisplayName("글로벌 스위치 on && 커스텀 설정 MANUAL")
            public void test_3() {
                // given
                Long propertyId = 1L;

                GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.ON, BigDecimal.valueOf(1));
                CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.MANUAL, BigDecimal.valueOf(2));

                // when
                ZeroMargin result = zeroMarginAggregateService.aggregateWithoutGlobalSwitch(globalZeroMargin, customZeroMargin);

                // then
                assertThat(result.isOn()).isTrue();
                assertThat(result.getZeroMarginRate()).isEqualTo(customZeroMargin.getZeroMarginRate());
                assertThat(result.getType()).isEqualTo(ZeroMarginType.MANUAL);
            }

            @Test
            @DisplayName("글로벌 스위치 on && 커스텀 설정 GLOBAL")
            public void test_5() {
                // given
                Long propertyId = 1L;

                GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.ON, BigDecimal.valueOf(1));
                CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.GLOBAL, null);
                boolean discountable = true;

                // when
                ZeroMargin result = zeroMarginAggregateService.aggregateWithoutGlobalSwitch(globalZeroMargin, customZeroMargin);

                // then
                assertThat(result.isOn()).isTrue();
                assertThat(result.getZeroMarginRate()).isEqualTo(globalZeroMargin.getZeroMarginRate());
                assertThat(result.getType()).isEqualTo(ZeroMarginType.GLOBAL);
            }

            @Test
            @DisplayName("글로벌 스위치 on && 커스텀 설정 NOT_APPLY")
            public void test_7() {
                // given
                Long propertyId = 1L;

                GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.ON, BigDecimal.valueOf(1));
                CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.NOT_APPLY, null);

                // when
                ZeroMargin result = zeroMarginAggregateService.aggregateWithoutGlobalSwitch(globalZeroMargin, customZeroMargin);

                // then
                assertThat(result.isOn()).isFalse();
                assertThat(result.getZeroMarginRate()).isNull();
                assertThat(result.getType()).isEqualTo(ZeroMarginType.NOT_APPLY);
            }

            @Test
            @DisplayName("글로벌 스위치 off && 커스텀 설정 없음")
            public void test_9() {
                // given
                Long propertyId = 1L;

                GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.OFF, BigDecimal.valueOf(1));
                CustomZeroMargin customZeroMargin = null;
                boolean discountable = true;

                // when
                ZeroMargin result = zeroMarginAggregateService.aggregateWithoutGlobalSwitch(globalZeroMargin, customZeroMargin);

                // then
                assertThat(result.isOn()).isTrue();
                assertThat(result.getZeroMarginRate()).isEqualTo(globalZeroMargin.getZeroMarginRate());
                assertThat(result.getType()).isEqualTo(ZeroMarginType.GLOBAL);
            }

            @Test
            @DisplayName("글로벌 스위치 off && 커스텀 설정 MANUAL")
            public void test_11() {
                // given
                Long propertyId = 1L;

                GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.OFF, BigDecimal.valueOf(1));
                CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.MANUAL, BigDecimal.valueOf(2));
                boolean discountable = true;

                // when
                ZeroMargin result = zeroMarginAggregateService.aggregateWithoutGlobalSwitch(globalZeroMargin, customZeroMargin);

                // then
                assertThat(result.isOn()).isTrue();
                assertThat(result.getZeroMarginRate()).isEqualTo(customZeroMargin.getZeroMarginRate());
                assertThat(result.getType()).isEqualTo(ZeroMarginType.MANUAL);
            }

            @Test
            @DisplayName("글로벌 스위치 off && 커스텀 설정 GLOBAL")
            public void test_13() {
                // given
                Long propertyId = 1L;

                GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.OFF, BigDecimal.valueOf(1));
                CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.GLOBAL, null);

                // when
                ZeroMargin result = zeroMarginAggregateService.aggregateWithoutGlobalSwitch(globalZeroMargin, customZeroMargin);

                // then
                assertThat(result.isOn()).isTrue();
                assertThat(result.getZeroMarginRate()).isEqualTo(globalZeroMargin.getZeroMarginRate());
                assertThat(result.getType()).isEqualTo(ZeroMarginType.GLOBAL);
            }

            @Test
            @DisplayName("글로벌 스위치 off && 커스텀 설정 NOT_APPLY && 할인가능")
            public void test_15() {
                // given
                Long propertyId = 1L;

                GlobalZeroMargin globalZeroMargin = globalZeroMargin(SwitchValue.OFF, BigDecimal.valueOf(1));
                CustomZeroMargin customZeroMargin = customZeroMargin(propertyId, ZeroMarginType.NOT_APPLY, null);

                // when
                ZeroMargin result = zeroMarginAggregateService.aggregateWithoutGlobalSwitch(globalZeroMargin, customZeroMargin);

                // then
                assertThat(result.isOn()).isFalse();
                assertThat(result.getZeroMarginRate()).isNull();
                assertThat(result.getType()).isEqualTo(ZeroMarginType.NOT_APPLY);
            }
        }
    }

    private GlobalZeroMargin globalZeroMargin(SwitchValue switchValue, BigDecimal zeroMarginMarkUpRate) {
        return GlobalZeroMargin.builder()
            .switchValue(switchValue)
            .zeroMarginRate(zeroMarginMarkUpRate)
            .build();
    }

    private CustomZeroMargin customZeroMargin(Long hotelId, ZeroMarginType type, BigDecimal zeroMarginMarkUpRate) {
        return CustomZeroMargin.builder()
            .hotelId(hotelId)
            .zeroMarginType(type)
            .zeroMarginRate(zeroMarginMarkUpRate)
            .build();
    }
}