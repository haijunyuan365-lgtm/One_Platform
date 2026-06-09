<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="modelValue" class="login-layer">
        <button type="button" class="login-backdrop" aria-label="关闭登录弹窗" @click="close"></button>

        <section class="login-panel" role="dialog" aria-modal="true" aria-labelledby="login-title">
          <aside class="login-side">
            <div class="side-mark">
              <ElIcon><Connection /></ElIcon>
            </div>
            <div>
              <span class="side-kicker">ONE PLATFORM</span>
              <h2>项目门户访问控制</h2>
              <p>登录后查看授权项目、访问入口、凭据和运行状态。</p>
            </div>

            <div class="side-stats">
              <div>
                <strong>SSO</strong>
                <span>统一身份</span>
              </div>
              <div>
                <strong>Audit</strong>
                <span>访问留痕</span>
              </div>
              <div>
                <strong>Role</strong>
                <span>权限隔离</span>
              </div>
            </div>
          </aside>

          <div class="login-main">
            <button type="button" class="close-btn" aria-label="关闭登录弹窗" @click="close">
              <ElIcon><Close /></ElIcon>
            </button>

            <div class="login-heading">
              <div class="heading-icon">
                <ElIcon><Lock /></ElIcon>
              </div>
              <div>
                <h1 id="login-title">欢迎回来</h1>
                <p>使用平台账号继续访问</p>
              </div>
            </div>

            <form class="login-form" @submit.prevent="handleLogin">
              <label class="form-field" :class="{ error: errors.username }">
                <span>账号</span>
                <div class="input-shell">
                  <ElIcon><User /></ElIcon>
                  <input
                    v-model="form.username"
                    type="text"
                    placeholder="请输入账号"
                    autocomplete="username"
                    @input="errors.username = ''"
                  />
                </div>
                <em v-if="errors.username">{{ errors.username }}</em>
              </label>

              <label class="form-field" :class="{ error: errors.password }">
                <span>密码</span>
                <div class="input-shell">
                  <ElIcon><Key /></ElIcon>
                  <input
                    v-model="form.password"
                    :type="showPwd ? 'text' : 'password'"
                    placeholder="请输入密码"
                    autocomplete="current-password"
                    @input="errors.password = ''"
                  />
                  <button type="button" class="icon-btn" :aria-label="showPwd ? '隐藏密码' : '显示密码'" @click="showPwd = !showPwd">
                    <ElIcon>
                      <component :is="showPwd ? Hide : View" />
                    </ElIcon>
                  </button>
                </div>
                <em v-if="errors.password">{{ errors.password }}</em>
              </label>

              <div v-if="loginError" class="error-banner">{{ loginError }}</div>

              <button type="submit" class="submit-btn" :disabled="loading">
                <span v-if="loading" class="loading-dot"></span>
                {{ loading ? '登录中...' : '登录' }}
              </button>
            </form>

            <div class="demo-account">
              <span>演示账号</span>
              <strong>admin / 123456</strong>
            </div>
          </div>
        </section>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Close, Connection, Hide, Key, Lock, User, View } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [val: boolean] }>()

const authStore = useAuthStore()
const loading = ref(false)
const loginError = ref('')
const showPwd = ref(false)

const form = reactive({ username: '', password: '' })
const errors = reactive({ username: '', password: '' })

function close() {
  loginError.value = ''
  errors.username = ''
  errors.password = ''
  emit('update:modelValue', false)
}

async function handleLogin() {
  errors.username = form.username.trim() ? '' : '请输入账号'
  errors.password = form.password ? '' : '请输入密码'
  if (errors.username || errors.password) return

  try {
    loading.value = true
    loginError.value = ''
    const result = await authStore.login(form.username, form.password)

    if (result.success) {
      close()
      form.username = ''
      form.password = ''
    } else {
      loginError.value = result.message || '登录失败'
    }
  } catch {
    loginError.value = '登录服务暂时不可用，请稍后再试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-layer {
  position: fixed;
  inset: 0;
  z-index: 100;
  display: grid;
  place-items: center;
  padding: 20px;
}

.login-backdrop {
  position: absolute;
  inset: 0;
  border: 0;
  background:
    linear-gradient(135deg, rgba(12, 19, 33, 0.72), rgba(22, 74, 88, 0.62)),
    rgba(12, 19, 33, 0.68);
  backdrop-filter: blur(12px);
}

.login-panel {
  position: relative;
  width: min(860px, 100%);
  min-height: 520px;
  display: grid;
  grid-template-columns: minmax(280px, 0.88fr) minmax(320px, 1fr);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 32px 78px rgba(0, 0, 0, 0.3);
}

.login-side {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 30px;
  color: #fff;
  background:
    linear-gradient(155deg, rgba(14, 31, 51, 0.98), rgba(29, 91, 99, 0.95)),
    #102033;
}

.side-mark,
.heading-icon {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 8px;
}

.side-mark {
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.12);
}

.side-kicker {
  display: block;
  margin-bottom: 12px;
  color: #8fe1c6;
  font-size: 12px;
  font-weight: 800;
}

.login-side h2,
.login-heading h1,
.login-side p {
  margin: 0;
}

.login-side h2 {
  max-width: 260px;
  font-size: 30px;
  line-height: 1.2;
  font-weight: 800;
}

.login-side p {
  max-width: 280px;
  margin-top: 12px;
  color: rgba(232, 240, 250, 0.76);
  font-size: 14px;
  line-height: 1.75;
}

.side-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.side-stats div {
  min-height: 76px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
  padding: 12px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
}

.side-stats strong {
  font-size: 14px;
}

.side-stats span {
  color: rgba(232, 240, 250, 0.68);
  font-size: 12px;
}

.login-main {
  position: relative;
  padding: 52px 46px 34px;
  background:
    linear-gradient(180deg, rgba(247, 250, 252, 0.96), rgba(255, 255, 255, 1)),
    #fff;
}

.close-btn,
.icon-btn {
  display: inline-grid;
  place-items: center;
  border: 0;
  border-radius: 8px;
  cursor: pointer;
  font: inherit;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 36px;
  height: 36px;
  color: #64748b;
  background: #eef3f8;
}

.close-btn:hover,
.icon-btn:hover {
  color: #142033;
  background: #e2eaf2;
}

.login-heading {
  display: flex;
  align-items: center;
  gap: 13px;
  margin-bottom: 28px;
}

.heading-icon {
  color: #fff;
  background: #1e63d6;
  box-shadow: 0 14px 26px rgba(30, 99, 214, 0.22);
}

.login-heading h1 {
  color: #142033;
  font-size: 24px;
  line-height: 1.2;
  font-weight: 800;
}

.login-heading p {
  margin: 4px 0 0;
  color: #68758a;
  font-size: 13px;
}

.login-form {
  display: grid;
  gap: 16px;
}

.form-field {
  display: grid;
  gap: 7px;
  color: #243149;
  font-size: 13px;
  font-weight: 800;
}

.input-shell {
  height: 46px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 12px;
  border: 1px solid #dbe3ed;
  border-radius: 8px;
  color: #68758a;
  background: #fff;
  transition:
    border-color 0.16s ease,
    box-shadow 0.16s ease;
}

.input-shell:focus-within {
  border-color: #1e63d6;
  box-shadow: 0 0 0 3px rgba(30, 99, 214, 0.1);
}

.input-shell input {
  min-width: 0;
  flex: 1;
  border: 0;
  outline: 0;
  color: #142033;
  background: transparent;
  font: inherit;
  font-weight: 600;
}

.input-shell input::placeholder {
  color: #9aa6b8;
}

.icon-btn {
  width: 30px;
  height: 30px;
  flex: 0 0 auto;
  color: #68758a;
  background: transparent;
}

.form-field em {
  color: #b42318;
  font-size: 12px;
  font-style: normal;
  font-weight: 700;
}

.form-field.error .input-shell {
  border-color: #f6a391;
  background: #fff7f4;
}

.error-banner {
  min-height: 38px;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid #ffd6cf;
  border-radius: 8px;
  color: #b42318;
  background: #fff7f4;
  font-size: 13px;
  font-weight: 700;
}

.submit-btn {
  height: 46px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 0;
  border-radius: 8px;
  color: #fff;
  background: #1e63d6;
  box-shadow: 0 14px 28px rgba(30, 99, 214, 0.24);
  cursor: pointer;
  font: inherit;
  font-size: 14px;
  font-weight: 800;
}

.submit-btn:hover:not(:disabled) {
  background: #174fba;
}

.submit-btn:disabled {
  cursor: not-allowed;
  opacity: 0.72;
}

.loading-dot {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: #fff;
  border-radius: 999px;
  animation: rotate 0.8s linear infinite;
}

.demo-account {
  min-height: 44px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
  padding: 0 12px;
  border: 1px solid #dfe7ef;
  border-radius: 8px;
  color: #68758a;
  background: #f7fafc;
  font-size: 13px;
}

.demo-account strong {
  color: #142033;
  font-weight: 800;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.18s ease;
}

.modal-enter-active .login-panel,
.modal-leave-active .login-panel {
  transition:
    opacity 0.18s ease,
    transform 0.18s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .login-panel,
.modal-leave-to .login-panel {
  opacity: 0;
  transform: translateY(10px) scale(0.98);
}

@keyframes rotate {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 760px) {
  .login-layer {
    padding: 12px;
  }

  .login-panel {
    max-height: calc(100vh - 24px);
    min-height: 0;
    grid-template-columns: 1fr;
    overflow: auto;
  }

  .login-side {
    min-height: 178px;
    padding: 22px;
  }

  .login-side h2 {
    font-size: 24px;
  }

  .side-stats {
    display: none;
  }

  .login-main {
    padding: 42px 20px 22px;
  }

  .demo-account {
    align-items: flex-start;
    flex-direction: column;
    justify-content: center;
    padding: 10px 12px;
  }
}
</style>
