package com.policene.voluntech.models.entities;

import com.policene.voluntech.models.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public static User create(UserRole role) {
        return switch (role) {
            case VOLUNTEER -> new Volunteer();
            case ORGANIZATION -> new Organization();
            default -> throw new IllegalArgumentException("Invalid user role.");
        };
    }

}
