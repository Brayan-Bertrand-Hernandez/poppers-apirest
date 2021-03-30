package com.project.springapirest.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.project.springapirest.model.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "https://poppers-app-angular.web.app", "https://poppers-app-angular.web.app/", "*", "**"})
@RequestMapping("/oauth")
public class OAuthRestController {
    @Value("${google.clientId}")
    String googleClientId;

    @PostMapping("/google")
    public ResponseEntity<?> google(@RequestBody Token token) throws IOException {
        NetHttpTransport netHttpTransport = new NetHttpTransport();
        JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

        GoogleIdTokenVerifier.Builder verifier = new GoogleIdTokenVerifier.Builder(
                netHttpTransport, jacksonFactory).setAudience(Collections.singletonList(googleClientId));

        GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), token.getValue());
        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @PostMapping("/facebook")
    public ResponseEntity<?> facebook(@RequestBody Token token) throws IOException {
        Facebook facebook = new FacebookTemplate(token.getValue());
        String[] fields = {"email", "picture"};
        User user = facebook.fetchObject("me", User.class, fields);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
