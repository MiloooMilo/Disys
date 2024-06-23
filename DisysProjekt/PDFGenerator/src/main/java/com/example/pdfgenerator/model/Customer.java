package com.example.pdfgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public class Customer {
    @Getter
    private int customerId;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
}
