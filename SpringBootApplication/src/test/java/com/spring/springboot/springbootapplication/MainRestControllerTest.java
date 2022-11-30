package com.spring.springboot.springbootapplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.spring.springboot.springbootapplication.controller.MainRestController;
import com.spring.springboot.springbootapplication.dto.CarDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("/application-test.properties")
@Sql(value = "/schema-test-start.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/schema-test-end.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MainRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MainRestController mainRestController;

    @Test
    public void getAllShopsShouldReturnStatus200AndCorrectJSON() throws Exception {
        final StringBuilder builder = new StringBuilder();

        try (final BufferedReader reader = new BufferedReader(
            new InputStreamReader(
            new FileInputStream("src/test/resources/all-shops-test.json"))))
        {

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mockMvc.perform(get("/api/shops"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(builder.toString()));
    }

    @Test
    public void getAllCarsShouldReturnStatus200AndCorrectJSON() throws Exception {
        final StringBuilder builder = new StringBuilder();

        try (final BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                new FileInputStream("src/test/resources/all-cars-test.json"))))
        {

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mockMvc.perform(get("/api/cars"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(builder.toString()));
    }

    @Test
    public void saveNewCarShouldReturnStatus200AndNewCarShouldBeAdded() throws Exception {
        final CarDTO carDTO = CarDTO.builder()
            .brand("Subaru")
            .model("XV")
            .ageOfProduce(2022)
            .price(4000000)
            .build();

        final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = writer.writeValueAsString(carDTO);

        this.mockMvc.perform(post("/api/cars").contentType(MediaType.APPLICATION_JSON).content(json))
            .andDo(print())
            .andExpect(status().isOk());

        final CarDTO addedCar = mainRestController.getAllCars().stream()
            .filter(car -> (carDTO.getBrand().equals(car.getBrand()) && carDTO.getModel().equals(car.getModel())))
            .findFirst()
            .get();

        Assert.assertNotEquals(carDTO.getId(), addedCar.getId());
    }
}