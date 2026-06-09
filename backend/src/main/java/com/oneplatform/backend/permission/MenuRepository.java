package com.oneplatform.backend.permission;

import java.util.List;

public interface MenuRepository {

    List<MenuRecord> findMenus();

    MenuRecord findMenuById(Long id);

    boolean existsMenuPermission(String permission, Long excludeId);

    MenuRecord createMenu(MenuMutation mutation);

    MenuRecord updateMenu(Long id, MenuMutation mutation);

    void softDeleteMenu(Long id);

    void updateMenuStatus(Long id, Integer status);

    long countChildren(Long id);

    long countRoleReferences(Long id);
}
