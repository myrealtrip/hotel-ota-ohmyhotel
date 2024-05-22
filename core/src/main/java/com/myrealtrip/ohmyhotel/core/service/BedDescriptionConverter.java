package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.enumarate.BedTypeCode;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhBedGroup;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class BedDescriptionConverter {

    public String toUnionStayBedDescription(List<OmhBedGroup> omhBedGroups) {
        return omhBedGroups.stream()
            .map(this::toBedGroupDescription)
            .collect(Collectors.joining(" 또는 "));
    }

    private String toBedGroupDescription(OmhBedGroup omhBedGroup) {
        return omhBedGroup.getBeds().stream()
            .map(bed -> {
                if (nonNull(bed.getBedTypeCode())) {
                    return bed.getBedTypeCode().getExposedName() + " " + bed.getBedTypeCount() + "개";
                }
                if (nonNull(BedTypeCode.getByDescription(bed.getBedTypeName()))) {
                    return BedTypeCode.getByDescription(bed.getBedTypeName()).getExposedName() + " " + bed.getBedTypeCount() + "개";
                }
                return bed.getBedTypeName() + " " + bed.getBedTypeCount() + "개";
            })
            .collect(Collectors.joining(","));
    }
}
