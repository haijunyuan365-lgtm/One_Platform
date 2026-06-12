<template>
  <header class="app-header" :class="{ scrolled }">
    <div class="header-inner">
      <RouterLink to="/" class="brand-link">
        <span class="brand-mark">
          <ElIcon><Monitor /></ElIcon>
        </span>
        <span class="brand-copy">
          <strong>公司一体化平台</strong>
          <small>Project Portal</small>
        </span>
      </RouterLink>

      <div class="header-spacer"></div>

      <div class="platform-state">
        <span></span>
        内部门户
      </div>

      <div class="header-actions">
        <template v-if="authStore.isLoggedIn">
          <div ref="userMenuRef" class="user-menu">
            <button type="button" class="user-trigger" @click.stop="userMenuOpen = !userMenuOpen">
              <span class="avatar">
                <img v-if="authStore.user?.avatar" :src="authStore.user.avatar" :alt="authStore.user.name" />
                <span v-else>{{ userInitial }}</span>
              </span>
              <span class="user-meta">
                <strong>{{ authStore.user?.name }}</strong>
                <small>{{ authStore.user?.role || 'portal user' }}</small>
              </span>
              <ElIcon class="arrow-icon" :class="{ open: userMenuOpen }"><ArrowDown /></ElIcon>
            </button>

            <Transition name="menu-pop">
              <div v-if="userMenuOpen" class="user-dropdown">
                <div class="dropdown-profile">
                  <span class="avatar avatar-large">
                    <img v-if="authStore.user?.avatar" :src="authStore.user.avatar" :alt="authStore.user.name" />
                    <span v-else>{{ userInitial }}</span>
                  </span>
                  <div>
                    <strong>{{ authStore.user?.name }}</strong>
                    <small>{{ authStore.user?.email }}</small>
                  </div>
                </div>
                <button type="button" class="dropdown-item">
                  <ElIcon><User /></ElIcon>
                  个人中心
                </button>
                <button type="button" class="dropdown-item">
                  <ElIcon><Setting /></ElIcon>
                  账号设置
                </button>
                <button type="button" class="dropdown-item danger" @click="handleLogout">
                  <ElIcon><SwitchButton /></ElIcon>
                  退出登录
                </button>
              </div>
            </Transition>
          </div>
        </template>

        <template v-else>
          <button type="button" class="text-btn" @click="loginModalOpen = true">登录</button>
          <button type="button" class="admin-btn" @click="openAdmin()">
            <ElIcon><Connection /></ElIcon>
            管理后台
          </button>
        </template>
      </div>
    </div>
  </header>

  <LoginModal v-model="loginModalOpen" />
</template>

<script setup lang="ts">
import { computed, inject, onMounted, onUnmounted, ref, watch } from 'vue'
import type { Ref } from 'vue'
import {
  ArrowDown,
  Connection,
  Monitor,
  Setting,
  SwitchButton,
  User,
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import LoginModal from '@/components/common/LoginModal.vue'

const authStore = useAuthStore()
const scrolled = ref(false)
const loginModalOpen = ref(false)
const userMenuOpen = ref(false)
const userMenuRef = ref<HTMLElement>()
const adminUrl = import.meta.env.VITE_ADMIN_URL || '/admin'

const userInitial = computed(() => authStore.user?.name?.slice(0, 1) || '用')

const scrollY = inject<Ref<number>>('scrollY')
if (scrollY) {
  watch(scrollY, (val) => {
    scrolled.value = val > 20
  })
}

function onClickOutside(e: MouseEvent) {
  if (userMenuRef.value && !userMenuRef.value.contains(e.target as Node)) {
    userMenuOpen.value = false
  }
}

function openAdmin() {
  window.open(adminUrl, '_blank', 'noopener,noreferrer')
}

async function handleLogout() {
  await authStore.logout()
  userMenuOpen.value = false
}

onMounted(() => document.addEventListener('click', onClickOutside))
onUnmounted(() => document.removeEventListener('click', onClickOutside))
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 50;
  border-bottom: 1px solid transparent;
  background: rgba(246, 245, 244, 0.8);
  backdrop-filter: blur(18px);
  transition:
    border-color 0.2s ease,
    background 0.2s ease,
    box-shadow 0.2s ease;
}

.app-header.scrolled {
  border-color: var(--portal-hairline);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: var(--portal-shadow-soft);
}

.header-inner {
  width: min(var(--portal-max-width), calc(100% - 32px));
  height: 72px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 18px;
}

.brand-link,
.user-trigger,
.admin-btn,
.text-btn,
.dropdown-item {
  text-decoration: none;
}

.brand-link {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  min-width: 232px;
  color: var(--portal-ink-soft);
}

.brand-mark {
  position: relative;
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  border: 1px solid var(--portal-hairline);
  border-radius: 12px;
  color: var(--portal-ink-soft);
  background: #fff;
  box-shadow: var(--portal-shadow-soft);
}

.brand-mark::after {
  content: '';
  position: absolute;
  top: 7px;
  right: 7px;
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: var(--portal-primary);
  box-shadow: 0 0 0 5px rgba(0, 117, 222, 0.1);
}

.brand-copy {
  display: grid;
  gap: 3px;
}

.brand-copy strong {
  color: var(--portal-ink);
  font-size: 15px;
  line-height: 1.2;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.brand-copy small,
.user-meta small,
.dropdown-profile small {
  color: var(--portal-faint);
  font-size: 12px;
}

.header-spacer {
  flex: 1;
  min-width: 16px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.platform-state {
  height: 34px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  border: 1px solid rgba(0, 117, 222, 0.14);
  border-radius: 999px;
  color: var(--portal-primary);
  background: rgba(255, 255, 255, 0.88);
  font-size: 12px;
  font-weight: 600;
  box-shadow: var(--portal-shadow-soft);
}

.platform-state span {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: var(--portal-primary);
  box-shadow: 0 0 0 4px rgba(0, 117, 222, 0.12);
}

button {
  border: 0;
  cursor: pointer;
  font: inherit;
}

.text-btn,
.admin-btn,
.user-trigger {
  height: 40px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
}

.text-btn {
  padding: 0 14px;
  color: var(--portal-ink-soft);
  background: transparent;
}

.text-btn:hover {
  color: var(--portal-ink);
  background: rgba(255, 255, 255, 0.78);
}

.admin-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  color: #fff;
  background: var(--portal-primary);
  padding: 0 15px;
  box-shadow: 0 12px 26px rgba(0, 117, 222, 0.18);
}

.admin-btn:hover {
  background: var(--portal-primary-active);
  transform: translateY(-1px);
}

.user-menu {
  position: relative;
}

.user-trigger {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  padding: 0 10px 0 6px;
  color: var(--portal-ink-soft);
  border: 1px solid var(--portal-hairline);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: var(--portal-shadow-soft);
}

.avatar {
  width: 30px;
  height: 30px;
  display: grid;
  place-items: center;
  overflow: hidden;
  border-radius: 10px;
  color: #fff;
  background: var(--portal-secondary);
  font-size: 13px;
  font-weight: 700;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-large {
  width: 40px;
  height: 40px;
  border-radius: 12px;
}

.user-meta {
  display: grid;
  gap: 1px;
  text-align: left;
}

.user-meta strong {
  max-width: 96px;
  overflow: hidden;
  color: var(--portal-ink-soft);
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow-icon {
  color: var(--portal-faint);
  transition: transform 0.18s ease;
}

.arrow-icon.open {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  width: 240px;
  padding: 8px;
  border: 1px solid var(--portal-hairline);
  border-radius: 16px;
  background: #fff;
  box-shadow: var(--portal-shadow-elevated);
}

.dropdown-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-bottom: 1px solid #f0ece8;
  margin-bottom: 6px;
}

.dropdown-profile strong {
  display: block;
  max-width: 152px;
  overflow: hidden;
  color: var(--portal-ink-soft);
  font-size: 14px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-item {
  width: 100%;
  height: 38px;
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 0 10px;
  border-radius: 12px;
  color: var(--portal-muted);
  background: transparent;
  font-size: 14px;
  text-align: left;
}

.dropdown-item:hover {
  color: var(--portal-ink-soft);
  background: #f7f5f2;
}

.dropdown-item.danger {
  color: #b42318;
}

.dropdown-item.danger:hover {
  background: #fff1ed;
}

.menu-pop-enter-active,
.menu-pop-leave-active {
  transition:
    opacity 0.16s ease,
    transform 0.16s ease;
}

.menu-pop-enter-from,
.menu-pop-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

@media (max-width: 840px) {
  .header-inner {
    width: calc(100% - 24px);
    height: 66px;
  }

  .brand-link {
    min-width: 0;
  }

  .brand-copy small {
    display: none;
  }

  .platform-state {
    display: none;
  }

  .header-actions {
    gap: 6px;
  }

  .text-btn {
    padding: 0 10px;
  }

  .admin-btn {
    padding: 0 10px;
  }

  .user-meta {
    display: none;
  }
}
</style>
