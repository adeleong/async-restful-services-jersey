package com.adeleon;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class BookApplication extends ResourceConfig{
    BookApplication(final BookDao dao){
        packages("com.adeleon");
        register(new AbstractBinder() {
            protected void configure() {
                bind(dao).to(BookDao.class);
            }
        });
    }
}
