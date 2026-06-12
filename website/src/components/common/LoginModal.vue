<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="modelValue" class="login-layer">
        <button type="button" class="login-backdrop" aria-label="关闭登录弹窗" @click="close"></button>

        <section class="login-panel" role="dialog" aria-modal="true" aria-labelledby="login-title">
          <aside class="login-side">
            <div class="brand-row">
              <div class="side-mark">
                <ElIcon><Connection /></ElIcon>
              </div>
              <div class="brand-copy">
                <strong>公司一体化平台</strong>
                <span>Unified Project Portal</span>
              </div>
            </div>

            <div class="side-hero">
              <span class="side-kicker">PROJECT PORTAL</span>
              <h2>进入一份安静、清晰的内部工作台</h2>
              <p>统一查看项目入口、凭据和访问说明，让每一次登录都直接抵达需要的工作页面。</p>
            </div>

            <div class="ai-visual" aria-hidden="true">
              <div class="visual-note note-blue">
                <strong>项目入口</strong>
                <span>地址、小程序与二维码统一归档</span>
              </div>

              <div class="visual-paper">
                <div class="paper-top">
                  <span>Workspace Overview</span>
                  <i></i>
                </div>
                <strong>今日访问目录</strong>
                <div class="paper-lines">
                  <span></span>
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <div class="paper-tags">
                  <span>可用</span>
                  <span>凭据</span>
                  <span>说明</span>
                </div>
              </div>

              <div class="visual-note note-orange">
                <strong>统一凭据</strong>
                <span>账号、密码和环境信息一处查看</span>
              </div>
            </div>

            <div class="side-stats">
              <div>
                <strong>统一入口</strong>
                <span>按项目查看系统与站点</span>
              </div>
              <div>
                <strong>凭据查看</strong>
                <span>访问账号与密码集中整理</span>
              </div>
              <div>
                <strong>状态追踪</strong>
                <span>异常与维护信息及时提醒</span>
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
                <span class="main-kicker">SECURE ACCESS</span>
                <h1 id="login-title">门户账号登录</h1>
                <p>使用公司一体化平台账号访问项目门户</p>
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
                {{ loading ? '正在接入...' : '进入平台' }}
              </button>
            </form>

            <div class="demo-account">
              <div>
                <span>演示账号</span>
                <strong>admin / 123456</strong>
              </div>
              <small>演示环境</small>
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
  padding: 24px;
  overflow: hidden;
}

.login-layer::before,
.login-layer::after {
  position: absolute;
  inset: -12%;
  content: '';
  pointer-events: none;
}

.login-layer::before {
  background:
    radial-gradient(circle at 16% 18%, rgba(214, 182, 246, 0.24), transparent 18%),
    radial-gradient(circle at 84% 12%, rgba(98, 174, 240, 0.18), transparent 16%),
    radial-gradient(circle at 78% 82%, rgba(255, 100, 200, 0.12), transparent 16%);
  opacity: 0.9;
}

.login-layer::after {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.24), transparent 54%);
  opacity: 0.88;
}

.login-backdrop {
  position: absolute;
  inset: 0;
  border: 0;
  background: rgba(24, 21, 18, 0.4);
  backdrop-filter: blur(18px);
}

.login-panel {
  position: relative;
  z-index: 1;
  width: min(1080px, calc(100vw - 48px));
  min-height: min(700px, calc(100dvh - 48px));
  display: grid;
  grid-template-columns: minmax(0, 1.02fr) minmax(390px, 440px);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.24);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.2);
  box-shadow: var(--portal-shadow-elevated);
}

.login-side {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 28px;
  min-width: 0;
  padding: 42px 42px 38px;
  overflow: hidden;
  color: #fff;
  background:
    radial-gradient(circle at 82% 18%, rgba(98, 174, 240, 0.24), transparent 22%),
    radial-gradient(circle at 20% 84%, rgba(214, 182, 246, 0.18), transparent 28%),
    linear-gradient(145deg, #213183, #17275f 58%, #101a47);
}

.login-side::before {
  position: absolute;
  top: 38px;
  right: 34px;
  width: 72px;
  height: 72px;
  border-radius: 18px;
  background: linear-gradient(145deg, rgba(255, 100, 200, 0.94), rgba(214, 182, 246, 0.92));
  content: '';
  pointer-events: none;
  transform: rotate(-8deg);
}

.login-side::after {
  position: absolute;
  left: 44px;
  bottom: 42px;
  width: 96px;
  height: 96px;
  border-radius: 22px;
  background: linear-gradient(145deg, rgba(42, 157, 153, 0.94), rgba(98, 174, 240, 0.9));
  content: '';
  opacity: 0.92;
  transform: rotate(10deg);
  pointer-events: none;
}

.brand-row,
.side-hero,
.side-stats,
.ai-visual {
  position: relative;
  z-index: 1;
}

.brand-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-copy {
  display: grid;
  gap: 3px;
}

.brand-copy strong {
  color: #fff;
  font-size: 20px;
  line-height: 1.15;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.brand-copy span {
  color: rgba(255, 255, 255, 0.74);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.side-mark,
.heading-icon {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  border-radius: 12px;
}

.side-mark {
  flex: 0 0 auto;
  border: 1px solid rgba(255, 255, 255, 0.18);
  color: #fff;
  background: rgba(255, 255, 255, 0.12);
  box-shadow: var(--portal-shadow-soft);
}

.side-kicker {
  display: block;
  margin-bottom: 14px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.login-side h2,
.login-heading h1,
.login-side p {
  margin: 0;
}

.login-side h2 {
  max-width: 520px;
  color: #fff;
  font-size: clamp(34px, 4vw, 50px);
  line-height: 1.04;
  font-weight: 700;
  letter-spacing: -0.05em;
}

.login-side p {
  max-width: 500px;
  margin-top: 14px;
  color: rgba(255, 255, 255, 0.76);
  font-size: 15px;
  line-height: 1.75;
}

.ai-visual {
  position: relative;
  min-height: 260px;
  margin-top: auto;
}

.visual-paper,
.visual-note {
  position: absolute;
  box-shadow: var(--portal-shadow-soft);
}

.visual-paper {
  left: 50%;
  bottom: 12px;
  width: min(330px, calc(100% - 88px));
  padding: 20px;
  border-radius: 24px;
  color: var(--portal-ink-soft);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 245, 242, 0.98));
  transform: translateX(-50%) rotate(-4deg);
}

.paper-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.paper-top span {
  color: var(--portal-faint);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
}

.paper-top i {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: var(--portal-primary);
  box-shadow: 0 0 0 5px rgba(0, 117, 222, 0.12);
}

.visual-paper > strong {
  display: block;
  margin-top: 18px;
  color: var(--portal-ink);
  font-size: 28px;
  line-height: 1;
  font-weight: 700;
  letter-spacing: -0.05em;
}

.paper-lines {
  display: grid;
  gap: 10px;
  margin-top: 18px;
}

.paper-lines span {
  height: 10px;
  border-radius: 999px;
  background: #efeae5;
}

.paper-lines span:nth-child(1) {
  width: 84%;
}

.paper-lines span:nth-child(2) {
  width: 100%;
}

.paper-lines span:nth-child(3) {
  width: 76%;
}

.paper-lines span:nth-child(4) {
  width: 62%;
}

.paper-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 18px;
}

.paper-tags span {
  min-height: 28px;
  display: inline-flex;
  align-items: center;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(0, 117, 222, 0.08);
  color: var(--portal-primary);
}

.visual-note {
  width: 168px;
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.98);
  color: var(--portal-ink-soft);
}

.visual-note strong {
  display: block;
  color: var(--portal-ink);
  font-size: 14px;
  font-weight: 700;
}

.visual-note span {
  display: block;
  margin-top: 6px;
  color: var(--portal-muted);
  font-size: 12px;
  line-height: 1.5;
}

.note-blue {
  top: 24px;
  left: 0;
  transform: rotate(-6deg);
}

.note-orange {
  right: 4px;
  bottom: 18px;
  transform: rotate(5deg);
}

.side-stats {
  width: min(560px, 100%);
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.side-stats div {
  min-height: 92px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: var(--portal-shadow-soft);
}

.side-stats strong {
  color: #fff;
  font-size: 14px;
  font-weight: 700;
}

.side-stats span {
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  line-height: 1.55;
}

.login-main {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  padding: 44px 42px 40px;
  overflow: hidden;
  border-left: 1px solid rgba(255, 255, 255, 0.28);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 245, 244, 0.98)),
    #fff;
}

.login-main::before {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.26), transparent 28%);
  content: '';
  opacity: 0.88;
  pointer-events: none;
}

.login-main::after {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--portal-primary), #62aef0, #d6b6f6);
  content: '';
}

.close-btn,
.icon-btn {
  position: relative;
  z-index: 1;
  display: inline-grid;
  place-items: center;
  border: 0;
  border-radius: 12px;
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
  border: 1px solid var(--portal-hairline);
  background: rgba(255, 255, 255, 0.94);
  box-shadow: var(--portal-shadow-soft);
  transition:
    color 0.16s ease,
    background 0.16s ease,
    transform 0.16s ease;
}

.close-btn:hover,
.icon-btn:hover {
  color: var(--portal-ink);
  background: #f7f5f2;
  transform: translateY(-1px);
}

.login-heading {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 370px;
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
}

.heading-icon {
  flex: 0 0 auto;
  color: var(--portal-primary);
  border: 1px solid rgba(0, 117, 222, 0.12);
  background: rgba(0, 117, 222, 0.08);
  box-shadow: var(--portal-shadow-soft);
}

.main-kicker {
  display: block;
  margin-bottom: 6px;
  color: var(--portal-primary);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.login-heading h1 {
  color: var(--portal-ink);
  font-size: 32px;
  line-height: 1.08;
  font-weight: 700;
  letter-spacing: -0.04em;
}

.login-heading p {
  margin: 6px 0 0;
  color: var(--portal-muted);
  font-size: 14px;
  line-height: 1.6;
}

.login-form {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 370px;
  display: grid;
  gap: 18px;
}

.form-field {
  display: grid;
  gap: 8px;
  color: var(--portal-ink-soft);
  font-size: 13px;
  font-weight: 600;
}

.input-shell {
  height: 52px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  border: 1px solid #dfdbd6;
  border-radius: 12px;
  color: var(--portal-faint);
  background: #fff;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.92);
  transition:
    background 0.16s ease,
    border-color 0.16s ease,
    box-shadow 0.16s ease,
    transform 0.16s ease;
}

.input-shell:focus-within {
  border-color: var(--portal-primary);
  background: #fff;
  box-shadow:
    0 0 0 4px rgba(0, 117, 222, 0.12),
    var(--portal-shadow-soft);
  transform: translateY(-1px);
}

.input-shell input {
  min-width: 0;
  flex: 1;
  border: 0;
  outline: 0;
  color: var(--portal-ink-soft);
  background: transparent;
  font: inherit;
  font-weight: 500;
}

.input-shell input::placeholder {
  color: var(--portal-faint);
}

.icon-btn {
  width: 30px;
  height: 30px;
  flex: 0 0 auto;
  color: var(--portal-faint);
  background: transparent;
}

.form-field em {
  color: #b42318;
  font-size: 12px;
  font-style: normal;
  font-weight: 600;
}

.form-field.error .input-shell {
  border-color: rgba(181, 63, 36, 0.4);
  background: #fff8f4;
}

.error-banner {
  min-height: 40px;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid #ffd6cf;
  border-radius: 12px;
  color: #b42318;
  background: #fff7f4;
  font-size: 13px;
  font-weight: 600;
}

.submit-btn {
  position: relative;
  min-width: 0;
  height: 50px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  overflow: hidden;
  border: 0;
  border-radius: 999px;
  color: #fff;
  background: var(--portal-primary);
  box-shadow: 0 14px 30px rgba(0, 117, 222, 0.22);
  cursor: pointer;
  font: inherit;
  font-size: 15px;
  font-weight: 600;
  transition:
    box-shadow 0.18s ease,
    filter 0.18s ease,
    transform 0.18s ease;
}

.submit-btn::before {
  position: absolute;
  inset: 0;
  z-index: 0;
  background: linear-gradient(110deg, transparent 0 34%, rgba(255, 255, 255, 0.36) 42%, transparent 52%);
  content: '';
  pointer-events: none;
  transform: translateX(-115%);
  transition: transform 0.55s ease;
}

.submit-btn:hover:not(:disabled) {
  background: var(--portal-primary-active);
  box-shadow: 0 18px 34px rgba(0, 117, 222, 0.28);
  transform: translateY(-1px);
}

.submit-btn:hover:not(:disabled)::before {
  transform: translateX(115%);
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
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 370px;
  min-height: 62px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 20px;
  padding: 12px 14px;
  border: 1px solid #ece7e2;
  border-radius: 16px;
  color: var(--portal-muted);
  background: #f7f5f2;
  box-shadow: var(--portal-shadow-soft);
  font-size: 13px;
}

.demo-account div {
  display: grid;
  gap: 3px;
}

.demo-account strong {
  color: var(--portal-ink-soft);
  font-size: 16px;
  font-weight: 700;
}

.demo-account small {
  flex: 0 0 auto;
  padding: 4px 8px;
  border-radius: 999px;
  color: var(--portal-primary);
  background: rgba(0, 117, 222, 0.08);
  font-size: 12px;
  font-weight: 600;
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
    width: calc(100vw - 24px);
    min-height: calc(100dvh - 24px);
    grid-template-columns: 1fr;
    overflow: auto;
  }

  .login-side {
    min-height: 420px;
    padding: 32px 24px 24px;
  }

  .login-side h2 {
    max-width: 100%;
    font-size: 32px;
  }

  .login-side p {
    max-width: 100%;
  }

  .ai-visual {
    min-height: 220px;
  }

  .login-main {
    min-height: 420px;
    padding: 48px 24px 28px;
    border-top: 1px solid rgba(255, 255, 255, 0.28);
    border-left: 0;
  }
}

@media (max-width: 640px) {
  .demo-account {
    align-items: flex-start;
    flex-direction: column;
    justify-content: center;
    padding: 10px 12px;
  }

  .brand-copy strong {
    font-size: 18px;
  }

  .login-side {
    min-height: 360px;
  }

  .login-side h2 {
    font-size: 24px;
  }

  .visual-paper {
    width: calc(100% - 52px);
    padding: 18px;
  }

  .visual-note {
    width: 148px;
  }

  .side-stats {
    grid-template-columns: 1fr;
  }

  .side-stats div {
    min-height: 58px;
  }

  .login-heading {
    align-items: flex-start;
  }

  .heading-icon {
    width: 44px;
    height: 44px;
  }

  .login-heading h1 {
    font-size: 28px;
  }
}
</style>
