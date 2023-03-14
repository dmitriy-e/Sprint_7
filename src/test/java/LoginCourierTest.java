import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apitesting.Credential;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Check login with credentials")
    public void loginCourierSuccessfully() {
        Credential credential = new Credential("ninja12523423dqdqdqdqdqdqdqdqdqdqdqt32", "123t32t3t");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(credential)
                        .when()
                        .post("/api/v1/courier/login");

        response.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Check login without password")
    public void loginCourierWithoutAllRequiredFieldsFails() {
        Credential credential = new Credential("ninja12523423dqdqdqdqdqdqdqdqdqdqdqt32", "");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(credential)
                        .when()
                        .post("/api/v1/courier/login");

        response.then().assertThat().body("message", containsString("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check login with invalid credentials")
    public void loginCourierWithInvalidCredentialsFails() {
        Credential credential = new Credential("notFoundLogin", "notFoundPassword");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(credential)
                        .when()
                        .post("/api/v1/courier/login");

        response.then().assertThat().body("message", containsString("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }
}

