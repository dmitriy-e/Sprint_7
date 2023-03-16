package org.apitesting.api;

import io.restassured.response.Response;
import org.apitesting.domain.Courier;
import org.apitesting.domain.Credential;

import static io.restassured.RestAssured.given;
import static org.apitesting.routes.CourierRoutes.BASIC_ROUTE;
import static org.apitesting.routes.CourierRoutes.LOGIN_ROUTE;

public class CourierApi extends BaseApi {
    private Credential credential;

    public Response createNewCourier() {
        Courier courier = new Courier(credential.getLogin(), credential.getPassword(), "Alexey");
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(BASIC_ROUTE);
    }

    public Response loginCourier() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(credential)
                .when()
                .post(LOGIN_ROUTE);
    }

    public Response deleteCourier() {
        Integer courierId = given().header("Content-type", "application/json").and().body(credential).when().post("/api/v1/courier/login")
                .then().extract().body().path("id");

        if (courierId != null) {
            return given()
                    .header("Content-type", "application/json")
                    .and()
                    .when()
                    .delete(String.format(String.join(BASIC_ROUTE, "", "/%d"), courierId));
        }
        return null;
    }

    public Response deleteCourierWithId(String courierId) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(String.format("{\"id\": %s}", courierId))
                .delete(String.format(String.join(BASIC_ROUTE, "", "/%s"), courierId));
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public Credential getCredential() {
        return credential;
    }
}
