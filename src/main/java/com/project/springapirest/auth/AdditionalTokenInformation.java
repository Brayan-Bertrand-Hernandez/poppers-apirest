package com.project.springapirest.auth;

import com.project.springapirest.factory.UserFactory;
import com.project.springapirest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AdditionalTokenInformation implements TokenEnhancer {
    private static final String HEADER_INFO = "info_adicional";
    private static final String HEADER_NAME = "Nombre";
    private static final String HEADER_EMAIL = "Email";
    private static final String MESSAGE = "Bienvenido al back feo :,v";

    @Autowired
    private UserFactory userFactory;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> info = new HashMap<>();

        User currentUser = userFactory.findByUsername(oAuth2Authentication.getName());

        info.put(HEADER_INFO, MESSAGE.concat(oAuth2Authentication.getName()));
        info.put(HEADER_NAME, currentUser.getName());
        info.put(HEADER_EMAIL, currentUser.getEmail());

        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);

        return oAuth2AccessToken;
    }
}
