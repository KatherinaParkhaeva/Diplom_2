package user;

import io.qameta.allure.Step;

public class UserCreds {
    private final String email;
    private final String password;

    public UserCreds(String login, String password) {
        this.email = login;
        this.password = password;
    }

    @Step("Получение учетных данных пользователя")
    public static UserCreds credsFrom(User user) {
        return new UserCreds(user.getEmail(), user.getPassword());
    }
}