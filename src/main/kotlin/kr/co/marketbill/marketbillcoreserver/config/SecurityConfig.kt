package kr.co.marketbill.marketbillcoreserver.config


//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true) // pre-authorize를 위해 사용
//class SecurityConfig {
//
////    @Autowired
////    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
//
//    @Bean
//    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()
//
//    @Bean
//    @Throws(Exception::class)
//    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
//        return http
////            .addFilterBefore(
////                jwtAuthenticationFilter,
////                UsernamePasswordAuthenticationFilter::class.java
////            )
//            .csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
//            .authorizeRequests(
//                Customizer { auth ->
//                    auth
//                        .antMatchers("**/graphiql/**")
//                        .permitAll()
//                }
//            )
//            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            }
//            .exceptionHandling().and()
//            .httpBasic().disable()
//            .logout().disable()
//            .anonymous().disable()
//            .build()
//    }
//
////    @Bean
////    fun users(): InMemoryUserDetailsManager? {
////        return InMemoryUserDetailsManager(
////            User.withUsername("admin")
////                .password("{noop}1234")
////                .authorities("admin")
////                .build()
////        )
////    }
//
//}