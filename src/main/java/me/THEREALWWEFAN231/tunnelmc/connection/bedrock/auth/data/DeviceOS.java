package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.auth.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DeviceOS {
    UNKNOWN,
    ANDROID,
    APPLE_IOS,
    APPLE_OSX,
    KINDLE_FIRE,
    GEAR_VR,
    MICROSOFT_HOLOLENS,
    MICROSOFT_WINDOWS_10,
    MICROSOFT_WINDOWS_32,
    DEDICATED, // ???
    APPLE_TVOS,
    PLAYSTATION,
    NINTENDO_SWITCH,
    MICROSOFT_XBOX,
    MICROSOFT_WINDOWS_PHONE;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
