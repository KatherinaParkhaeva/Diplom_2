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

public class CreateUserTest {
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
    }

    @Test
    @DisplayName("create new user")
    @Description("создать уникального пользователя")
    public void createNewUser() {
        ValidatableResponse response = userClient.createUser(user);
        String accessTokenBearer = response.extract().path("accessToken");
        accessToken = accessTokenBearer.split(" ")[1];
        response
                .body("success", equalTo(true))
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("create two equal users")
    @Description("создать пользователя, который уже зарегистрирован")
    public void createTwoEqualUsers() {
        ValidatableResponse response1 = userClient.createUser(user);
        String accessTokenBearer = response1.extract().path("accessToken");
        accessToken = accessTokenBearer.split(" ")[1];
        ValidatableResponse response2 = userClient.createUser(user);
        response2
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"))
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("create user without email")
    @Description("создать пользователя и не заполнить обязательное поле email")
    public void createUserWithoutEmail() {
        user = user.setEmail("");
        ValidatableResponse response = userClient.createUser(user);
        response
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("create user without password")
    @Description("создать пользователя и не заполнить обязательное поле password")
    public void createUserWithoutPassword() {
        user = user.setPassword("");
        ValidatableResponse response = userClient.createUser(user);
        response
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("create user without name")
    @Description("создать пользователя и не заполнить обязательное поле name")
    public void createUserWithoutName() {
        user = user.setName("");
        ValidatableResponse response = userClient.createUser(user);
        response
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}