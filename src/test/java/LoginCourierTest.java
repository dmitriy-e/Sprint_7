import io.qameta.allure.junit4.DisplayName;
import org.apitesting.api.CourierApi;
import org.apitesting.domain.Credential;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {
    private CourierApi courierApi;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        courierApi.setCredential(new Credential("ninja12523423dqdqdqdqdqdqdqdqdqdqdqt32", "123t32t3t"));
    }

    @Test
    @DisplayName("Check login with credentials")
    public void loginCourierSuccessfully() {
        courierApi.loginCourier()
                .then()
                .assertThat()
                .body("id", notNullValue())
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Check login without password")
    public void loginCourierWithoutAllRequiredFieldsFails() {
        courierApi.setCredential(new Credential("ninja12523423dqdqdqdqdqdqdqdqdqdqdqt32", ""));
        courierApi.loginCourier()
                .then()
                .assertThat()
                .body("message", containsString("Недостаточно данных для входа"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Check login with invalid credentials")
    public void loginCourierWithInvalidCredentialsFails() {
        courierApi.setCredential(new Credential("notFoundLogin", "notFoundPassword"));
        courierApi.loginCourier()
                .then()
                .assertThat()
                .body("message", containsString("Учетная запись не найдена"))
                .and()
                .statusCode(SC_NOT_FOUND);
    }
}

