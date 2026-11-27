package pl.put.poznan.tools.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JsonToolsControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new JsonToolsController()).build();
    }

    @Test
    void minimizesValidJson() throws Exception {
        String input = "{\n  \"imie\": \"Jan Kowalski\",\n  \"liczby\": [ 1, 2, 3 ]\n}";

        mockMvc.perform(post("/api/minimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"imie\":\"Jan Kowalski\",\"liczby\":[1,2,3]}"));
    }

    @Test
    void acceptsTextPlainContentType() throws Exception {
        mockMvc.perform(post("/api/minimize")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("{ \"ok\" : true }"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"ok\":true}"));
    }

    @Test
    void returns400OnInvalidJson() throws Exception {
        mockMvc.perform(post("/api/minimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"broken\": "))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Invalid JSON")));
    }

    @Test
    void returns400OnBlankBody() throws Exception {
        mockMvc.perform(post("/api/minimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("   "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns400OnEmptyBody() throws Exception {
        mockMvc.perform(post("/api/minimize")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}