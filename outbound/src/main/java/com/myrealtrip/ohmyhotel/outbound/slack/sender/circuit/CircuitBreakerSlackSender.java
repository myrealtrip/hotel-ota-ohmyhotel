package com.myrealtrip.ohmyhotel.outbound.slack.sender.circuit;

import com.myrealtrip.slack.client.SlackNotifier;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.myrealtrip.slack.core.Color.GREEN;
import static com.myrealtrip.slack.core.Color.RED;
import static com.myrealtrip.slack.core.utils.SlackMessageUtils.bold;
import static com.myrealtrip.slack.core.utils.SlackMessageUtils.messages;

@Component
public class CircuitBreakerSlackSender {

    private final SlackNotifier slackNotifier;
    private final String activeProfiles;

    /* <써킷브레이커 이름, 상태> */
    private final Map<String, State> stateMap;

    /* 써킷 브레이커 OPEN 이벤트가 여러 인스턴스에서 동시에 발생할 수 있어서 구분하기 위함입니다. */
    private final String serverAddr;

    public CircuitBreakerSlackSender(@Qualifier("commonSlackNotifier") SlackNotifier slackNotifier,
                                     @Value("${spring.profiles.active}") String activeProfile) {
        this.slackNotifier = slackNotifier;
        this.activeProfiles = activeProfile;
        this.stateMap = new ConcurrentHashMap<>();
        String serverAddr;
        try {
            serverAddr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            serverAddr = "확인불가";
        }
        this.serverAddr = serverAddr;
    }

    /**
     * CLOSED -> OPEN, HALF_OPEN -> CLOSED 일 때 알림을 보낸다!
     * HALF_OPEN -> OPEN, OPEN -> HALF_OPEN 일 때는 알림을 보내지 않는다!
     * https://resilience4j.readme.io/docs/circuitbreaker
     */
    public void sendForStateTransition(String circuitBreakerName, CircuitBreaker.State changedSate) {
        stateMap.putIfAbsent(circuitBreakerName, CircuitBreaker.State.CLOSED);

        if (stateMap.get(circuitBreakerName) == CircuitBreaker.State.CLOSED) {
            sendForCircuitOpen(circuitBreakerName);
        } else if (stateMap.get(circuitBreakerName) == CircuitBreaker.State.HALF_OPEN && changedSate == CircuitBreaker.State.CLOSED) {
            sendForCircuitClose(circuitBreakerName);
        }
        stateMap.put(circuitBreakerName, changedSate);
    }

    private void sendForCircuitOpen(String circuitBreakerName) {
        List<String> messages = messages(CircuitBreakerSlackEvent.CIRCUIT_OPEN.getEventName(), activeProfiles);
        messages.add("CircuitBreakerName: " + bold(circuitBreakerName));
        messages.add("Note: 써킷 브레이커가 OPEN 되었습니다. 장애상황인지 확인이 필요합니다.");
        messages.add("Server IP: " + bold(serverAddr));
        slackNotifier.send(messages, RED);
    }

    private void sendForCircuitClose(String circuitBreakerName) {
        List<String> messages = messages(CircuitBreakerSlackEvent.CIRCUIT_CLOSE.getEventName(), activeProfiles);
        messages.add("CircuitBreakerName: " + bold(circuitBreakerName));
        messages.add("Server IP: " + bold(serverAddr));
        slackNotifier.send(messages, GREEN);
    }
}
