import io.qameta.allure.junit4.DisplayName;
import org.apitesting.api.CourierApi;
import org.apitesting.api.OrderApi;
import org.apitesting.domain.Credential;
import org.apitesting.domain.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class AcceptOrderTest {
    private OrderApi orderApi;
    private Order order;
    private CourierApi courierApi;

    @Before
    public void setUp() {
        orderApi = new OrderApi();
        order = new Order("Sergei", "Ivanov", "Moscow", "Station", "+71234567890", "30", "2019-03-01 00:00:00", "Comment", new String[]{});
        courierApi = new CourierApi();
        courierApi.setCredential(new Credential("courier12345678", "12345678"));
    }

    @Test
    @DisplayName("Check accept new order")
    public void acceptOrderSuccessfully() {
        orderApi.createOrder(order);
        courierApi.createNewCourier();
        Integer courierId = courierApi.loginCourier().then().extract().body().path("id");

        orderApi.acceptOrder(courierId)
                .then()
                .assertThat()
                .body("ok", is(true))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Check accept new order without courier id error")
    public void acceptOrderWithoutCourierIdError() {
        orderApi.createOrder(order);

        orderApi.acceptOrder(null)
                .then()
                .assertThat()
                .body("message", containsString("Недостаточно данных для поиска"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Check accept new order with invalid courier id error")
    public void acceptOrderWithInvalidCourierIdError() {
        orderApi.createOrder(order);

        orderApi.acceptOrder(-1)
                .then()
                .assertThat()
                .body("message", containsString("Курьера с таким id не существует"))
                .and()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Check accept order without order id error")
    public void acceptOrderWithoutOrderIdError() {
        orderApi.acceptOrderWithoutOrderId()
                .then()
                .assertThat()
                .body("message", containsString("Недостаточно данных для поиска"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Check accept order with invalid order id error")
    public void acceptOrderWithInvalidOrderIdError() {
        courierApi.createNewCourier();
        Integer courierId = courierApi.loginCourier().then().extract().body().path("id");
        orderApi.setOrderId(-1);

        orderApi.acceptOrder(courierId)
                .then()
                .assertThat()
                .body("message", containsString("Заказа с таким id не существует"))
                .and()
                .statusCode(SC_NOT_FOUND);
    }

    @After
    public void after() {
        orderApi.cancelOrder();
        courierApi.deleteCourier();
    }
}