package ru.otus.java.chat.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryAuthenticatedProvider implements AuthenticatedProvider {
    private class User {
        private String login;
        private String password;
        private String userName;
        private String role;

        public User(String login, String password, String userName) {
            this.login = login;
            this.password = password;
            this.userName = userName;
            if (userName.equals("abc")) {
                this.role = "admin";
                return;
            }
            this.role = "user";
        }

        public String getRole() {
            return role;
        }
    }

    private Server server;

    private List<User> users;

    public InMemoryAuthenticatedProvider(Server server) {
        this.server = server;
        this.users = new CopyOnWriteArrayList<>();
        this.users.add(new User("qwe", "qwe", "qwe1"));
        this.users.add(new User("asd", "asd", "asd1"));
        this.users.add(new User("zxc", "zxc", "zxc1"));
    }


    @Override
    public void initialize() {
        System.out.println("initialize InMemoryAuthenticatedProvider");

    }

    private String getUserNameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.userName;
            }
        }
        return null;
    }

    private boolean isLoginAllReadyExists(String login) {
        for (User user : users) {
            if (user.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserNameAllReadyExists(String userName) {
        for (User user : users) {
            if (user.userName.equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRole(String userName) {
        for (User user : users) {
            if (user.userName.equals(userName) && user.role.equalsIgnoreCase("admin")) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean authenticate(ClientHandler clientHandler, String login, String password) {
        String authUsername = getUserNameByLoginAndPassword(login, password);
        if (authUsername == null) {
            clientHandler.sendMessage("Некорректный логин/пароль");
            return false;
        }

        if (server.isUserNameBusy(authUsername)) {
            clientHandler.sendMessage("Данная учетная запись уже занята");
            return false;
        }

        clientHandler.setUserName(authUsername);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/authok " + authUsername);
        return true;
    }

    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String userName) {
        if (login.trim().length() < 3 || password.trim().length() < 3 || userName.trim().length() < 3) {
            clientHandler.sendMessage("Вы ввели некорректные данные");
            return false;
        }

        if (isLoginAllReadyExists(login)) {
            clientHandler.sendMessage("Указанный логин уже занят");
            return false;
        }

        if (isUserNameAllReadyExists(userName)) {
            clientHandler.sendMessage("Указанное имя пользователя уже занято");
            return false;
        }

        users.add(new User(login, password, userName));
        clientHandler.setUserName(userName);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/regok " + userName);
        return true;
    }

    @Override
    public boolean kick(String userName) {
        if (isRole(userName)) {
            for (User user : users) {
                if (user.userName.equals(userName)) {
                    users.remove(user);
                    return true;
                }
            }
        }
        return false;
    }


}
