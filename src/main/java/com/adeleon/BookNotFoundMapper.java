package com.adeleon;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BookNotFoundMapper implements ExceptionMapper<BookNotFoundException> {

    public Response toResponse(BookNotFoundException exception) {
        return Response.status(404).entity(exception.getMessage()).type("text/plain").build();
    }
}
