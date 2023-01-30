package kr.co.marketbill.marketbillcoreserver.config

import graphql.com.google.common.collect.ImmutableList
import kr.co.marketbill.marketbillcoreserver.security.JwtAuthFilter
import kr.co.marketbill.marketbillcoreserver.security.JwtExceptionFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig {

    @Autowired
    private lateinit var jwtAuthFilter: JwtAuthFilter

    @Autowired
    private lateinit var jwtExceptionFilter: JwtExceptionFilter

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = ImmutableList.of("*")
        configuration.allowCredentials = true
        configuration.allowedHeaders = ImmutableList.of("*")
        configuration.exposedHeaders = listOf("*")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        return http
            .headers().frameOptions().disable().and()
            .cors().configurationSource(corsConfigurationSource())
            .and().addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(jwtExceptionFilter, jwtAuthFilter.javaClass)
            .csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
            .authorizeRequests(
                Customizer { auth ->
                    auth
                        .antMatchers(HttpMethod.OPTIONS, "/**/graphql/*").permitAll()
                        .antMatchers(HttpMethod.POST, "/**/graphql/*").permitAll()
                        .antMatchers(HttpMethod.GET, "/**/graphiql/*")
                        .permitAll()
                }
            )
            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling().and()
            .httpBasic().disable()
            .logout().disable()
            .anonymous().disable()
            .build()
    }
}