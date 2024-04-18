package com.myrealtrip.ohmyhotel.outbound.agent.mrt.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myrealtrip.common.values.StatusResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResource<T> {
    private T data;
    private Map<String, Object> meta;
    private StatusResult result;

    @JsonIgnore
    public boolean isOk() {
        return this.result.getStatus() == HttpStatus.OK.value();
    }
}
