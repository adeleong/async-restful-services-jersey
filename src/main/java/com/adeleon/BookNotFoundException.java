package com.adeleon;

public class BookNotFoundException extends Exception {

    BookNotFoundException(String message){
        super(message);
    }
}
