package org.apitesting.api;

import io.restassured.response.Response;
import org.apitesting.domain.Order;

import static io.restassured.RestAssured.given;
import static org.apitesting.routes.OrderRoutes.*;

public class OrderApi extends BaseApi {
    private Integer track;
    private Integer orderId;

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public void setTrack(Integer track) {
        this.track = track;
    }

    public Response createOrder(Order order) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .post(BASIC_ROUTE);

        track = response.then().extract().body().path("track");
        orderId = given().header("Content-type", "application/json").queryParam("t", track).get(TRACK_ROUTE)
                .then().extract().body().path("order.id");

        return response;
    }

    public void cancelOrder() {
        if (track != null) {
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(String.format("{\"track\": %d}", track))
                    .when()
                    .put(CANCEL_ROUTE);
        }
    }

    public Response acceptOrder(Integer courierId) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .queryParam("courierId", courierId)
                .put(String.format(String.join(ACCEPT_ROUTE, "", "/%d"), orderId));
    }

    public Response acceptOrderWithoutOrderId() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .put(String.join(ACCEPT_ROUTE, "", "/:id"));
    }

    public Response getOrder() {
        return given()
                .header("Content-type", "application/json")
                .queryParam("t", track)
                .get(TRACK_ROUTE);
    }

    public Response getOrderWithoutTrack() {
        return given()
                .header("Content-type", "application/json")
                .get(TRACK_ROUTE);
    }

    public Response retrieveAllOrders() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .get(BASIC_ROUTE);
    }
}