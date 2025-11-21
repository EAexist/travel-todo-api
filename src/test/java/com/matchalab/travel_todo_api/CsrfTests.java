package com.matchalab.travel_todo_api;

import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

/* 
 * https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-testing
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SecurityConfig.class)
@WebAppConfiguration
public class CsrfTests {

    // private MockMvc mockMvc;

    // @BeforeEach
    // public void setUp(WebApplicationContext applicationContext) {
    // this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
    // .apply(springSecurity())
    // .build();
    // }

    // @Test
    // public void loginWhenValidCsrfTokenThenSuccess() throws Exception {
    // this.mockMvc.perform(post("/login").with(csrf())
    // .accept(MediaType.TEXT_HTML)
    // .param("username", "user")
    // .param("password", "password"))
    // .andExpect(status().is3xxRedirection())
    // .andExpect(header().string(HttpHeaders.LOCATION, "/"));
    // }

    // @Test
    // public void loginWhenInvalidCsrfTokenThenForbidden() throws Exception {
    // this.mockMvc.perform(post("/login").with(csrf().useInvalidToken())
    // .accept(MediaType.TEXT_HTML)
    // .param("username", "user")
    // .param("password", "password"))
    // .andExpect(status().isForbidden());
    // }

    // @Test
    // public void loginWhenMissingCsrfTokenThenForbidden() throws Exception {
    // this.mockMvc.perform(post("/login")
    // .accept(MediaType.TEXT_HTML)
    // .param("username", "user")
    // .param("password", "password"))
    // .andExpect(status().isForbidden());
    // }

    // @Test
    // @WithMockUser
    // public void logoutWhenValidCsrfTokenThenSuccess() throws Exception {
    // this.mockMvc.perform(post("/logout").with(csrf())
    // .accept(MediaType.TEXT_HTML))
    // .andExpect(status().is3xxRedirection())
    // .andExpect(header().string(HttpHeaders.LOCATION, "/login?logout"));
    // }
}