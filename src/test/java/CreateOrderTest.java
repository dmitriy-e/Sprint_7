import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private final String[] color;
    private Integer track;

    public CreateOrderTest(String[] color) {
        this.color = color;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK, GREY"}},
                {new String[]{}},
        };
    }

    @Test
    @DisplayName("Check new order")
    public void createOrderSuccessfully() {
        Order order = new Order("Sergei", "Ivanov", "Moscow", "Station", "+71234567890", "30", "2019-03-01 00:00:00", "Comment", color);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .when()
                        .post("/api/v1/orders");

        response.then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);

        track = response.then().extract().body().path("track");
    }

    @After
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
}