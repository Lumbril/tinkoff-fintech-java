package org.example;

import org.example.controllers.WeatherController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = {WeatherController.class})
@AutoConfigureMockMvc
public class WeatherControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetOk() throws Exception {
        String accessAnswer = "{\"temperature\": 20}";

        mockMvc.perform(get("/api/weather/Казань"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(accessAnswer));
    }

    @Test
    public void testGetNotFound() throws Exception {
        String notFoundAnswer = "{\"error\": \"No value present\"}";

        mockMvc.perform(get("/api/weather/London"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(notFoundAnswer));
    }
}
