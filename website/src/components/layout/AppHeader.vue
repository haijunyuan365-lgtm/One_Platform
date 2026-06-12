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

      <div class="hotline-callout">
        <span>支持热线</span>
        <strong>400-1996-2026</strong>
      </div>

      <div class="platform-state">
        <span></span>
        目录索引
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
  border-bottom: 4px solid #000;
  background: #000;
  color: #fff;
  box-shadow: 0 8px 0 #000;
  transition: box-shadow 0.2s ease;
}

.app-header.scrolled {
  box-shadow: 0 10px 0 #000, 0 16px 26px rgba(0, 0, 0, 0.18);
}

.header-inner {
  width: 100%;
  min-height: 68px;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 0 18px;
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
  gap: 10px;
  min-width: 236px;
  color: #fff;
}

.brand-mark {
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border: 2px solid #fff;
  color: #000;
  background: #fcc20f;
  box-shadow: 3px 3px 0 #e91d2a;
}

.brand-copy {
  display: grid;
  gap: 2px;
}

.brand-copy strong {
  color: #fff;
  font-size: 15px;
  line-height: 1.2;
  font-family: var(--font-display);
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.brand-copy small,
.user-meta small,
.dropdown-profile small {
  color: #d7d7d7;
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

.hotline-callout {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 32px;
  padding: 0 12px;
  border: 2px solid #fff;
  color: #fff;
  background: #e91d2a;
  box-shadow: 3px 3px 0 #000;
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
  white-space: nowrap;
}

.hotline-callout span {
  padding: 2px 6px;
  color: #000;
  background: #fcc20f;
  font-weight: 900;
}

.hotline-callout strong {
  color: #fff;
  font-family: var(--font-display);
  letter-spacing: 0.02em;
}

.platform-state {
  height: 32px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  border: 2px solid #fff;
  color: #000;
  background: #fcc20f;
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
}

.platform-state span {
  width: 7px;
  height: 7px;
  border-radius: 0;
  background: #e91d2a;
  box-shadow: 0 0 0 2px #000;
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
  font-size: 14px;
  font-weight: 700;
}

.text-btn {
  padding: 0 14px;
  color: #fff;
  background: #000;
  border: 2px solid #fff;
}

.text-btn:hover {
  color: #000;
  background: #fcc20f;
}

.admin-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  padding: 0 15px;
  color: #fff;
  background: #e91d2a;
  border: 2px solid #fff;
  box-shadow: 3px 3px 0 #000;
}

.admin-btn:hover {
  color: #000;
  background: #fcc20f;
}

.user-menu {
  position: relative;
}

.user-trigger {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  padding: 0 10px 0 6px;
  color: #fff;
  background: #000;
  border: 2px solid #fff;
}

.avatar {
  width: 30px;
  height: 30px;
  display: grid;
  place-items: center;
  overflow: hidden;
  border-radius: 0;
  color: #000;
  background: #fcc20f;
  font-size: 13px;
  font-weight: 800;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-large {
  width: 40px;
  height: 40px;
}

.user-meta {
  display: grid;
  gap: 1px;
  text-align: left;
}

.user-meta strong {
  max-width: 96px;
  overflow: hidden;
  color: #fff;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow-icon {
  color: #fcc20f;
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
  border: 3px solid #000;
  background: #fff;
  box-shadow: 6px 6px 0 #000;
}

.dropdown-profile {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dropdown-profile {
  padding: 10px;
  border-bottom: 2px solid #000;
  margin-bottom: 6px;
}

.dropdown-profile strong {
  display: block;
  max-width: 152px;
  overflow: hidden;
  color: #000;
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
  border: 2px solid transparent;
  color: #000;
  background: transparent;
  font-size: 14px;
  text-align: left;
}

.dropdown-item:hover {
  border-color: #000;
  background: #fcc20f;
}

.dropdown-item.danger {
  color: #e91d2a;
}

.dropdown-item.danger:hover {
  color: #000;
  background: #e91d2a;
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
    height: 64px;
    gap: 8px;
    padding: 0 12px;
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
    padding: 0 8px;
  }

  .admin-btn {
    padding: 0 10px;
  }

  .user-meta {
    display: none;
  }
}
</style>
