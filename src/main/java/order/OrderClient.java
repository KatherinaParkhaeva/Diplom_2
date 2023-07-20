package order;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class OrderClient {
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    public static final String INGREDIENTS_PATH = "/api/ingredients";
    public static final String ORDERS_PATH = "/api/orders";
    private Order order;
    private ValidatableResponse ingredientsResponse;
    private ArrayList<String> availableIds;
    private ArrayList<String> orderIds;

    public OrderClient() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Получение данных об ингредиентах")
    public ValidatableResponse getIngredients() {
        return given()
                .when()
                .get(INGREDIENTS_PATH)
                .then();
    }

    @Step("Получение хэшей доступных ингредиентов")
    public ArrayList<String> getAvailableIds() {
        return getIngredients().extract().path("data._id");
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Получение заказа конкретного пользователя")
    public ValidatableResponse getOrdersFromUser(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .when()
                .get(ORDERS_PATH)
                .then();
    }

    @Step("Получение хэшей заказов")
    public ArrayList<String> getAvailableIds(String accessToken) {
        return getOrdersFromUser(accessToken).extract().path("orders._id");
    }
}