package com.oneplatform.backend.permission;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.oneplatform.backend.user.entity.SysMenu;
import com.oneplatform.backend.user.entity.SysRole;
import com.oneplatform.backend.user.mapper.MenuMapper;
import com.oneplatform.backend.user.mapper.RoleMapper;
import com.oneplatform.backend.user.mapper.RoleMenuMapper;
import com.oneplatform.backend.user.mapper.UserRoleMapper;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MybatisPermissionRepository implements RoleRepository, MenuRepository {

    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;

    public MybatisPermissionRepository(
            RoleMapper roleMapper,
            MenuMapper menuMapper,
            RoleMenuMapper roleMenuMapper,
            UserRoleMapper userRoleMapper
    ) {
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<RoleRecord> findRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getDeleted, 0)
                        .orderByAsc(SysRole::getSort)
                        .orderByAsc(SysRole::getId))
                .stream()
                .map(this::mapRole)
                .toList();
    }

    @Override
    public RoleRecord findRoleById(Long id) {
        SysRole role = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getId, id)
                .eq(SysRole::getDeleted, 0)
                .last("LIMIT 1"));
        return role == null ? null : mapRole(role);
    }

    @Override
    public boolean existsRoleCode(String code, Long excludeId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getCode, code)
                .eq(SysRole::getDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }
        return roleMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional
    public RoleRecord createRole(RoleMutation mutation) {
        SysRole role = new SysRole();
        applyRoleMutation(role, mutation);
        role.setIsSystem(0);
        role.setDeleted(0);
        roleMapper.insert(role);
        replaceRoleMenus(role.getId(), mutation.menuIds());
        return findRoleById(role.getId());
    }

    @Override
    @Transactional
    public RoleRecord updateRole(Long id, RoleMutation mutation) {
        SysRole role = new SysRole();
        role.setId(id);
        applyRoleMutation(role, mutation);
        roleMapper.updateById(role);
        replaceRoleMenus(id, mutation.menuIds());
        return findRoleById(id);
    }

    @Override
    public void softDeleteRole(Long id) {
        roleMapper.update(null, new LambdaUpdateWrapper<SysRole>()
                .eq(SysRole::getId, id)
                .set(SysRole::getDeleted, 1));
    }

    @Override
    public void updateRoleStatus(Long id, Integer status) {
        roleMapper.update(null, new LambdaUpdateWrapper<SysRole>()
                .eq(SysRole::getId, id)
                .eq(SysRole::getDeleted, 0)
                .set(SysRole::getStatus, status));
    }

    @Override
    public List<Long> findRoleMenuIds(Long roleId) {
        return roleMenuMapper.findMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public void replaceRoleMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        for (Long menuId : menuIds) {
            roleMenuMapper.insertRoleMenu(roleId, menuId);
        }
    }

    @Override
    public long countRoleUsers(Long roleId) {
        return userRoleMapper.countByRoleId(roleId);
    }

    @Override
    public List<Long> findExistingMenuIds(List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return List.of();
        }
        return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                        .select(SysMenu::getId)
                        .in(SysMenu::getId, menuIds)
                        .eq(SysMenu::getDeleted, 0))
                .stream()
                .map(SysMenu::getId)
                .toList();
    }

    @Override
    public List<MenuRecord> findMenus() {
        return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getDeleted, 0)
                        .orderByAsc(SysMenu::getSort)
                        .orderByAsc(SysMenu::getId))
                .stream()
                .map(this::mapMenu)
                .toList();
    }

    @Override
    public MenuRecord findMenuById(Long id) {
        SysMenu menu = menuMapper.selectOne(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getId, id)
                .eq(SysMenu::getDeleted, 0)
                .last("LIMIT 1"));
        return menu == null ? null : mapMenu(menu);
    }

    @Override
    public boolean existsMenuPermission(String permission, Long excludeId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getPermission, permission)
                .eq(SysMenu::getDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(SysMenu::getId, excludeId);
        }
        return menuMapper.selectCount(wrapper) > 0;
    }

    @Override
    public MenuRecord createMenu(MenuMutation mutation) {
        SysMenu menu = new SysMenu();
        applyMenuMutation(menu, mutation);
        menu.setDeleted(0);
        menuMapper.insert(menu);
        return findMenuById(menu.getId());
    }

    @Override
    public MenuRecord updateMenu(Long id, MenuMutation mutation) {
        SysMenu menu = new SysMenu();
        menu.setId(id);
        applyMenuMutation(menu, mutation);
        menuMapper.updateById(menu);
        return findMenuById(id);
    }

    @Override
    public void softDeleteMenu(Long id) {
        menuMapper.update(null, new LambdaUpdateWrapper<SysMenu>()
                .eq(SysMenu::getId, id)
                .set(SysMenu::getDeleted, 1));
    }

    @Override
    public void updateMenuStatus(Long id, Integer status) {
        menuMapper.update(null, new LambdaUpdateWrapper<SysMenu>()
                .eq(SysMenu::getId, id)
                .eq(SysMenu::getDeleted, 0)
                .set(SysMenu::getStatus, status));
    }

    @Override
    public long countChildren(Long id) {
        return menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, id)
                .eq(SysMenu::getDeleted, 0));
    }

    @Override
    public long countRoleReferences(Long id) {
        return roleMenuMapper.countByMenuId(id);
    }

    private RoleRecord mapRole(SysRole role) {
        return new RoleRecord(
                role.getId(),
                role.getCode(),
                role.getName(),
                role.getDescription(),
                role.getSort(),
                role.getStatus(),
                role.getIsSystem(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    private MenuRecord mapMenu(SysMenu menu) {
        return new MenuRecord(
                menu.getId(),
                menu.getParentId(),
                menu.getName(),
                menu.getTitle(),
                menu.getType(),
                menu.getPath(),
                menu.getComponent(),
                menu.getPermission(),
                menu.getIcon(),
                menu.getSort(),
                menu.getStatus(),
                menu.getCreatedAt(),
                menu.getUpdatedAt()
        );
    }

    private void applyRoleMutation(SysRole role, RoleMutation mutation) {
        role.setCode(mutation.code());
        role.setName(mutation.name());
        role.setDescription(mutation.description());
        role.setSort(mutation.sort());
        role.setStatus(mutation.status());
    }

    private void applyMenuMutation(SysMenu menu, MenuMutation mutation) {
        menu.setParentId(mutation.parentId());
        menu.setName(mutation.name());
        menu.setTitle(mutation.title());
        menu.setType(mutation.type());
        menu.setPath(mutation.path());
        menu.setComponent(mutation.component());
        menu.setPermission(mutation.permission());
        menu.setIcon(mutation.icon());
        menu.setSort(mutation.sort());
        menu.setStatus(mutation.status());
    }
}
