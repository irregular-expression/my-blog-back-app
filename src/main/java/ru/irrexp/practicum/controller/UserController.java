package ru.irrexp.practicum.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.irrexp.practicum.model.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            User user = new User();
            user.setId(i);
            user.setFirstName("UserName #" + i);
            user.setLastName("LastName #" + i);
            user.setAge(25);
            user.setActive(true);

            users.add(user);
        }

        return users;
    }

}
