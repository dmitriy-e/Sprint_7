import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apitesting.api.CourierApi;
import org.apitesting.domain.Credential;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class DeleteCourierTest {
    private CourierApi courierApi;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        courierApi.setCredential(new Credential("courier12345678", "12345678"));
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Check deleting courier")
    public void deleteCourierSuccessfully() {
        courierApi.createNewCourier();

        courierApi.deleteCourier()
                .then()
                .assertThat().body("ok", is(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Check deleting courier without id error")
    public void deleteCourierWithoutIdError() {
        courierApi.deleteCourierWithId("")
                .then()
                .assertThat().body("message", notNullValue())
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check deleting courier with invalid id error")
    public void deleteCourierWithInvalidId() {
        courierApi.deleteCourierWithId("-1")
                .then()
                .assertThat()
                .body("message", containsString("Курьера с таким id нет"))
                .and()
                .statusCode(404);
    }
}

