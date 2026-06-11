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
                <strong>数创智联</strong>
                <span>Digital Innovation Link</span>
              </div>
            </div>

            <div class="side-hero">
              <span class="side-kicker">AI SOFTWARE COMPANY</span>
              <h2>进入企业智能应用中枢</h2>
              <p>统一访问项目门户、AI 工具链、交付资产与运营数据，让每一次登录都连接到更高效的数字化现场。</p>
            </div>

            <div class="ai-visual" aria-hidden="true">
              <div class="ai-core">
                <span></span>
                <i></i>
              </div>
              <div class="trace trace-one"></div>
              <div class="trace trace-two"></div>
              <div class="trace trace-three"></div>
              <div class="pulse-node node-one"></div>
              <div class="pulse-node node-two"></div>
              <div class="pulse-node node-three"></div>
              <div class="data-stack">
                <span></span>
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>

            <div class="side-stats">
              <div>
                <strong>AI Copilot</strong>
                <span>智能研发</span>
              </div>
              <div>
                <strong>Data Hub</strong>
                <span>数据治理</span>
              </div>
              <div>
                <strong>DevOps</strong>
                <span>持续交付</span>
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
                <h1 id="login-title">智联身份登录</h1>
                <p>使用数创智联账号访问项目门户</p>
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
  padding: 0;
  overflow: hidden;
  font-family:
    "Microsoft YaHei",
    "PingFang SC",
    "HarmonyOS Sans SC",
    "Segoe UI",
    sans-serif;
}

.login-layer::before,
.login-layer::after {
  position: absolute;
  inset: -18%;
  content: "";
  pointer-events: none;
}

.login-layer::before {
  background-image:
    linear-gradient(rgba(55, 229, 206, 0.09) 1px, transparent 1px),
    linear-gradient(90deg, rgba(55, 229, 206, 0.07) 1px, transparent 1px),
    linear-gradient(128deg, transparent 0 64%, rgba(245, 183, 69, 0.12) 64.2% 64.7%, transparent 65%),
    linear-gradient(38deg, transparent 0 70%, rgba(44, 151, 255, 0.12) 70.2% 70.7%, transparent 71%);
  background-position:
    center,
    center,
    0 0,
    0 0;
  background-size:
    54px 54px,
    54px 54px,
    100% 100%,
    100% 100%;
  mask-image: linear-gradient(180deg, transparent, #000 16%, #000 82%, transparent);
  opacity: 0.9;
  animation: gridDrift 18s linear infinite;
}

.login-layer::after {
  background:
    repeating-linear-gradient(
      180deg,
      transparent 0,
      transparent 17px,
      rgba(255, 255, 255, 0.035) 18px,
      transparent 19px
    );
  mix-blend-mode: screen;
  opacity: 0.54;
  transform: translateY(-2%);
  animation: scanMove 7s ease-in-out infinite;
}

.login-backdrop {
  position: absolute;
  inset: 0;
  border: 0;
  background:
    linear-gradient(115deg, rgba(5, 10, 20, 0.92), rgba(9, 34, 40, 0.88) 44%, rgba(31, 27, 22, 0.9)),
    #07101b;
  backdrop-filter: blur(16px);
}

.login-panel {
  position: relative;
  z-index: 1;
  width: 100%;
  min-height: 100dvh;
  display: grid;
  grid-template-columns: minmax(480px, 1fr) minmax(430px, 560px);
  overflow: hidden;
  background: transparent;
  box-shadow: none;
}

.login-side {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 28px;
  min-width: 0;
  padding: 64px 72px 58px;
  overflow: hidden;
  color: #fff;
  background:
    linear-gradient(90deg, rgba(3, 12, 21, 0.58), rgba(3, 12, 21, 0.22) 56%, transparent),
    linear-gradient(145deg, rgba(8, 42, 47, 0.46), rgba(37, 31, 22, 0.26));
}

.login-side::before {
  position: absolute;
  inset: 42px 48px;
  border: 1px solid rgba(148, 231, 212, 0.16);
  border-radius: 8px;
  content: "";
  pointer-events: none;
}

.login-side::after {
  position: absolute;
  inset: auto -16% -18% 12%;
  height: 34%;
  background:
    linear-gradient(90deg, rgba(50, 220, 191, 0.18), rgba(255, 195, 84, 0.12), transparent);
  content: "";
  filter: blur(28px);
  opacity: 0.72;
  transform: rotate(-7deg);
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
  color: #f8fbff;
  font-size: 20px;
  line-height: 1.15;
  font-weight: 900;
}

.brand-copy span {
  color: rgba(178, 247, 228, 0.74);
  font-size: 12px;
  font-weight: 700;
}

.side-mark,
.heading-icon {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  border-radius: 8px;
}

.side-mark {
  flex: 0 0 auto;
  border: 1px solid rgba(137, 244, 221, 0.36);
  color: #dffef6;
  background:
    linear-gradient(145deg, rgba(67, 224, 195, 0.2), rgba(43, 106, 255, 0.18)),
    rgba(255, 255, 255, 0.1);
  box-shadow:
    0 16px 30px rgba(0, 0, 0, 0.26),
    0 0 28px rgba(48, 226, 197, 0.14);
}

.side-kicker {
  display: block;
  margin-bottom: 14px;
  color: #76f2d8;
  font-size: 12px;
  font-weight: 800;
}

.login-side h2,
.login-heading h1,
.login-side p {
  margin: 0;
}

.login-side h2 {
  max-width: 620px;
  color: #ffffff;
  font-size: 44px;
  line-height: 1.18;
  font-weight: 900;
  text-shadow: 0 16px 34px rgba(0, 0, 0, 0.28);
}

.login-side p {
  max-width: 560px;
  margin-top: 12px;
  color: rgba(224, 241, 246, 0.76);
  font-size: 14px;
  line-height: 1.9;
}

.ai-visual {
  width: min(560px, 100%);
  height: 222px;
  margin: 4px 0 0;
  border: 1px solid rgba(138, 237, 217, 0.2);
  border-radius: 8px;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.07) 1px, transparent 1px),
    linear-gradient(rgba(255, 255, 255, 0.06) 1px, transparent 1px),
    linear-gradient(135deg, rgba(3, 18, 30, 0.76), rgba(15, 65, 68, 0.58));
  background-size:
    28px 28px,
    28px 28px,
    auto;
  box-shadow: 0 20px 52px rgba(0, 0, 0, 0.2) inset;
}

.ai-core {
  position: absolute;
  top: 72px;
  left: 50%;
  width: 92px;
  height: 92px;
  border: 1px solid rgba(125, 245, 224, 0.42);
  border-radius: 8px;
  background:
    linear-gradient(145deg, rgba(120, 250, 229, 0.24), rgba(255, 200, 86, 0.12)),
    rgba(8, 24, 35, 0.86);
  box-shadow:
    0 0 0 10px rgba(125, 245, 224, 0.04),
    0 24px 42px rgba(0, 0, 0, 0.26);
  transform: translateX(-50%) rotate(45deg);
  animation: corePulse 3.8s ease-in-out infinite;
}

.ai-core span,
.ai-core i {
  position: absolute;
  inset: 18px;
  border-radius: 8px;
  content: "";
}

.ai-core span {
  border: 1px solid rgba(255, 255, 255, 0.42);
}

.ai-core i {
  inset: 30px;
  display: block;
  background: #7ff7e2;
  box-shadow: 0 0 22px rgba(127, 247, 226, 0.85);
}

.trace {
  position: absolute;
  height: 2px;
  border-radius: 999px;
  background: linear-gradient(90deg, transparent, #7ff7e2, #ffc857, transparent);
  opacity: 0.72;
  transform-origin: center;
  animation: traceFlow 3.2s ease-in-out infinite;
}

.trace-one {
  top: 56px;
  left: 44px;
  width: 210px;
  transform: rotate(12deg);
}

.trace-two {
  right: 44px;
  bottom: 62px;
  width: 226px;
  animation-delay: 0.5s;
  transform: rotate(-15deg);
}

.trace-three {
  bottom: 42px;
  left: 112px;
  width: 260px;
  animation-delay: 1s;
}

.pulse-node {
  position: absolute;
  width: 12px;
  height: 12px;
  border: 2px solid rgba(255, 255, 255, 0.8);
  border-radius: 999px;
  background: #0f2f37;
  box-shadow: 0 0 18px rgba(127, 247, 226, 0.75);
  animation: nodeBlink 2.4s ease-in-out infinite;
}

.node-one {
  top: 48px;
  left: 40px;
}

.node-two {
  right: 94px;
  top: 42px;
  animation-delay: 0.6s;
}

.node-three {
  right: 48px;
  bottom: 58px;
  animation-delay: 1.1s;
}

.data-stack {
  position: absolute;
  left: 30px;
  bottom: 28px;
  display: grid;
  gap: 7px;
  width: 78px;
}

.data-stack span {
  height: 5px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(127, 247, 226, 0.92), rgba(255, 200, 87, 0.72));
}

.data-stack span:nth-child(2) {
  width: 62%;
}

.data-stack span:nth-child(3) {
  width: 82%;
}

.data-stack span:nth-child(4) {
  width: 48%;
}

.side-stats {
  width: min(560px, 100%);
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.side-stats div {
  min-height: 84px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 7px;
  padding: 13px;
  border: 1px solid rgba(178, 247, 228, 0.2);
  border-radius: 8px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.11), rgba(255, 255, 255, 0.06)),
    rgba(255, 255, 255, 0.04);
  box-shadow: 0 18px 34px rgba(0, 0, 0, 0.14);
}

.side-stats strong {
  color: #ffffff;
  font-size: 13px;
}

.side-stats span {
  color: rgba(224, 241, 246, 0.68);
  font-size: 12px;
}

.login-main {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 72px 58px 44px;
  overflow: hidden;
  border-left: 1px solid rgba(166, 236, 222, 0.22);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(230, 245, 246, 0.86)),
    rgba(247, 251, 252, 0.82);
  backdrop-filter: blur(24px);
  box-shadow:
    -28px 0 90px rgba(0, 0, 0, 0.22),
    1px 0 0 rgba(255, 255, 255, 0.72) inset;
}

.login-main::before {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(18, 103, 126, 0.06) 1px, transparent 1px),
    linear-gradient(rgba(18, 103, 126, 0.045) 1px, transparent 1px);
  background-size: 34px 34px;
  content: "";
  opacity: 0.58;
  pointer-events: none;
}

.login-main::after {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #34e7c2, #2c7dff, #ffc857);
  content: "";
}

.close-btn,
.icon-btn {
  position: relative;
  z-index: 1;
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
  width: 42px;
  height: 42px;
  color: #425064;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.08);
  transition:
    color 0.16s ease,
    background 0.16s ease,
    transform 0.16s ease;
}

.close-btn:hover,
.icon-btn:hover {
  color: #081523;
  background: #e9f3f5;
  transform: translateY(-1px);
}

.login-heading {
  position: relative;
  z-index: 1;
  width: min(424px, 100%);
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 30px;
}

.heading-icon {
  color: #fff;
  background:
    linear-gradient(145deg, #1238a8, #18bfa5 78%),
    #1e63d6;
  box-shadow:
    0 18px 34px rgba(24, 191, 165, 0.25),
    0 0 0 8px rgba(24, 191, 165, 0.08);
}

.main-kicker {
  display: block;
  margin-bottom: 5px;
  color: #0f8f78;
  font-size: 12px;
  font-weight: 900;
}

.login-heading h1 {
  color: #101a2b;
  font-size: 28px;
  line-height: 1.2;
  font-weight: 900;
}

.login-heading p {
  margin: 4px 0 0;
  color: #5d6b7f;
  font-size: 13px;
}

.login-form {
  position: relative;
  z-index: 1;
  width: min(424px, 100%);
  display: grid;
  gap: 17px;
}

.form-field {
  display: grid;
  gap: 8px;
  color: #182438;
  font-size: 13px;
  font-weight: 900;
}

.input-shell {
  height: 52px;
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 0 14px;
  border: 1px solid rgba(139, 161, 183, 0.34);
  border-radius: 8px;
  color: #607088;
  background: rgba(255, 255, 255, 0.86);
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.9) inset,
    0 14px 32px rgba(16, 36, 54, 0.06);
  transition:
    background 0.16s ease,
    border-color 0.16s ease,
    box-shadow 0.16s ease,
    transform 0.16s ease;
}

.input-shell:focus-within {
  border-color: #19bfa5;
  background: #fff;
  box-shadow:
    0 0 0 4px rgba(25, 191, 165, 0.12),
    0 18px 38px rgba(16, 36, 54, 0.1);
  transform: translateY(-1px);
}

.input-shell input {
  min-width: 0;
  flex: 1;
  border: 0;
  outline: 0;
  color: #101a2b;
  background: transparent;
  font: inherit;
  font-weight: 700;
}

.input-shell input::placeholder {
  color: #94a1b4;
}

.icon-btn {
  width: 30px;
  height: 30px;
  flex: 0 0 auto;
  color: #607088;
  background: transparent;
}

.form-field em {
  color: #b42318;
  font-size: 12px;
  font-style: normal;
  font-weight: 700;
}

.form-field.error .input-shell {
  border-color: #f19b84;
  background: #fff8f4;
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
  position: relative;
  min-width: 0;
  height: 52px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  overflow: hidden;
  border: 0;
  border-radius: 8px;
  color: #fff;
  background:
    linear-gradient(90deg, #0d58d5, #11bfa3 54%, #f0a62e),
    #0d58d5;
  box-shadow:
    0 18px 34px rgba(13, 88, 213, 0.24),
    0 10px 26px rgba(17, 191, 163, 0.16);
  cursor: pointer;
  font: inherit;
  font-size: 15px;
  font-weight: 900;
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
  content: "";
  pointer-events: none;
  transform: translateX(-115%);
  transition: transform 0.55s ease;
}

.submit-btn:hover:not(:disabled) {
  filter: saturate(1.1);
  box-shadow:
    0 22px 42px rgba(13, 88, 213, 0.28),
    0 12px 30px rgba(17, 191, 163, 0.2);
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
  width: min(424px, 100%);
  min-height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 20px;
  padding: 10px 14px;
  border: 1px solid rgba(138, 161, 183, 0.28);
  border-radius: 8px;
  color: #5d6b7f;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.84), rgba(231, 246, 244, 0.78)),
    #f6fbfc;
  box-shadow: 0 14px 32px rgba(16, 36, 54, 0.06);
  font-size: 13px;
}

.demo-account div {
  display: grid;
  gap: 3px;
}

.demo-account strong {
  color: #101a2b;
  font-size: 16px;
  font-weight: 900;
}

.demo-account small {
  flex: 0 0 auto;
  padding: 4px 8px;
  border-radius: 999px;
  color: #0f8f78;
  background: rgba(25, 191, 165, 0.1);
  font-size: 12px;
  font-weight: 900;
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

@keyframes gridDrift {
  to {
    transform: translate3d(54px, 54px, 0);
  }
}

@keyframes scanMove {
  50% {
    transform: translateY(2%);
  }
}

@keyframes corePulse {
  50% {
    box-shadow:
      0 0 0 16px rgba(125, 245, 224, 0.06),
      0 30px 52px rgba(0, 0, 0, 0.3);
    transform: translateX(-50%) rotate(45deg) scale(1.04);
  }
}

@keyframes traceFlow {
  50% {
    opacity: 1;
    filter: drop-shadow(0 0 9px rgba(127, 247, 226, 0.72));
  }
}

@keyframes nodeBlink {
  50% {
    background: #ffc857;
    box-shadow: 0 0 20px rgba(255, 200, 87, 0.82);
  }
}

@media (max-width: 900px) {
  .login-panel {
    min-height: 100dvh;
    grid-template-columns: 1fr;
    overflow: auto;
  }

  .login-side {
    min-height: 520px;
    padding: 32px 24px 28px;
  }

  .login-side h2 {
    max-width: 100%;
    font-size: 28px;
  }

  .login-side p {
    max-width: 100%;
  }

  .ai-visual {
    height: 138px;
  }

  .login-main {
    min-height: 480px;
    padding: 58px 24px 28px;
    border-top: 1px solid rgba(166, 236, 222, 0.18);
    border-left: 0;
    box-shadow: 0 -28px 70px rgba(0, 0, 0, 0.18);
  }
}

@media (max-width: 560px) {
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

  .ai-visual {
    display: none;
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
    font-size: 24px;
  }
}
</style>
