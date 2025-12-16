package com.example.labyrinth;

import org.springframework.test.web.servlet.MvcResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BoardControllerTests {

    @Autowired
    private MockMvc mvc;

        @Test
        void createAndRetrieveBoard() throws Exception {
        MvcResult res = mvc.perform(post("/api/boards"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.size").exists())
            .andReturn();

        String location = res.getResponse().getHeader("Location");
        // expect a 7x7 board by default
        mvc.perform(get(location))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tiles.length()", is(7 * 7)));
        }

    @Test
    void getNonExistentBoardReturns404() throws Exception {
        mvc.perform(get("/api/boards/9999999"))
                .andExpect(status().isNotFound());
    }
}
