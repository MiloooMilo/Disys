package com.example.collectionreceiver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Charge {

    @Getter
    private String kwh;

    @Getter
    private String customerID;
}
