package org.springframework.boot.autoconfigure.security.oauth2.client.servlet;

import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2WebSecurityConfigurationTests;
import org.junit.Test;

public class OAuth2WebSecurityConfigurationTestsTest {
    
    OAuth2WebSecurityConfigurationTests oAuth2WebSecurityConfigurationTests = new OAuth2WebSecurityConfigurationTests();

    @Test
    public void testSecurityConfigurerConfiguresOAuth2Login() throws Exception {
        oAuth2WebSecurityConfigurationTests.securityConfigurerConfiguresOAuth2Login();
    }

    @Test
    public void testSecurityConfigurerConfiguresAuthorizationCode() throws Exception {
        oAuth2WebSecurityConfigurationTests.securityConfigurerConfiguresAuthorizationCode();
    }

    @Test
    public void testSecurityConfigurerBacksOffWhenClientRegistrationBeanAbsent() throws Exception {
        oAuth2WebSecurityConfigurationTests.securityConfigurerBacksOffWhenClientRegistrationBeanAbsent();
    }

    @Test
    public void testConfigurationRegistersAuthorizedClientServiceBean() throws Exception {
        oAuth2WebSecurityConfigurationTests.configurationRegistersAuthorizedClientServiceBean();
    }

    @Test
    public void testConfigurationRegistersAuthorizedClientRepositoryBean() throws Exception {
        oAuth2WebSecurityConfigurationTests.configurationRegistersAuthorizedClientRepositoryBean();
    }

    @Test
    public void testSecurityFilterChainConfigBacksOffWhenOtherSecurityFilterChainBeanPresent() throws Exception {
        oAuth2WebSecurityConfigurationTests.securityFilterChainConfigBacksOffWhenOtherSecurityFilterChainBeanPresent();
    }

    @Test
    public void testSecurityFilterChainConfigConditionalOnSecurityFilterChainClass() throws Exception {
        oAuth2WebSecurityConfigurationTests.securityFilterChainConfigConditionalOnSecurityFilterChainClass();
    }

    @Test
    public void testAuthorizedClientServiceBeanIsConditionalOnMissingBean() throws Exception {
        oAuth2WebSecurityConfigurationTests.authorizedClientServiceBeanIsConditionalOnMissingBean();
    }

    @Test
    public void testAuthorizedClientRepositoryBeanIsConditionalOnMissingBean() throws Exception {
        oAuth2WebSecurityConfigurationTests.authorizedClientRepositoryBeanIsConditionalOnMissingBean();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme