package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.BedTypeCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmhBedGroup {

    private Integer bedGroupNo;

    private List<OmhBed> beds;

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhBed {

        private Integer bedNo;

        private String bedTypeCode;

        private String bedTypeName;

        private String bedTypeNameByLanguage;

        private Integer bedTypeCount;

        private Double bedTypeSize;

        @JsonIgnore
        public BedTypeCode getBedTypeCodeEnum() {
            return EnumUtils.getEnum(BedTypeCode.class, this.bedTypeCode, BedTypeCode.NONE);
        }
    }
}
