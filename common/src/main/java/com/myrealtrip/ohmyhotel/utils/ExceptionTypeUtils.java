package com.myrealtrip.ohmyhotel.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionTypeUtils {

    /**
     * 해당 예외의 모든 throwable 을 검사하여 일치하는 Exception type 이 있다면 true 리턴
     * @param throwable 검사할 예외
     * @param clazz 해당 클래스타입과 일치하는지 검사한다.
     * @return
     */
    public static boolean hasType(Throwable throwable, Class<? extends Throwable> clazz) {
        return ExceptionUtils.indexOfType(throwable, clazz) != -1;
    }
}
