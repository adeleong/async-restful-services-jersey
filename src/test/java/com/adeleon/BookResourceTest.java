package com.adeleon;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BookResourceTest extends JerseyTest{

    private String book1_id;
    private String book2_id;

    protected Application configure() {
        //enable(TestProperties.LOG_TRAFFIC);
       // enable(TestProperties.DUMP_ENTITY);
        final BookDao dao = new BookDao();
        return new BookApplication(dao);
    }

    protected Response addBook(String author, String title, Date published, String isbn){
        Book book = new Book();
        book.setAuthor(author);
        book.setTitle(title);
        book.setPublished(published);
        book.setIsbn(isbn);
        Entity<Book> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("books").request().post(bookEntity);
        return response;
    }

    @Before
    public void setupBooks(){
        book1_id = addBook("anderson", "clean code", new Date(), "4538").readEntity(Book.class).getId();
        book2_id = addBook("rocio", "macro excel", new Date(), "2589").readEntity(Book.class).getId();
    }

    @Test
    public void testAddBook(){
        Date thisDate = new Date();
        Response response = addBook("adeleon","myFavoriteBook", new Date(), "1478");
        assertEquals("http status",200, response.getStatus());
        Book responseBook = response.readEntity(Book.class);
        assertNotNull(responseBook.getId());
        assertEquals("myFavoriteBook", responseBook.getTitle());
        assertEquals("adeleon", responseBook.getAuthor());
        assertEquals(thisDate, responseBook.getPublished());
        assertEquals("1478", responseBook.getIsbn());
    }


    @Test
    public void testGetBook(){
        Book response = target("books").path(book1_id).request().get(Book.class);
        assertNotNull(response);
    }

    @Test
    public void testGetBooks(){
        Collection<Book> response = target("books").request().get(new GenericType<Collection<Book>>(){});
        assertEquals(2, response.size());
    }

    @Test
    public void testDao(){
        Book response1 = target("books").path(book1_id).request().get(Book.class);
        Book response2 = target("books").path(book1_id).request().get(Book.class);
        assertEquals(response1.getPublished().getTime(),response2.getPublished().getTime());
    }
}
