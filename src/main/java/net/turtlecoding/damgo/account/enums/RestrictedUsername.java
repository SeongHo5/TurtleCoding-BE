package net.turtlecoding.damgo.account.enums;

import lombok.Getter;

@Getter
public enum RestrictedUsername {
    ADMIN("관리"),
    ROOT("루트"),
    ADMINISTRATOR("어드민"),
    OPERATOR("운영"),
    MANAGER("매니저"),
    MASTER("마스터"),
    TEST("테스트"),
    TESTER("테스터"),
    GUEST("게스트");

    private final String name;

    RestrictedUsername(String name) {
        this.name = name;
    }
}
