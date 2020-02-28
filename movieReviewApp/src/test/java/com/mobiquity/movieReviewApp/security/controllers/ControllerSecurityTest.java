package com.mobiquity.movieReviewApp.security.controllers;


import com.mobiquity.movieReviewApp.security.AuthenticationConfig;
import com.mobiquity.movieReviewApp.security.SecurityTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ContextConfiguration(classes = {
        SecurityTestConfig.class,
        ApplicationContext.class,
        AuthenticationConfig.class
})
public abstract class ControllerSecurityTest
        implements SecurityResults {

    protected MockMvc mockMvc;
    private int[] unAuthorizedStatusCodes = {401, 302};

    @Override
    final public boolean isAuthenticated(String url) throws Exception {
        return isAuthenticated(url, unAuthorizedStatusCodes);
    }

    @Override
    final public boolean isAuthenticated(String url, int... codes) throws Exception {
        AtomicBoolean statusMatchesUnauthCodes = new AtomicBoolean(false);
        int status = mockMvc.perform(get(url)).andReturn().getResponse().getStatus();

        Arrays.stream(codes).forEach(
                (i) ->
                {
                    if (i == status || statusMatchesUnauthCodes.get()) {
                        statusMatchesUnauthCodes.set(true);
                    }
                });
        return !statusMatchesUnauthCodes.get();
    }

    @Autowired
    final void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }
}
