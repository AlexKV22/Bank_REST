package com.example.bankcards.exception;

import com.example.bankcards.util.RoleName;

public class RoleNotFoundException extends RuntimeException {
    private final RoleName roleName;

    public RoleNotFoundException(RoleName roleName) {
        super("Роль не найдена");
        this.roleName = roleName;
    }

    public RoleName getRoleName() {
        return roleName;
    }
}
