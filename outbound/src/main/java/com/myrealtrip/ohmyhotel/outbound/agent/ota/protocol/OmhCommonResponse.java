package com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmhCommonResponse {

    private Boolean succeedYn;

    private String transactionSetId;

    private String serviceCode;

    private String serviceName;

    private String serviceTime;

//    private String status; OmhPreCheckResponse.status 와 겹쳐 주석처리

    private String errorCode;

    private String errorMessage;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhError {

        private String errorCode;

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
