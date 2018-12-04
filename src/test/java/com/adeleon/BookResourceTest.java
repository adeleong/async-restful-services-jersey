package com.adeleon;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BookResourceTest extends JerseyTest{

    private String book1_id;
    private String book2_id;

    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        final BookDao dao = new BookDao();
        return new BookApplication(dao);
    }

    protected Response addBook(String author, String title, Date published, String isbn, String... extras){

        HashMap<String, Object> book = new HashMap<>();
        book.put("author",author);
        book.put("title",title);
        book.put("published",published);
        book.put("isbn", isbn);
        if (extras != null){
            int count = 1;
            for (String s : extras) {
                book.put("extras"+count++, s);
            }
        }

        Entity<HashMap<String, Object>> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("books").request().post(bookEntity);
        return response;
    }

    protected HashMap<String, Object> toHashMap(Response response){
        return (response.readEntity(new GenericType<HashMap<String, Object>>() {}));
    }

    @Before
    public void setupBooks(){
        book1_id = (String)toHashMap(addBook("anderson", "clean code", new Date(), "4538")).get("id");
        book2_id = (String)toHashMap(addBook("rocio", "macro excel", new Date(), "2589")).get("id");
    }

    @Test
    public void testAddBook() throws ParseException {
        Date thisDate = new Date();
        Response response = addBook("adeleon","myFavoriteBook", new Date(), "1478");
        assertEquals("http status",200, response.getStatus());

        HashMap<String, Object> responseBook = toHashMap(response);
        assertNotNull(responseBook.get("id"));
        assertEquals("myFavoriteBook", responseBook.get("title"));
        assertEquals("adeleon", responseBook.get("author"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        assertEquals(thisDate, dateFormat.parse( (String.valueOf(responseBook.get("published"))) ));
        assertEquals("1478", responseBook.get("isbn"));
    }


    @Test
    public void testGetBook(){
        HashMap<String, Object> response = toHashMap( target("books").path(book1_id).request().get());
        assertNotNull(response);
    }

    @Test
    public void testGetBooks(){
        Collection<HashMap<String, Object>> response = target("books").request()
                .get(new GenericType<Collection<HashMap<String, Object>>>(){});
        assertEquals(2, response.size());
    }

    @Test
    public void testExtraField(){
        Response response = addBook("ade", "jersey", new Date(), "001412", "extra field hello");
        assertEquals(200, response.getStatus());

        HashMap<String, Object> book = toHashMap(response);
        assertNotNull(book.get("id"));
        assertEquals(book.get("extras1"),"extra field hello");
    }

    @Test
    public void testDao(){
        Book response1 = target("books").path(book1_id).request().get(Book.class);
        Book response2 = target("books").path(book1_id).request().get(Book.class);
        assertEquals(response1.getPublished().getTime(),response2.getPublished().getTime());
    }

    @Test
    public void getBooksAsXml() {
        String output = target("books").request(MediaType.APPLICATION_XML).get().readEntity(String.class);
        XML xml = new XMLDocument(output);

        assertEquals("anderson", xml.xpath("/books/book[@id='" + book1_id + "']/author/text()").get(0));
        assertEquals("clean code", xml.xpath("/books/book[@id='" + book1_id + "']/title/text()").get(0));
        assertEquals(2, xml.xpath("//book/author/text()").size());
    }
}
