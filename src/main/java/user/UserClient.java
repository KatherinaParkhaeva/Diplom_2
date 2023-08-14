package user;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient {
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    public static final String REGISTER_PATH = "/api/auth/register";
    public static final String LOGIN_PATH = "/api/auth/login";
    public static final String USER_PATH = "/api/auth/user";
    private User user;
    private String accessToken;
    private static UserCreds userCreds;

    public UserClient() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(UserCreds userCreds) {
        return given()
                .header("Content-type", "application/json")
                .body(userCreds)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("получение данных о пользователе")
    public ValidatableResponse getUser(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .get(USER_PATH)
                .then();
    }

    @Step("обновление данных о пользователе")
    public ValidatableResponse patchUser(String newUserInfo, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .body(newUserInfo)
                .when()
                .patch(USER_PATH)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .delete(USER_PATH)
                .then();
    }
}