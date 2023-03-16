import io.qameta.allure.junit4.DisplayName;
import org.apitesting.api.OrderApi;
import org.apitesting.domain.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private OrderApi orderApi;
    private final String[] color;

    public CreateOrderTest(String[] color) {
        this.color = color;
    }

    @Before
    public void setUp() {
        orderApi = new OrderApi();
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
        orderApi.createOrder(order)
                .then()
                .assertThat()
                .body("track", notNullValue())
                .and()
                .statusCode(SC_CREATED);
    }

    @After
    public void cancelOrder() {
        orderApi.cancelOrder();
    }
}