import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apitesting.domain.Courier;
import org.apitesting.domain.Credential;
import org.apitesting.domain.Order;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class AcceptOrderTest {
    private Order order;
    private static Credential credential;
    private Courier courier;
    private Integer track;
    private Integer courierId;
    private Integer orderId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        order = new Order("Sergei", "Ivanov", "Moscow", "Station", "+71234567890", "30", "2019-03-01 00:00:00", "Comment", new String[]{});
        credential = new Credential("courier12345678", "12345678");
        courier = new Courier(credential.getLogin(), credential.getPassword(), "Alexey");
    }

    public void createOrder() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .post("/api/v1/orders");

        track = response.then().extract().body().path("track");
        orderId = given().header("Content-type", "application/json").queryParam("t", track).get("/api/v1/orders/track")
                .then().extract().body().path("order.id");
    }

    public void createNewCourier() {
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");

        courierId = given().header("Content-type", "application/json").and().body(credential).when().post("/api/v1/courier/login")
                .then().extract().body().path("id");
    }

    public void cancelOrder() {
        if (track != null) {
            ObjectNode trackJsonObject = null;
            try {
                ObjectMapper mapper = new ObjectMapper();

                trackJsonObject = mapper.createObjectNode();
                trackJsonObject.put("track", track);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(trackJsonObject)
                    .when()
                    .put("/api/v1/orders/cancel");
        }
    }

    public static void deleteCourier() {
        Integer courierId = given().header("Content-type", "application/json").and().body(credential).when().post("/api/v1/courier/login")
                .then().extract().body().path("id");

        if (courierId != null) {
            given()
                    .header("Content-type", "application/json")
                    .delete(String.format("/api/v1/courier/%d", courierId));
        }
    }

    @Test
    @DisplayName("Check accept new order")
    public void acceptOrderSuccessfully() {
        createOrder();
        createNewCourier();

        given()
                .header("Content-type", "application/json")
                .and()
                .queryParam("courierId", courierId)
                .put(String.format("/api/v1/orders/accept/%d", orderId))
                .then()
                .assertThat()
                .body("ok", is(true))
                .and()
                .statusCode(200);

        cancelOrder();
        deleteCourier();
    }

    @Test
    @DisplayName("Check accept new order without courier id error")
    public void acceptOrderWithoutCourierIdError() {
        createOrder();

        given()
                .header("Content-type", "application/json")
                .and()
                .put(String.format("/api/v1/orders/accept/%d", orderId))
                .then()
                .assertThat()
                .body("message", containsString("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);

        cancelOrder();
    }

    @Test
    @DisplayName("Check accept new order with invalid courier id error")
    public void acceptOrderWithInvalidCourierIdError() {
        createOrder();

        given()
                .header("Content-type", "application/json")
                .and()
                .queryParam("courierId", -1)
                .put(String.format("/api/v1/orders/accept/%d", orderId))
                .then()
                .assertThat()
                .body("message", containsString("Курьера с таким id не существует"))
                .and()
                .statusCode(404);

        cancelOrder();
    }

    @Test
    @DisplayName("Check accept order without order id error")
    public void acceptOrderWithoutOrderIdError() {
        given()
                .header("Content-type", "application/json")
                .and()
                .put("/api/v1/orders/accept/:id")
                .then()
                .assertThat()
                .body("message", containsString("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Check accept order with invalid order id error")
    public void acceptOrderWithInvalidOrderIdError() {
        createNewCourier();

        given()
                .header("Content-type", "application/json")
                .and()
                .queryParam("courierId", courierId)
                .put(String.format("/api/v1/orders/accept/%d", -1))
                .then()
                .assertThat()
                .body("message", containsString("Заказа с таким id не существует"))
                .and()
                .statusCode(404);

        deleteCourier();
    }
}