package com.example.bankcards.service.roleService;

import com.example.bankcards.entity.Role;
import com.example.bankcards.util.RoleName;

public interface RoleService {
    Role findByName(RoleName roleName);
}
