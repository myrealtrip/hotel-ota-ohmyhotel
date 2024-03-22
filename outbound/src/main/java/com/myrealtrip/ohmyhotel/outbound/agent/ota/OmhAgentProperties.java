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
@ConstructorBinding
public class OmhAgentProperties {

    private String baseUrl;
    private String apiKey;
    private String secret;

    private AgentProperties healthCheck;

    public String getDecryptedApiKey(String profile) {
        return CipherDecryptor.decrypt(profile, apiKey);
    }

    public String getDecryptedSecret(String profile) {
        return CipherDecryptor.decrypt(profile, secret);
    }
}
