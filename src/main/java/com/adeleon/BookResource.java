package com.adeleon;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/books")
public class BookResource {

    @Context BookDao dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void getBooks(@Suspended final AsyncResponse response){
        response.resume(dao.getBooks());
        ListenableFuture<Collection<Book>> booksFuture = dao.getBooksAsync();
        Futures.addCallback(booksFuture, new FutureCallback<Collection<Book>>() {
            public void onSuccess(Collection<Book> books) {
                response.resume(books);
            }

            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void getBook(@PathParam("id") String id, @Suspended final AsyncResponse response){
        ListenableFuture<Book> bookListenableFuture = dao.getBookAsync(id);
        Futures.addCallback(bookListenableFuture, new FutureCallback<Book>() {
            public void onSuccess(Book book) {
                response.resume(book);
            }

            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
        response.resume(dao.getBook(id));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void addBook(Book book, @Suspended final AsyncResponse response){
        ListenableFuture<Book> bookFuture = dao.addBookAsync(book);
        Futures.addCallback(bookFuture, new FutureCallback<Book>() {
            public void onSuccess(Book addedBook) {
                response.resume(addedBook);
            }
            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
    }
}
