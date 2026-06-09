package com.oneplatform.backend.portal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import com.oneplatform.backend.common.BusinessException;
import com.oneplatform.backend.portal.dto.CopyCredentialRequest;
import com.oneplatform.backend.portal.dto.PortalActionLogRequest;
import com.oneplatform.backend.portal.dto.PortalProjectDetailResponse;
import com.oneplatform.backend.portal.dto.PortalProjectQuery;

import org.junit.jupiter.api.Test;

class PortalServiceTest {

    @Test
    void listsOnlyEnabledProjectsVisibleToCurrentUser() {
        InMemoryPortalRepository repository = new InMemoryPortalRepository();
        PortalService service = new PortalService(repository, new PlainCredentialSecretService(), new TestPortalUserResolver(), "127.0.0.1");

        List<PortalProjectDetailResponse.ProjectCard> projects = service.listProjects(new PortalProjectQuery("", "", ""), "");

        assertThat(projects)
                .extracting(PortalProjectDetailResponse.ProjectCard::id)
                .containsExactly(1L, 3L, 5L);
    }

    @Test
    void detailReturnsMaskedCredentialsAndAggregatedPermissionFlags() {
        InMemoryPortalRepository repository = new InMemoryPortalRepository();
        PortalService service = new PortalService(repository, new PlainCredentialSecretService(), new TestPortalUserResolver(), "127.0.0.1");

        PortalProjectDetailResponse detail = service.getProjectDetail(1L, "");

        assertThat(detail.project().name()).isEqualTo("公司统一门户");
        assertThat(detail.credentials()).singleElement().satisfies(credential -> {
            assertThat(credential.username()).isEqualTo("portal_demo");
            assertThat(credential.passwordMasked()).isEqualTo("************");
        });
        assertThat(detail.canViewPassword()).isTrue();
        assertThat(detail.canCopyPassword()).isFalse();
        assertThat(detail.canViewQrcode()).isTrue();
        assertThat(repository.recentVisits()).containsExactly("1:1");
    }

    @Test
    void revealPasswordRejectsUserWithoutPasswordViewPermission() {
        InMemoryPortalRepository repository = new InMemoryPortalRepository();
        PortalService service = new PortalService(repository, new PlainCredentialSecretService(), new TestPortalUserResolver(), "127.0.0.1");

        assertThatThrownBy(() -> service.revealCredential(2L, ""))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(403);
    }

    @Test
    void copyPasswordRequiresPermissionAndWritesOperationLogBeforeReturningSecret() {
        InMemoryPortalRepository repository = new InMemoryPortalRepository();
        PortalService service = new PortalService(repository, new PlainCredentialSecretService(), new TestPortalUserResolver(), "127.0.0.1");

        String copied = service.copyCredential(3L, new CopyCredentialRequest("password"), "");

        assertThat(copied).isEqualTo("secret:ops");
        assertThat(repository.operationLogs())
                .containsExactly("1:5:复制密码:监控账号:成功");
    }

    @Test
    void recordsPortalActionWithoutSensitivePlainText() {
        InMemoryPortalRepository repository = new InMemoryPortalRepository();
        PortalService service = new PortalService(repository, new PlainCredentialSecretService(), new TestPortalUserResolver(), "127.0.0.1");

        boolean recorded = service.recordAction(new PortalActionLogRequest(1L, "复制地址", "正式地址"), "");

        assertThat(recorded).isTrue();
        assertThat(repository.operationLogs())
                .containsExactly("1:1:复制地址:正式地址:成功");
    }
}
