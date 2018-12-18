package com.adeleon;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

public class BookApplication extends ResourceConfig{
    BookApplication(final BookDao dao){

        JacksonJsonProvider json = new JacksonJsonProvider().
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).
                configure(SerializationFeature.INDENT_OUTPUT, true);

        JacksonXMLProvider xml = new JacksonXMLProvider().
                configure(SerializationFeature.INDENT_OUTPUT, true).
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        packages("com.adeleon");
        register(new AbstractBinder() {
            protected void configure() {
                bind(dao).to(BookDao.class);
            }
        });
        register(json);
        register(xml);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE,true);
    }
}
