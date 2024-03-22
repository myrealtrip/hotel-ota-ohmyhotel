package com.myrealtrip.ohmyhotel.outbound.agent.ota;

import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.ohmyhotel.utils.Sha256Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Service
@Slf4j
public class OmhAgentSupport {

    private final String apiKey;
    private final String secret;

    public OmhAgentSupport(@Value("${spring.config.activate.on-profile}") String profile, OmhAgentProperties properties) {
        this.apiKey = properties.getDecryptedApiKey(profile);
        this.secret = properties.getDecryptedSecret(profile);
    }

    public <T extends OmhCommonResponse> T checkFail(T res, String uri) {
        if (!res.isSuccess()) {
            throw new OmhApiException(uri, res);
        }
        return res;
    }

    public void setAuthHeader(HttpHeaders httpHeaders) {
        httpHeaders.set("Authorization", apiKey);
        httpHeaders.set("Signature", getSignature());
    }

    private String getSignature() {
        long timestamp = Instant.now().getEpochSecond();
        try {
            return Sha256Utils.encrypt(apiKey + secret + timestamp);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            log.info("auth info: {} {} {}", apiKey, secret, timestamp);
            throw new IllegalStateException("falied to create ohmyhotel auth signature", noSuchAlgorithmException);
        }
    }
}
