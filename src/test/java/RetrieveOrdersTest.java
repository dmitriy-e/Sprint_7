import io.qameta.allure.junit4.DisplayName;
import org.apitesting.api.OrderApi;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.notNullValue;

public class RetrieveOrdersTest {
    private OrderApi orderApi;

    @Before
    public void setUp() {
        orderApi = new OrderApi();
    }

    @Test
    @DisplayName("Check retrieving of all orders")
    public void retrieveAllOrdersSuccessfully() {
        orderApi.retrieveAllOrders()
                .then()
                .assertThat()
                .body("orders", notNullValue())
                .and()
                .statusCode(SC_OK);
    }
}

