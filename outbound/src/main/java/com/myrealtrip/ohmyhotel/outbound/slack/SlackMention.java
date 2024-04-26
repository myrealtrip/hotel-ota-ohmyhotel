package com.myrealtrip.ohmyhotel.outbound.slack;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class SlackMention {

    public static final MentionGroup RESERVATION_GROUP = new MentionGroup(List.of(SlackUser.KANG_MIN_SU));

    @AllArgsConstructor
    public static class MentionGroup {
        private List<SlackUser> users;

        public String mention() {
            return users.stream().map(user -> "<@" + user.getUserId() + ">")
                .collect(Collectors.joining(""));
        }
    }
}
