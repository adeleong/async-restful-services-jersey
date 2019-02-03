package com.adeleon;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.sun.org.apache.regexp.internal.RE;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BookResourceTest extends JerseyTest{

    private String book1_id;
    private String book2_id;

    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        final BookDao dao = new BookDao();
        return new BookApplication(dao);
    }

    protected void configureClient(ClientConfig clientConfig){
        JacksonJsonProvider json = new JacksonJsonProvider();
        json.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        clientConfig.register(json);
        clientConfig.connectorProvider(new GrizzlyConnectorProvider());
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

    @Test
    public void AddBookNoAuthor(){
        Response response = addBook(null, "Jersey", new Date(), "12345");
        assertEquals(400, response.getStatus());
        String message = response.readEntity(String.class);
        assertTrue(message.contains("author is a required field"));
    }

    @Test
    public void AddBookNoTitle(){
        Response response = addBook("adeleon", null, new Date(), "12345");
        assertEquals(400, response.getStatus());
        String message = response.readEntity(String.class);
        assertTrue(message.contains("title is a required field"));
    }

    @Test
    public void AddBookNoBook(){
        Response response = target("books").request().post(null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void BookNotFoundWithMessage(){
        Response response = target("books").path("1").request().get();
        assertEquals(404, response.getStatus());
        String message = response.readEntity(String.class);
        assertTrue(message.contains("Book 1 is not found"));
    }

    @Test
    public void BookEntityTagNotModified(){
        EntityTag entityTag = target("books").path(book1_id).request().get().getEntityTag();
        assertNotNull(entityTag);

        Response response = target("books").path(book1_id).request().header("If-None-Match", entityTag).get();
        assertEquals(304, response.getStatus());
    }

    @Test
    public void UpdateBookAuthor(){
        HashMap<String, Object> updates = new HashMap<String, Object>();
        updates.put("author", "updatedAuthor");
        Entity<HashMap<String, Object>> updateEntity = Entity.entity(updates, MediaType.APPLICATION_JSON);
        Response updateResponse = target("books").path(book1_id).request().build("PATCH", updateEntity).invoke();

        assertEquals(200, updateResponse.getStatus());

        Response getResponse = target("books").path(book1_id).request().get();
        HashMap<String, Object> getResponseMap = toHashMap(getResponse);

        assertEquals("updatedAuthor", getResponseMap.get("author"));
    }

    @Test
    public void PatchMethodOverride(){
        HashMap<String, Object> updates = new HashMap<String, Object>();
        updates.put("author","updateAuthor");
        Entity<HashMap<String, Object>> updateEntity = Entity.entity(updates, MediaType.APPLICATION_JSON);
        Response updateResponse = target("books").path(book1_id).queryParam("_method", "PATCH").
                request().post(updateEntity);
        assertEquals(200, updateResponse.getStatus());

        Response getResponse = target("books").path(book1_id).request().get();
        HashMap<String, Object> getResponseMap = toHashMap(getResponse);
        assertEquals("updateAuthor", getResponseMap.get("author"));
    }
}

