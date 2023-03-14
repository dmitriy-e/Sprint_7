import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apitesting.api.CourierApi;
import org.apitesting.domain.Credential;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class CreateCourierTest {
    private CourierApi courierApi;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        courierApi.setCredential(new Credential("courier12345678", "12345678"));
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Check creating new courier")
    public void createNewCourierAndCheckResponse() {
        courierApi.createNewCourier().then().assertThat().body("ok", is(true))
                .and()
                .statusCode(201);
    }

    @Test
    @DisplayName("Check new creation error without login or password")
    public void createNewCourierWithoutLoginOrPasswordError() {
        courierApi.setCredential(new Credential("", ""));
        courierApi.createNewCourier()
                .then()
                .assertThat()
                .body("message", containsString("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check new courier creation with duplicate")
    public void createNewCourierIfExistsConflict() {
        courierApi.createNewCourier();
        courierApi.createNewCourier()
                .then()
                .assertThat()
                .body("message", containsString("Этот логин уже используется"))
                .and()
                .statusCode(409);
    }

    @After
    public void deleteCourier() {
        courierApi.deleteCourier();
    }
}

