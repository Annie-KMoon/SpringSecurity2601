package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableMethodSecurity //시큐어 어노테이션 활성화
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
/*
    로우하이어라키란?
    상위 권한이 하위 권한을 자동으로 포함하도록 만드는 권한 구조를 말함.
    즉 관리자 권한이면 사용자 권한도 당연히 갖게된다.
    1)로그인 성공 -> 2)사용자 권한 반환(UserDetails, getAuthorities())
    3)spring security가 RoleHierarchy를 적용
    4)상위권한 -> 하위권한으로 자동 확장
    5)인가(hasRole, hasAuthority())판단시 반영
    최종 효과
    - /user/** : USER 이상(= USER, MANAGER, ADMIN)
     - /manager/** : MANAGER 이상(= MANAGER, ADMIN)
     - /admin/** : ADMIN만
*/
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl rh = new RoleHierarchyImpl();
        rh.setHierarchy("""
        ROLE_ADMIN > ROLE_MANAGER 
        ROLE_MANAGER > ROLE_USER
    """);
        return rh;
    }

    @Bean
    GrantedAuthoritiesMapper authoritiesMapper(RoleHierarchy roleHierarchy) {
        // 로그인 시 ADMIN이면 MANAGER/USER 권한을 자동으로 '추가' 부여
        return new RoleHierarchyAuthoritiesMapper(roleHierarchy);
    }


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/user/**").authenticated() //인증만 되면 모두 다 접근가능
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/joinForm","join").permitAll()//권한없이접근가능함
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/loginForm") //사용자 정의한 로그인 페이지
                        // -> /login이 호출되면 시큐리티가 낚아채서 대신 로그인을 진행함
                        .loginProcessingUrl("/loginProcess") //로그인 요청 처리 URL(디폴트)
                        .defaultSuccessUrl("/") //로그인 성공 후 이동할 페이지
                        .failureUrl("/login-error")//로그인실패시 이동할 페이지
                        .permitAll()
                )
                //로그아웃 설정 추가
                .logout(logout -> logout
                        .logoutUrl("/logout")//로그아웃 처리 URL(디폴트)
                        .logoutSuccessUrl("/") //로그아웃 후 이동할 URL
                        .invalidateHttpSession(true) //세션 무효화(기본값 true)
                        .deleteCookies("JSESSIONID")//쿠키 삭제
                        .permitAll()
                )
                .exceptionHandling(exception-> exception.accessDeniedPage("/access-denied"));
        return http.build();
    }// end of defaultSecurityFilterChain
    //아래 코드가 없으면 user 12345로 로그인 안됨.
    //spring security 5이상에서는 비밀번호를 저장할 때 반드시 인코딩 방식이 명시되어야 함.
    //Spring Security 5+부터는 비밀번호 저장/검증 에 인코더 가 없으면 에러 발생
    // NoOpPasswordEncorder: 평문 그대로 비교함(암호화 없이 비교)
    //@Bean
    //public PasswordEncoder passwordEncoder(){
    //    return NoOpPasswordEncoder.getInstance();
    //}
}