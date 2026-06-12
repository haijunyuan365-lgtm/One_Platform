<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="modelValue" class="login-layer">
        <button type="button" class="login-backdrop" aria-label="关闭登录弹窗" @click="close"></button>

        <section class="login-panel" role="dialog" aria-modal="true" aria-labelledby="login-title">
          <aside class="login-side">
            <div class="brand-row">
              <div class="side-mark">
                <span>OP</span>
              </div>
              <div class="brand-copy">
                <strong>公司一体化平台</strong>
                <span>OnePlatform Portal</span>
              </div>
            </div>

            <div class="side-hero">
              <span class="side-kicker">PORTAL ACCESS</span>
              <h2>把内部入口收进同一张桌面</h2>
              <p>统一访问项目门牌、账号凭据、二维码和使用说明，减少切换成本。</p>
            </div>

            <div class="board-preview" aria-hidden="true">
              <article class="board-note board-note-wide">
                <span class="note-label">Workspace</span>
                <strong>门户概览板</strong>
                <p>项目、账号和巡检结果集中展示。</p>
              </article>
              <article class="board-note board-note-yellow">
                <span class="note-label">项目</span>
                <strong>05</strong>
                <p>当前可访问入口</p>
              </article>
              <article class="board-note board-note-coral">
                <span class="note-label">关注</span>
                <strong>02</strong>
                <p>异常或维护中</p>
              </article>
              <article class="board-note board-note-teal">
                <img :src="heroImage" alt="" />
                <span>视觉占位</span>
              </article>
            </div>

            <div class="side-stats">
              <div>
                <strong>入口</strong>
                <span>项目与资源</span>
              </div>
              <div>
                <strong>账号</strong>
                <span>统一授权</span>
              </div>
              <div>
                <strong>状态</strong>
                <span>实时巡检</span>
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
                <span class="main-kicker">SECURE SIGN-IN</span>
                <h1 id="login-title">进入平台</h1>
                <p>使用账号登录后访问项目入口。</p>
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
                  <button
                    type="button"
                    class="icon-btn"
                    :aria-label="showPwd ? '隐藏密码' : '显示密码'"
                    @click="showPwd = !showPwd"
                  >
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
                {{ loading ? '正在进入...' : '进入平台' }}
              </button>
            </form>

            <div class="demo-account">
              <div>
                <span>演示账号</span>
                <strong>admin / 123456</strong>
              </div>
              <small>Portal Demo</small>
            </div>
          </div>
        </section>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Close, Hide, Key, Lock, User, View } from '@element-plus/icons-vue'
import heroImage from '@/assets/hero.png'
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
  padding: 16px;
  overflow: hidden;
}

.login-layer::before {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.7), rgba(255, 255, 255, 0.34)),
    repeating-linear-gradient(0deg, rgba(28, 28, 30, 0.03) 0 1px, transparent 1px 28px),
    repeating-linear-gradient(90deg, rgba(28, 28, 30, 0.03) 0 1px, transparent 1px 28px);
}

.login-backdrop {
  position: absolute;
  inset: 0;
  border: 0;
  background: rgba(250, 250, 247, 0.84);
  backdrop-filter: blur(14px);
}

.login-panel {
  position: relative;
  z-index: 1;
  width: min(1180px, calc(100vw - 32px));
  min-height: min(720px, calc(100vh - 32px));
  display: grid;
  grid-template-columns: minmax(420px, 1.04fr) minmax(360px, 0.96fr);
  overflow: hidden;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: rgba(5, 0, 56, 0.22) 0 28px 64px -18px;
}

.login-side {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 22px;
  min-width: 0;
  padding: 40px 40px 32px;
  color: var(--portal-ink);
  background:
    linear-gradient(180deg, rgba(255, 244, 196, 0.88), rgba(255, 255, 255, 0.96)),
    #fff;
}

.login-side::before {
  content: '';
  position: absolute;
  inset: 24px;
  border: 1px solid rgba(224, 226, 232, 0.65);
  border-radius: 28px;
  pointer-events: none;
}

.brand-row,
.side-hero,
.board-preview,
.side-stats {
  position: relative;
  z-index: 1;
}

.brand-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.side-mark {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(28, 28, 30, 0.08);
  border-radius: 14px;
  color: #1c1c1e;
  background: var(--portal-yellow);
  box-shadow: rgba(252, 185, 0, 0.24) 0 16px 24px -12px;
}

.side-mark span {
  font-family: var(--portal-font-display);
  font-size: 16px;
  font-weight: 700;
}

.brand-copy {
  display: grid;
  gap: 2px;
}

.brand-copy strong {
  font-family: var(--portal-font-display);
  font-size: 20px;
  line-height: 1.2;
  font-weight: 700;
}

.brand-copy span {
  color: var(--portal-subtle);
  font-size: 12px;
  font-weight: 700;
}

.side-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  margin-bottom: 14px;
  padding: 7px 12px;
  border-radius: 999px;
  color: var(--portal-ink);
  background: var(--portal-yellow);
  font-size: 12px;
  font-weight: 800;
}

.side-hero h2,
.login-heading h1,
.side-hero p {
  margin: 0;
}

.side-hero h2 {
  max-width: 560px;
  font-family: var(--portal-font-display);
  font-size: clamp(28px, 3.8vw, 46px);
  line-height: 1.12;
  font-weight: 700;
}

.side-hero p {
  max-width: 520px;
  margin-top: 12px;
  color: var(--portal-muted);
  font-size: 14px;
  line-height: 1.8;
}

.board-preview {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  padding: 16px;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: var(--portal-shadow-mockup);
}

.board-note {
  min-height: 118px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 8px;
  padding: 14px;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 20px;
  background: #fff;
}

.board-note-wide {
  grid-column: 1 / -1;
  min-height: 138px;
}

.note-label {
  color: var(--portal-muted);
  font-size: 12px;
  font-weight: 700;
}

.board-note strong {
  font-family: var(--portal-font-display);
  font-size: 28px;
  line-height: 1;
  font-weight: 700;
}

.board-note p,
.board-note span {
  color: var(--portal-subtle);
  font-size: 12px;
  line-height: 1.5;
}

.board-note img {
  width: 100%;
  height: 72px;
  object-fit: contain;
}

.board-note-yellow {
  background: var(--portal-yellow-soft);
}

.board-note-coral {
  background: #ffc6c6;
}

.board-note-teal {
  background: var(--portal-teal);
}

.side-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: auto;
}

.side-stats div {
  min-height: 82px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
  padding: 14px;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.88);
}

.side-stats strong {
  font-family: var(--portal-font-display);
  font-size: 14px;
  font-weight: 700;
}

.side-stats span {
  color: var(--portal-subtle);
  font-size: 12px;
}

.login-main {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 36px 42px 32px;
  background: #fff;
  border-left: 1px solid rgba(224, 226, 232, 0.95);
}

.close-btn,
.icon-btn {
  position: relative;
  z-index: 1;
  display: inline-grid;
  place-items: center;
  border: 0;
  border-radius: 999px;
  cursor: pointer;
  font: inherit;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 40px;
  height: 40px;
  color: var(--portal-muted);
  background: #fff;
  box-shadow: var(--portal-shadow-soft);
}

.close-btn:hover,
.icon-btn:hover {
  color: var(--portal-ink);
  background: #f7f8fa;
}

.login-heading {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
}

.heading-icon {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  color: #1c1c1e;
  background: var(--portal-yellow);
}

.main-kicker {
  display: block;
  margin-bottom: 5px;
  color: var(--portal-subtle);
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.login-heading h1 {
  color: var(--portal-ink);
  font-family: var(--portal-font-display);
  font-size: 28px;
  line-height: 1.18;
  font-weight: 700;
}

.login-heading p {
  margin: 4px 0 0;
  color: var(--portal-muted);
  font-size: 13px;
}

.login-form {
  display: grid;
  gap: 16px;
}

.form-field {
  display: grid;
  gap: 8px;
  color: var(--portal-ink);
  font-size: 13px;
  font-weight: 700;
}

.input-shell {
  height: 52px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  border: 1px solid var(--portal-border-strong);
  border-radius: 999px;
  background: #fff;
  box-shadow: var(--portal-shadow-soft);
  transition:
    border-color 0.16s ease,
    box-shadow 0.16s ease,
    transform 0.16s ease,
    background 0.16s ease;
}

.input-shell:focus-within {
  border-color: var(--portal-ink);
  box-shadow: rgba(5, 0, 56, 0.12) 0 16px 28px -14px;
  transform: translateY(-1px);
}

.input-shell input {
  min-width: 0;
  flex: 1;
  border: 0;
  outline: 0;
  color: var(--portal-ink);
  background: transparent;
  font: inherit;
  font-weight: 600;
}

.input-shell input::placeholder {
  color: var(--portal-subtle);
}

.icon-btn {
  width: 30px;
  height: 30px;
  flex: 0 0 auto;
  color: var(--portal-muted);
  background: transparent;
}

.form-field em {
  color: #b42318;
  font-size: 12px;
  font-style: normal;
  font-weight: 700;
}

.form-field.error .input-shell {
  border-color: rgba(255, 122, 122, 0.5);
  background: #fff8f4;
}

.error-banner {
  min-height: 38px;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid #ffd6cf;
  border-radius: 16px;
  color: #b42318;
  background: #fff7f4;
  font-size: 13px;
  font-weight: 700;
}

.submit-btn {
  position: relative;
  min-width: 0;
  height: 52px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  overflow: hidden;
  border: 0;
  border-radius: 999px;
  color: #fff;
  background: var(--portal-ink);
  box-shadow: rgba(5, 0, 56, 0.16) 0 16px 28px -10px;
  cursor: pointer;
  font: inherit;
  font-size: 15px;
  font-weight: 800;
  transition:
    box-shadow 0.18s ease,
    background 0.18s ease,
    transform 0.18s ease;
}

.submit-btn:hover:not(:disabled) {
  background: #2c2c34;
  box-shadow: rgba(5, 0, 56, 0.2) 0 20px 36px -12px;
  transform: translateY(-1px);
}

.submit-btn:disabled {
  cursor: not-allowed;
  opacity: 0.72;
}

.loading-dot {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.42);
  border-top-color: #fff;
  border-radius: 999px;
  animation: rotate 0.8s linear infinite;
}

.demo-account {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
  padding: 12px 14px;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 20px;
  background: rgba(255, 244, 196, 0.58);
}

.demo-account div {
  display: grid;
  gap: 3px;
}

.demo-account strong {
  color: var(--portal-ink);
  font-family: var(--portal-font-display);
  font-size: 15px;
  font-weight: 700;
}

.demo-account small {
  flex: 0 0 auto;
  padding: 4px 8px;
  border-radius: 999px;
  color: #187574;
  background: var(--portal-teal);
  font-size: 12px;
  font-weight: 800;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.22s ease;
}

.modal-enter-active .login-panel,
.modal-leave-active .login-panel {
  transition:
    opacity 0.26s ease,
    transform 0.26s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .login-panel,
.modal-leave-to .login-panel {
  opacity: 0;
  transform: translateY(16px) scale(0.985);
}

@keyframes rotate {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 960px) {
  .login-panel {
    min-height: auto;
    grid-template-columns: 1fr;
    overflow: auto;
  }

  .login-side {
    padding: 28px 22px 24px;
  }

  .login-main {
    min-height: 480px;
    padding: 64px 22px 28px;
    border-top: 1px solid rgba(224, 226, 232, 0.95);
    border-left: 0;
  }
}

@media (max-width: 620px) {
  .login-layer {
    padding: 10px;
  }

  .login-panel {
    width: calc(100vw - 20px);
    border-radius: 24px;
  }

  .login-side::before {
    inset: 14px;
  }

  .board-preview {
    grid-template-columns: 1fr;
  }

  .side-stats {
    grid-template-columns: 1fr;
  }

  .login-heading {
    align-items: flex-start;
  }

  .heading-icon {
    width: 44px;
    height: 44px;
  }

  .login-heading h1 {
    font-size: 24px;
  }
}
</style>
