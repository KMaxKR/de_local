package ks.mx.de_local.entity.Provider;

import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


public enum UserProvider {
    LOCAL("LOCAL"),
    OAUTH2("OAUTH2");

    @Getter
    public final String getUserProvider;

    @Generated
    UserProvider(final String getUserProvider){
        this.getUserProvider = getUserProvider;
    };
}
