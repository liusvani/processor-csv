package com.processor_csv.domain.service;

import com.processor_csv.domain.model.User;
import com.processor_csv.infraestructure.persistence.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final String TEXT_REGEX = "[A-Za-zÁÉÍÓÚáéíóúÑñ ]+";

    public User validate(User user) {
        List<String> errores = new ArrayList<>();
        if (user.getEmail() == null) {
            //throw new ValidationException("Email is empty or invalid: " + user.getEmail());
            errores.add("Email is empty.");
        } else if (!user.getEmail().matches(EMAIL_REGEX)) {
            errores.add("Email is invalid.");

        }

        if (user.getFirstname() == null || user.getFirstname().trim().isEmpty()) {
            errores.add("First name is empty.");
           // throw new IllegalArgumentException("First name is empty");

        } else if(!user.getFirstname().matches(TEXT_REGEX)){
            errores.add("First name is invalid.");
            //throw new IllegalArgumentException("First name is invalid.");

        }

        if (user.getLastname() == null || user.getLastname().trim().isEmpty()) {
            //throw new IllegalArgumentException("Last name is empty");
            errores.add("Last name is empty.");
        } else if(!user.getLastname().matches(TEXT_REGEX)){
            //throw new IllegalArgumentException("Last name is invalid.");
            errores.add("Last name is invalid.");
        }

        if (user.getOccupation() == null || user.getOccupation().trim().isEmpty()) {
            //throw new IllegalArgumentException("Last name is empty");
            errores.add("Occupation is empty.");
        } else if(!user.getOccupation().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")){
            //throw new IllegalArgumentException("Ocupation is invalid.");
            errores.add("Occupation is invalid.");
        }

        if(!isNumber(user.getAge().toString())){
            errores.add("Invalid age.");
        }

        if (!errores.isEmpty()) {

            //return user;
            //registrarErrores(user, errores);
            //System.out.println("Lista de errores: " + String.join("\n", errores));
            throw new ValidationException(String.join("\n", errores));
        }

        return user;
    }
    public boolean isNumber(String valor) {
        try {
            Integer.parseInt(valor);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public void saveAll(List<? extends User> users) {
        users.forEach(this::validate);
        userRepository.saveAll(users);
        users.forEach(user -> {
            // lógica de guardado, por ejemplo:
            System.out.println("Guardando usuario en la BD: " + user.getFirstname());
        });
    }

}

