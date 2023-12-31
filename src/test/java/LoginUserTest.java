import utils.RandomUserGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserClient;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static user.UserCreds.credsFrom;

public class LoginUserTest {
    private User user;
    private UserClient userClient = new UserClient();
    private RandomUserGenerator userGenerator = new RandomUserGenerator();
    private String accessToken;

    @Before
    public void setUp() {
        user = new User()
                .setEmail(userGenerator.getEmail())
                .setPassword(userGenerator.getPassword())
                .setName(userGenerator.getName());
        ValidatableResponse response = userClient.createUser(user);
        String accessTokenBearer = response.extract().path("accessToken");
        accessToken = accessTokenBearer.split(" ")[1];
    }

    @Test
    @DisplayName("login user")
    @Description("логин под существующим пользователем")
    public void loginUserTest() {
        ValidatableResponse response = userClient.loginUser(credsFrom(user));
        response
                .body("success", equalTo(true))
                .statusCode(HttpStatus.SC_OK);
        assertEquals("Неверное сообщение",
                user.getEmail(),
                response.extract().path("user.email"));
        assertEquals("Неверное сообщение",
                user.getName(),
                response.extract().path("user.name"));
    }

    @Test
    @DisplayName("login user with incorrect email")
    @Description("логин с неверным логином")
    public void loginUserWithIncorrectEmailTest() {
        String incorrectEmail = userGenerator.getEmail();
        user = user.setEmail(incorrectEmail);
        ValidatableResponse incorrectEmailResponse = userClient.loginUser(credsFrom(user));
        incorrectEmailResponse
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("login user with incorrect password")
    @Description("логин с неверным логином и паролем")
    public void loginUserWithIncorrectPasswordTest() {
        String incorrectPassword = userGenerator.getPassword();
        user = user.setPassword(incorrectPassword);
        ValidatableResponse incorrectPasswordResponse = userClient.loginUser(credsFrom(user));
        incorrectPasswordResponse
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}