package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.BedTypeCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BedGroup {

    private Integer bedGroupNo;

    private List<Bed> beds;

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Bed {

        private Integer bedNo;

        private BedTypeCode bedTypeCode;

        private String bedTypeName;

        private String bedTypeNameByLanguage;

        private Integer bedTypeCount;

        private Double bedTypeSize;
    }
}
