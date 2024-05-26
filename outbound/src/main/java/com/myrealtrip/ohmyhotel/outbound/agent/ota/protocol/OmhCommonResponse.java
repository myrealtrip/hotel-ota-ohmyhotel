package com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OmhCommonResponse {

    @JsonInclude(Include.NON_NULL)
    private Boolean succeedYn;

    @JsonInclude(Include.NON_NULL)
    private String transactionSetId;

    @JsonInclude(Include.NON_NULL)
    private String serviceCode;

    @JsonInclude(Include.NON_NULL)
    private String serviceName;

    @JsonInclude(Include.NON_NULL)
    private String serviceTime;

//    private String status; OmhPreCheckResponse.status 와 겹쳐 주석처리

    @JsonInclude(Include.NON_NULL)
    private String errorCode;

    @JsonInclude(Include.NON_NULL)
    private String errorMessage;

    @JsonInclude(Include.NON_NULL)
    private List<OmhError> errors;

    @JsonIgnore
    public List<String> getAllErrorMessages() {
        List<String> errorMessages = new ArrayList<>();
        if (nonNull(this.errorMessage)) {
            errorMessages.add(this.errorMessage);
        }
        if (CollectionUtils.isEmpty(this.errors)) {
            return errorMessages;
        }
        for (OmhError error : this.errors) {
            if (nonNull(error.getErrorMessage())) {
                errorMessages.add(error.getErrorMessage());
            }
        }
        return errorMessages;
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OmhError {

        @JsonInclude(Include.NON_NULL)
        private String errorCode;

        @JsonInclude(Include.NON_NULL)
        private String errorMessage;
    }

    @JsonIgnore
    public boolean isFail() {
        return (nonNull(succeedYn) && !succeedYn) ||
               CollectionUtils.isNotEmpty(errors);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return !isFail();
    }
}
