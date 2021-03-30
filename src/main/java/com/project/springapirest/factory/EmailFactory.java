package com.project.springapirest.factory;

public interface EmailFactory {
    void sendMail(String to, String subject, String body);
}
