package com.adeleon;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookDao {

    private Map<String, Book> books;

    BookDao(){
        books = new HashMap<String, Book>();
        Book book1 =  new Book();
        book1.setId("1");
        book1.setTitle("theadeBook");
        book1.setAuthor("adeleon");
        book1.setIsbn("12456");
        book1.setPublished(new Date());
        book1.setStock(true);

        Book book2 =  new Book();
        book2.setId("2");
        book2.setTitle("My2Book");
        book2.setAuthor("aguzman");
        book2.setIsbn("78956");
        book2.setPublished(new Date());
        book2.setStock(false);

        books.put(book1.getId(), book1);
        books.put(book2.getId(), book2);
    }

    Collection<Book> getBooks(){
        return books.values();
    }

    Book getBook(String id){
        return books.get(id);
    }
}
