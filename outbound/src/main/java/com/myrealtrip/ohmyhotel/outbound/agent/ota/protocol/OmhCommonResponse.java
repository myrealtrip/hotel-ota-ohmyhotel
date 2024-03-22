package com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
    private String status;
    private String errorCode;
    private String errorMessage;

    @JsonIgnore
    public boolean isFail() {
        return nonNull(succeedYn) && !succeedYn;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return !isFail();
    }
}
