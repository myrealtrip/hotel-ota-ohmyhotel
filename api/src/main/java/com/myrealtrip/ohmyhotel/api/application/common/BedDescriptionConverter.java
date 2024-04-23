package com.myrealtrip.ohmyhotel.api.application.common;

import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhBedGroup;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BedDescriptionConverter {

    public String toUnionStayBedDescription(List<OmhBedGroup> omhBedGroups) {
        return omhBedGroups.stream()
            .map(this::toBedGroupDescription)
            .collect(Collectors.joining("또는 "));
    }

    private String toBedGroupDescription(OmhBedGroup omhBedGroup) {
        return omhBedGroup.getBeds().stream()
            .map(bed -> bed.getBedTypeCode().getExposedName() + " " + bed.getBedTypeCount() + "개")
            .collect(Collectors.joining(","));
    }
}
