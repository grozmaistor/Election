package grozdan.test.election.web.impl;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public FilterRegistrationBean<LocalhostFilter> localhostFilterRegistrationBean() {
        FilterRegistrationBean<LocalhostFilter> registrationBean = new FilterRegistrationBean<>();
        LocalhostFilter localhostFilter = new LocalhostFilter();
        registrationBean.setFilter(localhostFilter);
        registrationBean.addUrlPatterns("/election");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}