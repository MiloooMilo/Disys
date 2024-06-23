package com.example.pdfgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Charge {
    @Getter
    private double kwh;
    @Getter
    private int customerID;
}
