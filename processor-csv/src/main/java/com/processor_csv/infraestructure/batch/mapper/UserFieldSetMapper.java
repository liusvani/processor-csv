package com.processor_csv.infraestructure.batch.mapper;

import com.processor_csv.domain.model.User;
import com.processor_csv.infraestructure.batch.listener.UserSkipListener;
import jakarta.validation.ValidationException;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class UserFieldSetMapper implements FieldSetMapper<User> {

    private UserSkipListener skipListener;


    // 💡 Inyección vía setter para evitar ciclos
    public void setSkipListener(UserSkipListener skipListener) {
        this.skipListener = skipListener;
    }

    @Override
    public User mapFieldSet(FieldSet fieldSet) {
        Map<String, String> errores = new LinkedHashMap<>();
        String email;
        String firstname = fieldSet.readString("firstname");
        String lastname = fieldSet.readString("lastname");
        Integer age = Integer.valueOf(fieldSet.readString("age"));
         email = fieldSet.readString("email");
        String occupation = fieldSet.readString("occupation");
        //System.err.println("Edad: " + age);


        if (firstname.isEmpty()) {
            firstname = "N/A";
            errores.put("firstname", "First name is empty.");
        } else if (!firstname.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) {
            errores.put("firstname", "First name is invalid.");
        }

        if (lastname.isEmpty()) {
            lastname = "N/A";
            errores.put("lastname", "Last name name is empty.");
        } else if (!lastname.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) {
            errores.put("lastname", "Last name name is invalid.");
        }

        if (age.toString().isEmpty()) {
            age = 0;
        } else if (age < 1) {
            errores.put("age","Age is invalid.");
        }

        if (email.isEmpty()) {
            email = "N/A";
            errores.put("email", "Email is empty.");
        } else if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
            errores.put("email", "Invalid email.");
        }

        if (occupation.isEmpty() || occupation.trim().isEmpty()) {
            errores.put("occupation", "Occupation is empty.");
        }

        if (!errores.isEmpty()) {
            User userParcial = new User(firstname, lastname, age, email, occupation);
            if (skipListener != null) {
                skipListener.registrarError(userParcial, errores);
            }
            throw new ValidationException("Errores de validación: " + errores.toString());
        }

        return new User(firstname, lastname, age, email, occupation);

    }
}
