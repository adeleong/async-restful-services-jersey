package com.adeleon;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BookResourceTest extends JerseyTest{

    protected Application configure() {
        //enable(TestProperties.LOG_TRAFFIC);
       // enable(TestProperties.DUMP_ENTITY);
        final BookDao dao = new BookDao();
        return new BookApplication(dao);
    }

    @Test
    public void testGetBook(){
        Book response = target("books").path("1").request().get(Book.class);
        assertNotNull(response);
    }

    @Test
    public void testGetBooks(){
        Collection<Book> response = target("books").request().get(new GenericType<Collection<Book>>(){});
        assertEquals(2, response.size());
    }

    @Test
    public void testDao(){
        Book response1 = target("books").path("1").request().get(Book.class);
        Book response2 = target("books").path("1").request().get(Book.class);
        assertEquals(response1.getPublished().getTime(),response2.getPublished().getTime());
    }
}
