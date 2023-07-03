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
public class AuthorControllerTest {
    private String baseUrl="http://localhost:9999/api/author";

    private static RestTemplate restTemplate;


    @Autowired
    AuthorService authorService;


    @Autowired
    CountryService countryService;

    private static Author author;
    private static Country country;


    @BeforeEach
    public void init()
    {
        restTemplate=new RestTemplate();
        CountryDto countryDto=new CountryDto("USA","North America");
        country=countryService.save(countryDto).get();
        AuthorDto authorDto=new AuthorDto("Andrew","Fillips",country.getId());
        author=authorService.save(authorDto).get();

    }

    @Test
    public void getAllTest()
    {
        List<Author> response = restTemplate.getForObject(baseUrl,List.class);
        Assertions.assertEquals(1,response.size());
    }

    @Test
    public void addAuthorTest()
    {
        AuthorDto authorDto=new AuthorDto("Alexander","Floyd", country.getId());
        Author response = restTemplate.postForObject(baseUrl+"/add",authorDto, Author.class);
        Assertions.assertEquals("Alexander", response.getName());

    }

    @Test
    public void editAuthorTest()
    {
        AuthorDto authorDto=new AuthorDto("Bob","Smith", country.getId());
        HttpEntity<AuthorDto> requestEntity = new HttpEntity<>(authorDto);
        ResponseEntity<Author> response = restTemplate.exchange(
                baseUrl+"/edit/{id}",
                HttpMethod.POST,
                requestEntity,
                Author.class,
                author.getId()
        );

        Assertions.assertEquals("Bob",this.authorService.findById(author.getId()).get().getName());

    }

    @Test
    public void deleteAuthorTest()
    {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/delete/{id}",
                HttpMethod.DELETE,
                null,
                String.class,
                author.getId()
        );

        Assertions.assertEquals(0,authorService.findAll().size());
    }
}
