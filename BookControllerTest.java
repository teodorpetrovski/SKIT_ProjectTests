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
import org.junit.Before;
import org.junit.Assert;
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
public class BookControllerTest {
    private String baseUrl="http://localhost:9999/api/books";

    private static RestTemplate restTemplate;



    @Autowired
    BookService bookService;


    @Autowired
    AuthorService authorService;


    @Autowired
    CountryService countryService;

    private static Author author;
    private static Country country;
    private static Book book;

    @BeforeEach
    public void init()
    {
        restTemplate=new RestTemplate();
        CountryDto countryDto=new CountryDto("USA","North America");
        country=countryService.save(countryDto).get();
        AuthorDto authorDto=new AuthorDto("Andrew","Fillips",country.getId());
        author=authorService.save(authorDto).get();
        BookDto bookDto=new BookDto("Adventures", Category.HISTORY, author.getId(), 10);
        book=bookService.save(bookDto).get();


    }


    @Test
    public void getBooksTest()
    {
        List<Book> response = restTemplate.getForObject(baseUrl,List.class);
        Assertions.assertEquals(1,response.size());
    }

    @Test
    public void findByIdTest()
    {
        ResponseEntity<Book> response = restTemplate.exchange(
                baseUrl+"/{id}",
                HttpMethod.GET,
                null,
                Book.class,
                book.getId()
        );

        Assertions.assertEquals("Adventures",response.getBody().getName());

    }


    @Test
    public void addBookTest()
    {
        BookDto bookDto=new BookDto("Romeo&Juliet", Category.DRAMA, author.getId(), 10);
        Book response = restTemplate.postForObject(baseUrl+"/add",bookDto, Book.class);
        Assertions.assertEquals("Romeo&Juliet", response.getName());
    }

    @Test
    public void editBookTest()
    {
        BookDto bookDto=new BookDto("Romeo&Juliet", Category.DRAMA, author.getId(), 10);
        HttpEntity<BookDto> requestEntity = new HttpEntity<>(bookDto);
        ResponseEntity<Book> response = restTemplate.exchange(
                baseUrl+"/edit/{id}",
                HttpMethod.POST,
                requestEntity,
                Book.class,
                book.getId()
        );

        Assertions.assertEquals("Romeo&Juliet",bookService.findById(book.getId()).get().getName());
    }

    @Test
    public void deleteBookTest()
    {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/delete/{id}",
                HttpMethod.DELETE,
                null,
                String.class,
                book.getId()
        );

        Assertions.assertEquals(0,bookService.findAll().size());
    }

    @Test
    public void markAsTakenTest()
    {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/mark/{id}",
                HttpMethod.POST,
                null,
                String.class,
                book.getId()
        );

        Assertions.assertEquals(9,bookService.findById(book.getId()).get().getAvailableCopies());
    }


    @Test
    public void getCategoriesTest()
    {
        List<String> response = restTemplate.getForObject(baseUrl+"/categories",List.class);
        Assertions.assertEquals(7,response.size());
    }



}
