package com.myrealtrip.ohmyhotel.outbound.agent.ota;

import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.ohmyhotel.utils.Sha256Utils;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

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

    public <T extends OmhCommonResponse> T checkFail(T res, String apiName) {
        if (!res.isSuccess()) {
            throw new OmhApiException(apiName, res, 200);
        }
        return res;
    }

    public Mono<OmhApiException> getOmhApiExceptionMono(String apiName, ClientResponse res) {
        return res.bodyToMono(OmhCommonResponse.class)
            .map(omhCommonResponse -> new OmhApiException(apiName, omhCommonResponse, res.rawStatusCode()));
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
