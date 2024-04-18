package com.myrealtrip.ohmyhotel.outbound.agent.mrt.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentError {
    private String name;
    private List<String> traces = Collections.emptyList();
}
