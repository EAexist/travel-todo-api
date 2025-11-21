/* https://github.com/spring-projects/spring-security-samples/tree/main/servlet/java-configuration/authentication/remember-me */
/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.matchalab.travel_todo_api.config;

import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@ExtendWith(SpringExtension.class)
@SpringJUnitWebConfig(classes = SecurityConfig.class)
public class WebSecurityConfigTest {

    // @Test
    // void loginWhenRemembermeThenAuthenticated(WebApplicationContext context)
    // throws Exception {
    //     // @formatter:off
	// 	MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
	// 			.apply(springSecurity())
	// 			.build();

	// 	MockHttpServletRequestBuilder login = post("/auth/web-browser")
	// 			.with(csrf())
	// 			.param("username", "user")
	// 			.param("remember-me", "true");
	// 	MvcResult mvcResult = mockMvc.perform(login)
	// 			.andExpect(authenticated())
	// 			.andReturn();
	// 	// @formatter:on

    // Cookie rememberMe = mvcResult.getResponse().getCookie("remember-me");

    //     // @formatter:off
	// 	mockMvc.perform(get("/").cookie(rememberMe))
	// 			.andExpect(authenticated());
	// 	// @formatter:on
    // }

    // @Test
    // void loginWhenNoRemembermeThenUnauthenticated(WebApplicationContext context)
    // throws Exception {
    //     // @formatter:off
	// 	MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
	// 			.apply(springSecurity())
	// 			.build();

	// 	MockHttpServletRequestBuilder login = post("/auth/web-browser")
	// 			.with(csrf())
	// 			.param("username", "user")
	// 			.param("remember-me", "true");
	// 	// @formatter:on

    //     // @formatter:off
	// 	mockMvc.perform(get("/"))
	// 			.andExpect(unauthenticated());
	// 	// @formatter:on
    // }

    // @Test
    // void loginWhenNoRemembermeThenNoCookie(WebApplicationContext context) throws
    // Exception {
    //     // @formatter:off
	// 	MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
	// 			.apply(springSecurity())
	// 			.build();

	// 	MockHttpServletRequestBuilder login = post("/auth/web-browser")
	// 			.with(csrf())
	// 			.param("username", "user");
	// 	MvcResult mvcResult = mockMvc.perform(login)
	// 			.andExpect(authenticated())
	// 			.andReturn();
	// 	// @formatter:on

    // Cookie rememberMe = mvcResult.getResponse().getCookie("remember-me");

    // assertThat(rememberMe).isNull();
    // }

}