package com.nhom1.java6;

import com.nhom1.java6.advice.CustomAuthorizationRequestResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthConfig {

//  Khi nào thêm client mới có thể dễ dàng chỉnh sửa code
    private static List<String> clients = Arrays.asList("google");

    @Autowired
    private Environment env;

    private static String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public InMemoryUserDetailsManager configAuthentication() throws Exception {
//        auth.jdbcAuthentication().dataSource(dataSource);

//        auth.inMemoryAuthentication()
//                .withUser("user").password("123").roles("USER")
//                .and()
//                .withUser("admin").password("123").roles("ADMIN")
//                .and()
//                .withUser("guest").password("123").roles("GUEST");

        UserDetails user = User.withUsername("user")
                .password("123")
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password("123")
                .roles("ADMIN")
                .build();
        UserDetails guest = User.withUsername("guest")
                .password("123")
                .roles("GUEST")
                .build();
        return new InMemoryUserDetailsManager(user,admin,guest);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(){
        List<ClientRegistration> registrations = clients.stream()
                .map(c -> createRegistration(c)) // Với từng tên client (vd: google, facebook, etc. Gọi hàm createRegistration)
                .filter(Objects::nonNull) // tương đương với lambda (client -> client != null)
                .toList();
        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration createRegistration(String clientName){
        String clientId = env.getProperty(CLIENT_PROPERTY_KEY + clientName + ".client-id");
        if(clientId == null) return null;
        String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + clientName + ".client-secret");
        if(clientName.equals("google")){
            return CommonOAuth2Provider.GOOGLE
                    .getBuilder(clientName)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        }
        return null;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
////
//        http.csrf(config -> config.disable())
//                .cors(config -> config.disable())
//                .authorizeHttpRequests(req -> req
//                        .requestMatchers("/home/index","/home/about","/assets/**","/auth/**","/api/**").permitAll()
//                        .anyRequest().authenticated())
//                .httpBasic(Customizer.withDefaults());
//
         http.formLogin(form -> form
                 .loginPage("/auth/login/form")
                 .loginProcessingUrl("/auth/login")
                 .defaultSuccessUrl("/home/index",true)
                 .failureForwardUrl("/auth/login/error")
                 .usernameParameter("username")
                 .passwordParameter("password"))
                 .csrf(config -> config.disable())
                .cors(config -> config.disable())
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/home/index","/home/about","/assets/**","/auth/**","/api/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                 .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/home/index",true)
                         .authorizationEndpoint(authEnd -> authEnd
                                 .authorizationRequestResolver(new CustomAuthorizationRequestResolver(this.clientRegistrationRepository()))))
                 .rememberMe(remeberMe ->remeberMe.rememberMeParameter("remember"))
                 .logout(logout ->logout.logoutUrl("/auth/logoff")
                         .clearAuthentication(true)
                         .logoutSuccessUrl("/auth/login/form")
                         .invalidateHttpSession(true)
                         .deleteCookies("JSESSIONID"))
                .exceptionHandling(excep -> excep.accessDeniedPage("/auth/access/denied"))
                 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
        return http.build();
    }



}
