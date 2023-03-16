import io.qameta.allure.junit4.DisplayName;
import org.apitesting.api.OrderApi;
import org.apitesting.domain.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderTest {
    private OrderApi orderApi;
    private Order order;

    @Before
    public void setUp() {
        orderApi = new OrderApi();
        order = new Order("Sergei", "Ivanov", "Moscow", "Station", "+71234567890", "30", "2019-03-01 00:00:00", "Comment", new String[]{});
    }

    @Test
    @DisplayName("Get order successfully")
    public void getOrderSuccessfully() {
        orderApi.createOrder(order);

        orderApi.getOrder()
                .then()
                .assertThat()
                .body("order.id", notNullValue())
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Get order without track error")
    public void getOrderWithoutTrackError() {
        orderApi.getOrderWithoutTrack()
                .then()
                .assertThat()
                .body("message", containsString("Недостаточно данных для поиска"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Get order with invalid track error")
    public void getOrderWithInvalidTrackError() {
        orderApi.setTrack(0);
        orderApi.getOrder()
                .then()
                .assertThat()
                .body("message", containsString("Заказ не найден"))
                .and()
                .statusCode(SC_NOT_FOUND);
    }

    @After
    public void cancelOrder() {
        orderApi.cancelOrder();
    }
}
