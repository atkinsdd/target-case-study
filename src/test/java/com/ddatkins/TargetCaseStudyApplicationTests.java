package com.ddatkins;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.ddatkins.tgt.products.controller.ProductsController;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TargetCaseStudyApplicationTests {
	
	@Autowired
    private MockMvc mockMvc;
	@Autowired
	private ProductsController productsController;
	


	@Test
	public void contextLoads() {
		assertThat(productsController).isNotNull();
	}
	
	@Test
	public void testGetProductNotFound() throws Exception {
		this.mockMvc.perform(get("/product/0")).andDo(print()).andExpect(status().isNotFound());
	}
	
	@Test
	public void testGetProductFound() throws Exception {
		this.mockMvc.perform(get("/product/14777812")).andDo(print()).andExpect(status().isOk());
	}

}
