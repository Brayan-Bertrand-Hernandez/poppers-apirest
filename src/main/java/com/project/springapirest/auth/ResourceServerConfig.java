package com.project.springapirest.auth;

import com.project.springapirest.util.Roles;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    private static final String CLIENTS_HEADER = "/api/clients";
    private static final String PAGINATION_HEADER = "/api/clients/page/**";
    private static final String CLIENT_ID_HEADER = "/api/clients/{id}";
    private static final String CLIENT_DEL_HEADER = "/api/clients";
    private static final String CLIENTS_DEL_HEADER = "/api/clients/**";
    private static final String CLIENTS_UPLOAD_IMG_HEADER = "/api/upload";
    private static final String CLIENTS_SIGN_UP = "/api/clients/sign-up";
    private static final String USERS_SIGN_UP = "/api/sellers";
    private static final String SOCIAL_SIGN_UP = "/api/clients/social";
    private static final String COUNTRIES = "/api/clients/countries";
    private static final String ROLES = "/api/clients/roles";
    private static final String TICKETS = "/api/tickets/**";
    private static final String CLIENT_ROLE = "/api/clients/role";
    private static final String UPLOAD_DOWNLOAD_IMG_HEADER = "/api/uploads/img/**";
    private static final String UPLOAD_DOWNLOAD_TICKET_HEADER = "/api/uploads/ticket/**";
    private static final String UPLOAD_IMAGE_PROFILE = "/api/clients/upload";
    private static final String UPLOAD_TICKET_IMG = "/api/clients/upload/ticket";
    private static final String UPLOADED_DIRECTORY = "src/main/resources/static/image/**";

    private static final String TEST = "/api/clients/enabled/{email}";
    private static final String TEST_1 = "/api/clients/email/{email}";

    private static final String RECOVER_ACCOUNT = "/api/clients/recoverAccount";

    private static final String GOOGLE_TOKEN_LOG = "/oauth/google";
    private static final String FACEBOOK_TOKEN_LOG = "/oauth/facebook";

    private static final List<String> ALLOWED_HOST = Arrays.asList("http://localhost:4200", "https://poppers-app-angular.web.app", "https://poppers-app-angular.web.app/", "*", "**");
    private static final List<String> ALLOWED_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");
    private static final List<String> ALLOWED_HEADERS = Arrays.asList("Content-Type", "Authorization");

    private static final String ALLOWED_PAGE = "/**";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, TEST_1, TEST, CLIENT_ID_HEADER, ROLES, COUNTRIES, UPLOAD_DOWNLOAD_IMG_HEADER, UPLOAD_DOWNLOAD_TICKET_HEADER,   UPLOADED_DIRECTORY).permitAll().
                antMatchers(HttpMethod.POST, UPLOAD_IMAGE_PROFILE, UPLOAD_TICKET_IMG, RECOVER_ACCOUNT, CLIENT_ROLE, SOCIAL_SIGN_UP, GOOGLE_TOKEN_LOG, FACEBOOK_TOKEN_LOG, CLIENTS_SIGN_UP, USERS_SIGN_UP).permitAll().
                antMatchers(TICKETS).permitAll().
                anyRequest().authenticated().and().cors().configurationSource(corsConfigurationSource());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(ALLOWED_HOST);
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setAllowCredentials(true);
        config.setAllowedHeaders(ALLOWED_HEADERS);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(ALLOWED_PAGE, config);

        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfigurationSource()));

        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
    }
}
