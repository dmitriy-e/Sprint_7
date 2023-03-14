import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apitesting.Order;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderTest {
    private Order order;
    private Integer track;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        order = new Order("Sergei", "Ivanov", "Moscow", "Station", "+71234567890", "30", "2019-03-01 00:00:00", "Comment", new String[]{});
    }

    public void createOrder() {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .post("/api/v1/orders");

        track = response.then().extract().body().path("track");
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

    @Test
    @DisplayName("Get order successfully")
    public void getOrderSuccessfully() {
        createOrder();

        given().header("Content-type", "application/json")
                .queryParam("t", track)
                .get("/api/v1/orders/track")
                .then()
                .assertThat()
                .body("order.id", notNullValue())
                .and()
                .statusCode(200);

        cancelOrder();
    }

    @Test
    @DisplayName("Get order without track error")
    public void getOrderWithoutTrackError() {
        given().header("Content-type", "application/json")
                .get("/api/v1/orders/track")
                .then()
                .assertThat()
                .body("message", containsString("Недостаточно данных для поиска"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Get order with invalid track error")
    public void getOrderWithInvalidTrackError() {
        given().header("Content-type", "application/json")
                .queryParam("t", 0)
                .get("/api/v1/orders/track")
                .then()
                .assertThat()
                .body("message", containsString("Заказ не найден"))
                .and()
                .statusCode(404);
    }
}
