package com.techmate.techmate.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"app.cors.allowed-origins=http://localhost:3000,http://localhost:3001"})
public class WebConfigTest {

    @Autowired
    private WebConfig webConfig;

    @Test
    public void contextLoads_andWebConfigBeanPresent() {
        assertThat(webConfig).isNotNull();
    }
}

