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
                <span>Project Portal</span>
              </div>
            </div>

            <div class="side-hero">
              <span class="side-kicker">1996 CATALOG SHELL</span>
              <h2>进入内部目录册</h2>
              <p>用一张扁平登录卡，打开项目、凭据与访问入口的统一目录。</p>
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
                <strong>INDEX</strong>
                <span>快速索引</span>
              </div>
              <div>
                <strong>NEW!</strong>
                <span>目录更新</span>
              </div>
              <div>
                <strong>SAFE</strong>
                <span>受控访问</span>
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
                <span class="main-kicker">ACCESS PASS</span>
                <h1 id="login-title">目录访问登录</h1>
                <p>使用账号进入项目门户目录册</p>
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
                {{ loading ? '正在索引...' : '进入目录' }}
              </button>
            </form>

            <div class="demo-account">
              <div>
                <span>演示账号</span>
                <strong>admin / 123456</strong>
              </div>
              <small>DEMO</small>
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
  font-family: var(--font-body);
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
    linear-gradient(90deg, rgba(255, 255, 255, 0.08) 1px, transparent 1px),
    linear-gradient(rgba(255, 255, 255, 0.08) 1px, transparent 1px),
    radial-gradient(circle at 20% 30%, rgba(252, 194, 15, 0.18), transparent 22%),
    radial-gradient(circle at 80% 70%, rgba(233, 29, 42, 0.16), transparent 20%);
  background-position:
    center,
    center,
    0 0,
    0 0;
  background-size:
    40px 40px,
    40px 40px,
    100% 100%,
    100% 100%;
  opacity: 0.65;
  animation: gridDrift 22s linear infinite;
}

.login-layer::after {
  background:
    repeating-linear-gradient(
      180deg,
      transparent 0,
      transparent 18px,
      rgba(255, 255, 255, 0.03) 19px,
      transparent 20px
    );
  opacity: 0.4;
  transform: translateY(-2%);
  animation: scanMove 8s ease-in-out infinite;
}

.login-backdrop {
  position: absolute;
  inset: 0;
  border: 0;
  background:
    linear-gradient(115deg, rgba(0, 0, 0, 0.94), rgba(32, 32, 32, 0.92) 42%, rgba(0, 0, 0, 0.95)),
    #000;
}

.login-panel {
  position: relative;
  z-index: 1;
  width: 100%;
  min-height: 100dvh;
  display: grid;
  grid-template-columns: minmax(460px, 1fr) minmax(420px, 520px);
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
  padding: 62px 64px 54px;
  overflow: hidden;
  color: #000;
  background:
    linear-gradient(90deg, rgba(179, 189, 149, 0.9), rgba(255, 255, 255, 0.9) 50%, rgba(214, 122, 122, 0.92)),
    #fff;
  border-right: 4px solid #000;
}

.login-side::before {
  position: absolute;
  inset: 36px 36px 30px;
  border: 3px solid #000;
  content: "";
  pointer-events: none;
}

.login-side::after {
  position: absolute;
  inset: auto -16% -18% 12%;
  height: 34%;
  background:
    linear-gradient(90deg, rgba(233, 29, 42, 0.18), rgba(252, 194, 15, 0.18), transparent);
  content: "";
  filter: blur(24px);
  opacity: 0.55;
  transform: rotate(-6deg);
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
  color: #000;
  font-size: 20px;
  line-height: 1.15;
  font-family: var(--font-display);
  font-weight: 900;
  text-transform: uppercase;
}

.brand-copy span {
  color: #333;
  font-size: 12px;
  font-weight: 700;
}

.side-mark,
.heading-icon {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  border-radius: 0;
}

.side-mark {
  flex: 0 0 auto;
  border: 2px solid #000;
  color: #000;
  background: #fcc20f;
  box-shadow: 3px 3px 0 #000;
}

.side-kicker {
  display: block;
  margin-bottom: 14px;
  color: #000;
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
}

.login-side h2,
.login-heading h1,
.login-side p {
  margin: 0;
}

.login-side h2 {
  max-width: 620px;
  color: #000;
  font-size: 44px;
  line-height: 1.18;
  font-family: var(--font-display);
  font-weight: 900;
  text-transform: uppercase;
}

.login-side p {
  max-width: 560px;
  margin-top: 12px;
  color: #111;
  font-size: 14px;
  line-height: 1.9;
}

.ai-visual {
  width: min(560px, 100%);
  height: 222px;
  margin: 4px 0 0;
  border: 3px solid #000;
  background:
    linear-gradient(90deg, rgba(0, 0, 0, 0.08) 1px, transparent 1px),
    linear-gradient(rgba(0, 0, 0, 0.08) 1px, transparent 1px),
    linear-gradient(135deg, rgba(255, 255, 255, 0.92), rgba(245, 238, 202, 0.96));
  background-size:
    28px 28px,
    28px 28px,
    auto;
  box-shadow: 6px 6px 0 #000;
}

.ai-core {
  position: absolute;
  top: 72px;
  left: 50%;
  width: 92px;
  height: 92px;
  border: 3px solid #000;
  border-radius: 0;
  background:
    linear-gradient(145deg, rgba(252, 194, 15, 0.95), rgba(233, 29, 42, 0.94));
  box-shadow: 6px 6px 0 #000;
  transform: translateX(-50%) rotate(45deg);
  animation: corePulse 3.8s ease-in-out infinite;
}

.ai-core span,
.ai-core i {
  position: absolute;
  inset: 18px;
  border-radius: 0;
  content: "";
}

.ai-core span {
  border: 2px solid #000;
}

.ai-core i {
  inset: 30px;
  display: block;
  background: #fff;
  box-shadow: 0 0 0 2px #000 inset;
}

.trace {
  position: absolute;
  height: 2px;
  border-radius: 0;
  background: linear-gradient(90deg, transparent, #000, #e91d2a, transparent);
  opacity: 0.8;
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
  border: 2px solid #000;
  border-radius: 0;
  background: #fcc20f;
  box-shadow: 3px 3px 0 #000;
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
  border-radius: 0;
  background: linear-gradient(90deg, #000, #e91d2a, #fcc20f);
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
  border: 2px solid #000;
  background: #fff;
  box-shadow: 3px 3px 0 #000;
}

.side-stats strong {
  color: #000;
  font-size: 13px;
  text-transform: uppercase;
}

.side-stats span {
  color: #333;
  font-size: 12px;
}

.login-main {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 64px 50px 42px;
  overflow: hidden;
  border-left: 4px solid #000;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(245, 238, 202, 0.9)),
    #fff;
  box-shadow: -8px 0 0 #000;
}

.login-main::before {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(0, 0, 0, 0.08) 1px, transparent 1px),
    linear-gradient(rgba(0, 0, 0, 0.08) 1px, transparent 1px);
  background-size: 34px 34px;
  content: "";
  opacity: 0.45;
  pointer-events: none;
}

.login-main::after {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #000, #e91d2a, #fcc20f);
  content: "";
}

.close-btn,
.icon-btn {
  position: relative;
  z-index: 1;
  display: inline-grid;
  place-items: center;
  border: 2px solid #000;
  border-radius: 0;
  cursor: pointer;
  font: inherit;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 42px;
  height: 42px;
  color: #000;
  background: #fcc20f;
  box-shadow: 3px 3px 0 #000;
  transition:
    color 0.16s ease,
    background 0.16s ease,
    transform 0.16s ease;
}

.close-btn:hover,
.icon-btn:hover {
  color: #000;
  background: #e91d2a;
  transform: translate(-1px, -1px);
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
  color: #000;
  background: #fcc20f;
  box-shadow: 3px 3px 0 #000;
}

.main-kicker {
  display: block;
  margin-bottom: 5px;
  color: #000;
  font-size: 12px;
  font-weight: 900;
  text-transform: uppercase;
}

.login-heading h1 {
  color: #000;
  font-size: 28px;
  line-height: 1.2;
  font-family: var(--font-display);
  font-weight: 900;
  text-transform: uppercase;
}

.login-heading p {
  margin: 4px 0 0;
  color: #222;
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
  color: #000;
  font-size: 13px;
  font-weight: 900;
}

.input-shell {
  height: 52px;
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 0 14px;
  border: 2px solid #000;
  border-radius: 0;
  color: #000;
  background: #fff;
  box-shadow: 3px 3px 0 #000;
  transition:
    background 0.16s ease,
    border-color 0.16s ease,
    box-shadow 0.16s ease,
    transform 0.16s ease;
}

.input-shell:focus-within {
  background: #fff;
  box-shadow: 4px 4px 0 #000;
  transform: translate(-1px, -1px);
}

.input-shell input {
  min-width: 0;
  flex: 1;
  border: 0;
  outline: 0;
  color: #000;
  background: transparent;
  font: inherit;
  font-weight: 700;
}

.input-shell input::placeholder {
  color: #555;
}

.icon-btn {
  width: 30px;
  height: 30px;
  flex: 0 0 auto;
  color: #000;
  background: transparent;
}

.form-field em {
  color: #e91d2a;
  font-size: 12px;
  font-style: normal;
  font-weight: 700;
}

.form-field.error .input-shell {
  background: #fff1ed;
}

.error-banner {
  min-height: 38px;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border: 2px solid #000;
  border-radius: 0;
  color: #000;
  background: #d77a7a;
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
  border: 2px solid #000;
  border-radius: 0;
  color: #fff;
  background: #e91d2a;
  box-shadow: 4px 4px 0 #000;
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
  background: linear-gradient(110deg, transparent 0 34%, rgba(255, 255, 255, 0.28) 42%, transparent 52%);
  content: "";
  pointer-events: none;
  transform: translateX(-115%);
  transition: transform 0.55s ease;
}

.submit-btn:hover:not(:disabled) {
  filter: saturate(1.05);
  box-shadow: 6px 6px 0 #000;
  transform: translate(-1px, -1px);
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
  border: 2px solid rgba(255, 255, 255, 0.45);
  border-top-color: #fff;
  border-radius: 0;
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
  border: 2px solid #000;
  border-radius: 0;
  color: #000;
  background: #fff;
  box-shadow: 3px 3px 0 #000;
  font-size: 13px;
}

.demo-account div {
  display: grid;
  gap: 3px;
}

.demo-account strong {
  color: #000;
  font-size: 16px;
  font-weight: 900;
}

.demo-account small {
  flex: 0 0 auto;
  padding: 4px 8px;
  border: 2px solid #000;
  border-radius: 0;
  color: #000;
  background: #fcc20f;
  font-size: 12px;
  font-weight: 900;
  text-transform: uppercase;
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
    box-shadow: 8px 8px 0 #000;
    transform: translateX(-50%) rotate(45deg) scale(1.03);
  }
}

@keyframes traceFlow {
  50% {
    opacity: 1;
    filter: none;
  }
}

@keyframes nodeBlink {
  50% {
    background: #e91d2a;
    box-shadow: 4px 4px 0 #000;
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
    border-top: 4px solid #000;
    border-left: 0;
    box-shadow: 0 -6px 0 #000;
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
