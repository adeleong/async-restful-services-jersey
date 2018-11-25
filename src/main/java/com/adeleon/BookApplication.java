package com.adeleon;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class BookApplication extends ResourceConfig{
    BookApplication(final BookDao dao){

        JacksonJsonProvider json = new JacksonJsonProvider().
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        packages("com.adeleon");
        register(new AbstractBinder() {
            protected void configure() {
                bind(dao).to(BookDao.class);
            }
        });
        register(json);
    }
}
