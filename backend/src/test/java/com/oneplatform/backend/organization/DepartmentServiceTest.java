package com.oneplatform.backend.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.common.PageResponse;

import org.junit.jupiter.api.Test;

class DepartmentServiceTest {

    @Test
    void listBuildsTreeAndKeepsAncestorsWhenFilteringByName() {
        InMemoryDepartmentRepository repository = new InMemoryDepartmentRepository();
        DepartmentService service = new DepartmentService(repository);

        PageResponse<DepartmentResponse> response = service.list(new DepartmentQuery("研发", null));

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.list()).singleElement().satisfies(root -> {
            assertThat(root.name()).isEqualTo("公司总部");
            assertThat(root.type()).isEqualTo("公司");
            assertThat(root.children()).singleElement().satisfies(center -> {
                assertThat(center.name()).isEqualTo("产品研发中心");
                assertThat(center.type()).isEqualTo("中心");
                assertThat(center.children()).singleElement().satisfies(dept -> {
                    assertThat(dept.name()).isEqualTo("研发部");
                    assertThat(dept.type()).isEqualTo("部门");
                });
            });
        });
    }

    @Test
    void disablingDepartmentWithEnabledUsersIsRejected() {
        InMemoryDepartmentRepository repository = new InMemoryDepartmentRepository();
        DepartmentService service = new DepartmentService(repository);

        assertThatThrownBy(() -> service.updateStatus(21L, new DepartmentStatusRequest(0)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("启用用户");
    }

    @Test
    void enablesDepartmentWhenDepartmentExists() {
        InMemoryDepartmentRepository repository = new InMemoryDepartmentRepository();
        DepartmentService service = new DepartmentService(repository);

        service.updateStatus(31L, new DepartmentStatusRequest(1));

        assertThat(repository.updatedStatuses()).containsExactly("31:1");
    }

    static final class InMemoryDepartmentRepository implements DepartmentRepository {

        private final java.util.ArrayList<String> updatedStatuses = new java.util.ArrayList<>();

        @Override
        public List<DepartmentRecord> findAll() {
            LocalDateTime now = LocalDateTime.of(2026, 6, 5, 9, 0);
            return List.of(
                    new DepartmentRecord(1L, null, "公司总部", "HQ", "管理员", "", 1, 1, now, now),
                    new DepartmentRecord(2L, 1L, "产品研发中心", "PRODUCT-RD", "李四", "", 10, 1, now, now),
                    new DepartmentRecord(21L, 2L, "研发部", "RD", "赵六", "", 11, 1, now, now),
                    new DepartmentRecord(22L, 2L, "产品部", "PRODUCT", "王五", "", 12, 1, now, now),
                    new DepartmentRecord(3L, 1L, "交付与运营中心", "DELIVERY-OPS", "周八", "", 20, 1, now, now),
                    new DepartmentRecord(31L, 3L, "交付部", "DELIVERY", "孙七", "", 21, 0, now, now)
            );
        }

        @Override
        public DepartmentRecord findById(Long id) {
            return findAll().stream()
                    .filter(item -> item.id().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public long countEnabledUsers(Long departmentId) {
            return departmentId == 21L ? 2 : 0;
        }

        @Override
        public void updateStatus(Long id, Integer status) {
            updatedStatuses.add("%d:%d".formatted(id, status));
        }

        List<String> updatedStatuses() {
            return updatedStatuses;
        }
    }
}
