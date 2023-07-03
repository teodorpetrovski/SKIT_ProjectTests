package com.example.lab;


import com.example.lab.model.Author;
import com.example.lab.model.Book;
import com.example.lab.model.Country;
import com.example.lab.model.DTO.AuthorDto;
import com.example.lab.model.DTO.BookDto;
import com.example.lab.model.DTO.CountryDto;
import com.example.lab.model.enums.Category;
import com.example.lab.service.AuthorService;
import com.example.lab.service.BookService;
import com.example.lab.service.CountryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CountryControllerTest {

    private String baseUrl="http://localhost:9999/api/country";

    private static RestTemplate restTemplate;



    @Autowired
    CountryService countryService;


    private static Country country;


    @BeforeEach
    public void init()
    {
        restTemplate=new RestTemplate();
        CountryDto countryDto=new CountryDto("USA","North America");
        country=countryService.save(countryDto).get();
    }

    @Test
    public void getAllTest()
    {
        List<Country> response = restTemplate.getForObject(baseUrl,List.class);
        Assertions.assertEquals(1,response.size());
    }

    @Test
    public void addCountryTest()
    {
        CountryDto countryDto=new CountryDto("Macedonia","Europe");
        Country response = restTemplate.postForObject(baseUrl+"/add",countryDto, Country.class);
        Assertions.assertEquals("Macedonia", response.getName());
        Assertions.assertEquals("Europe", response.getContinent());
    }

    @Test
    public void editCountryTest()
    {
        CountryDto countryDto=new CountryDto("Spain","Europe");
        HttpEntity<CountryDto> requestEntity = new HttpEntity<>(countryDto);
        ResponseEntity<Country> response = restTemplate.exchange(
                baseUrl+"/edit/{id}",
                HttpMethod.POST,
                requestEntity,
                Country.class,
                country.getId()
        );

        Assertions.assertEquals("Spain",this.countryService.findById(country.getId()).get().getName());

    }


    @Test
    public void deleteCountryTest()
    {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/delete/{id}",
                HttpMethod.DELETE,
                null,
                String.class,
                country.getId()
        );

        Assertions.assertEquals(0,countryService.findAll().size());
    }

}
