package org.apitesting.api;

import io.restassured.RestAssured;

import static org.apitesting.constants.Constants.BASE_URI;

public class BaseApi {
    public BaseApi() {
        setupRequestSpecification();
    }

    public void setupRequestSpecification()
    {
        RestAssured.baseURI = BASE_URI;
    }
}
