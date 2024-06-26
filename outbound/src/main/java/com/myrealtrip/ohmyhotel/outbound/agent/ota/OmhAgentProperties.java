package com.myrealtrip.ohmyhotel.outbound.agent.ota;

import com.myrealtrip.cipher.CipherDecryptor;
import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@Setter
@ConfigurationProperties("ohmyhotel.api")
public class OmhAgentProperties {

    private String baseUrl;
    private String apiKey;
    private String secret;

    private AgentProperties healthCheck;
    private AgentProperties staticHotelBulkList;
    private AgentProperties staticHotelInfoList;
    private AgentProperties hotelsAvailability;
    private AgentProperties roomsAvailability;
    private AgentProperties roomInfo;
    private AgentProperties preCheck;
    private AgentProperties createBooking;
    private AgentProperties bookingDetail;
    private AgentProperties cancelBooking;

    public String getDecryptedApiKey(String profile) {
        return CipherDecryptor.decrypt(profile, apiKey);
    }

    public String getDecryptedSecret(String profile) {
        return CipherDecryptor.decrypt(profile, secret);
    }
}
