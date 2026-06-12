<template>
  <section class="portal-shell">
    <div v-if="toastMessage" class="portal-toast">{{ toastMessage }}</div>

    <div class="portal-main">
      <section class="hero-band">
        <div class="hero-copy">
          <div class="eyebrow-row">
            <span class="eyebrow-dot"></span>
            <span>公司一体化平台</span>
          </div>
          <h1>项目统一门户</h1>
          <p>把项目地址、账号凭据、小程序入口和运行状态集中到一个清爽的内部工作台。</p>
        </div>

        <div class="hero-metrics">
          <div class="metric-card">
            <span>授权项目</span>
            <strong>{{ store.projects.length }}</strong>
            <small>当前可访问资源</small>
          </div>
          <div class="metric-card">
            <span>运行可用</span>
            <strong>{{ statusCounts['可用'] }}</strong>
            <small>最近检测正常</small>
          </div>
          <div class="metric-card metric-card-warn">
            <span>需关注</span>
            <strong>{{ attentionCount }}</strong>
            <small>异常或维护中</small>
          </div>
        </div>
      </section>

      <section class="filter-panel">
        <div class="search-control">
          <ElIcon><Search /></ElIcon>
          <input
            v-model="store.filters.keyword"
            placeholder="搜索项目名称、简称、标签或说明"
            @keyup.enter="loadProjects"
          />
        </div>
        <select v-model="store.filters.category" class="filter-select">
          <option value="">全部分类</option>
          <option v-for="item in categoryOptions" :key="item" :value="item">{{ item }}</option>
        </select>
        <select v-model="store.filters.status" class="filter-select">
          <option value="">全部状态</option>
          <option v-for="item in statusOptions" :key="item" :value="item">{{ item }}</option>
        </select>
        <button type="button" class="primary-btn" @click="loadProjects">
          <ElIcon><Search /></ElIcon>
          查询
        </button>
        <button type="button" class="ghost-btn" @click="resetFilters">
          <ElIcon><Refresh /></ElIcon>
          重置
        </button>
      </section>

      <div class="overview-strip">
        <button
          v-for="item in statusSummary"
          :key="item.status"
          class="status-summary"
          :class="[statusClass(item.status), { active: store.filters.status === item.status }]"
          type="button"
          @click="setStatusFilter(item.status)"
        >
          <span>
            <i class="summary-dot"></i>
            {{ item.status }}
          </span>
          <strong>{{ item.count }}</strong>
        </button>
      </div>

      <section class="content-header">
        <div class="content-copy">
          <span class="content-kicker">Project Library</span>
          <h2>把项目入口、凭据与说明整理成一份可检索的工作台</h2>
        </div>
        <span class="content-count">{{ store.projects.length }} 个已授权项目</span>
      </section>

      <div class="content-grid">
        <main class="project-zone">
          <div v-if="store.loading" class="state-box">加载中...</div>
          <div v-else-if="store.projects.length === 0" class="state-box">暂无可访问项目，请联系管理员授权</div>
          <div v-else class="project-grid">
            <article
              v-for="project in store.projects"
              :key="project.id"
              class="project-card"
              :class="{ attention: project.status !== '可用' }"
            >
              <div class="card-top">
                <div class="logo-wrap">
                  <img :src="project.logo" :alt="project.name" class="project-logo" />
                </div>
                <span class="category-chip">{{ project.category }}</span>
              </div>

              <div class="card-title-line">
                <div>
                  <h2>{{ project.name }}</h2>
                  <span>{{ project.shortName }}</span>
                </div>
                <span :class="['status-pill', statusClass(project.status)]">
                  <i class="status-dot"></i>
                  {{ project.status }}
                </span>
              </div>

              <p class="project-desc">{{ project.description }}</p>

              <div class="tag-row">
                <span v-for="tag in project.tags" :key="tag" class="tag-chip">{{ tag }}</span>
              </div>

              <button type="button" class="detail-btn" @click="openProject(project.id)">
                <ElIcon><View /></ElIcon>
                查看详情
              </button>
            </article>
          </div>
        </main>
      </div>
    </div>
  </section>

  <div v-if="detailVisible" class="modal-mask" @click.self="closeDetail">
    <section class="detail-modal">
      <button type="button" class="modal-close-btn" aria-label="Close detail" @click="closeDetail">
        <ElIcon><Close /></ElIcon>
      </button>
      <aside class="detail-aside">
        <div class="detail-logo-wrap">
          <img :src="detail?.project.logo" :alt="detail?.project.name" />
        </div>
        <h2>{{ detail?.project.name }}</h2>
        <p>{{ detail?.project.description }}</p>
        <div v-if="detail" class="detail-status-card">
          <span :class="['status-pill', statusClass(detail.project.status)]">{{ detail.project.status }}</span>
          <strong>{{ detail.project.responseTime ? `${detail.project.responseTime}ms` : '未检测' }}</strong>
          <small>{{ detail.project.lastCheckTime || '暂无检测时间' }}</small>
        </div>
      </aside>

      <div class="detail-content">
        <nav class="detail-tabs">
          <button v-for="tab in tabs" :key="tab.key" type="button" :class="{ active: activeTab === tab.key }" @click="activeTab = tab.key">
            <ElIcon>
              <component :is="tab.icon" />
            </ElIcon>
            {{ tab.label }}
          </button>
        </nav>

        <div v-if="store.detailLoading" class="modal-state">加载中...</div>
        <div v-else-if="detail" class="detail-body">
          <section v-if="activeTab === 'basic'" class="detail-section">
            <dl class="info-list">
              <div><dt>项目分类</dt><dd>{{ detail.project.category }}</dd></div>
              <div><dt>项目标签</dt><dd>{{ detail.project.tags.join('、') }}</dd></div>
              <div><dt>维护人</dt><dd>{{ detail.project.maintainer }}</dd></div>
              <div><dt>响应耗时</dt><dd>{{ detail.project.responseTime ? `${detail.project.responseTime}ms` : '未检测' }}</dd></div>
              <div><dt>最近检测</dt><dd>{{ detail.project.lastCheckTime || '暂无检测时间' }}</dd></div>
              <div><dt>异常原因</dt><dd>{{ detail.project.abnormalReason || '-' }}</dd></div>
            </dl>
          </section>

          <section v-if="activeTab === 'entry'" class="detail-section">
            <div class="entry-group">
              <div class="section-heading">
                <strong>访问入口</strong>
                <span>{{ projectEntries.length }} 个入口</span>
              </div>
              <div v-if="projectEntries.length === 0" class="modal-state">暂未配置访问入口</div>
              <div v-else class="entry-table">
                <div
                  v-for="entry in projectEntries"
                  :key="entry.key"
                  class="resource-row entry-row"
                  :class="{
                    'entry-qrcode-row': entry.kind === 'qrcode',
                    'entry-plain-row': entry.kind === 'address' && entry.credentials.length === 0,
                    'entry-no-credential-row': entry.credentials.length === 0,
                  }"
                >
                  <span class="entry-type">{{ entry.typeLabel }}</span>

                  <div class="entry-address-cell">
                    <strong>{{ entry.name }}</strong>
                    <span>{{ entry.description }}</span>
                    <div v-if="entry.kind === 'qrcode'" class="entry-qrcode-inline">
                      <img :src="entry.image" :alt="entry.name" class="entry-qrcode-thumb" />
                      <span>{{ entry.audience }}</span>
                    </div>
                    <div v-if="entry.kind === 'address'" class="resource-actions entry-actions">
                      <button type="button" @click="openAddress(entry.url)">
                        <ElIcon><Link /></ElIcon>
                        打开
                      </button>
                      <button type="button" @click="copyText(entry.url, '地址已复制')">
                        <ElIcon><CopyDocument /></ElIcon>
                        复制
                      </button>
                    </div>
                  </div>

                  <div class="entry-credential-cell">
                    <span v-if="entry.credentials.length === 0" class="entry-empty">无账号</span>
                    <div
                      v-for="credential in entry.credentials"
                      :key="credential.id"
                      class="entry-credential-inline"
                    >
                      <strong>{{ credential.name }} · {{ credential.environment }}</strong>
                      <div class="entry-account-lines">
                        <span>账号：{{ credential.username }}</span>
                        <span>密码：{{ visiblePassword(credential) }}</span>
                      </div>
                      <div class="resource-actions credential-actions">
                        <button type="button" @click="copyCredential(credential.id, 'username')">
                          <ElIcon><CopyDocument /></ElIcon>
                          账号
                        </button>
                        <button type="button" @click="copyCredential(credential.id, 'password')">
                          <ElIcon><Key /></ElIcon>
                          密码
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section v-if="activeTab === 'instruction'" class="detail-section">
            <dl class="info-list instruction-list">
              <div><dt>浏览器要求</dt><dd>{{ detail.instruction.browserRequirement }}</dd></div>
              <div><dt>VPN 要求</dt><dd>{{ detail.instruction.vpnRequirement }}</dd></div>
              <div><dt>注意事项</dt><dd>{{ detail.instruction.notes }}</dd></div>
              <div><dt>维护联系人</dt><dd>{{ detail.instruction.maintainer }} · {{ detail.instruction.contactPhone }}</dd></div>
            </dl>
          </section>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  Close,
  CopyDocument,
  Key,
  Link,
  Monitor,
  Reading,
  Refresh,
  Search,
  View,
} from '@element-plus/icons-vue'
import { usePortalStore } from '@/stores/portal'
import type { ProjectAddress, ProjectCategory, ProjectCredential, ProjectStatus } from '@/types/project'

type ProjectEntry =
  | {
      key: string
      kind: 'address'
      id: number
      typeLabel: string
      name: string
      description: string
      url: string
      credentials: ProjectCredential[]
    }
  | {
      key: string
      kind: 'qrcode'
      id: number
      typeLabel: string
      name: string
      description: string
      image: string
      audience: string
      credentials: ProjectCredential[]
    }

const store = usePortalStore()
const detailVisible = ref(false)
const activeTab = ref('entry')
const revealedPasswords = ref<Record<number, string>>({})
const toastMessage = ref('')
let toastTimer: number | undefined
let passwordLoadToken = 0

const categoryOptions: ProjectCategory[] = ['内部系统', '客户项目', 'AI工具', '数据平台', '运维服务', '小程序']
const statusOptions: ProjectStatus[] = ['可用', '异常', '维护中', '未检测']
const tabs = [
  { key: 'entry', label: '访问入口', icon: Link },
  { key: 'basic', label: '基本信息', icon: Monitor },
  { key: 'instruction', label: '访问说明', icon: Reading },
]

const detail = computed(() => store.detail)
const projectEntries = computed<ProjectEntry[]>(() => {
  if (!detail.value) return []

  const addressEntries: ProjectEntry[] = detail.value.addresses.map((address) => ({
    key: `address-${address.id}`,
    kind: 'address',
    id: address.id,
    typeLabel: address.type,
    name: address.name,
    description: address.url,
    url: address.url,
    credentials: credentialsForAddress(address.id),
  }))

  const qrcodeEntries: ProjectEntry[] = detail.value.qrcodes.map((qrcode) => ({
    key: `qrcode-${qrcode.id}`,
    kind: 'qrcode',
    id: qrcode.id,
    typeLabel: '小程序',
    name: qrcode.name,
    description: qrcode.description,
    image: qrcode.image,
    audience: qrcode.audience,
    credentials: [],
  }))

  return [...addressEntries, ...qrcodeEntries]
})
const credentialsByAddressId = computed<Record<number, ProjectCredential[]>>(() => {
  if (!detail.value) return {}

  const result = detail.value.addresses.reduce<Record<number, ProjectCredential[]>>((groups, address) => {
    groups[address.id] = []
    return groups
  }, {})
  const fallbackAddress = detail.value.addresses.find((address) => address.isDefault === 1) || detail.value.addresses[0]

  for (const credential of detail.value.credentials) {
    const matchedAddress =
      detail.value.addresses.find((address) => isCredentialForAddress(credential, address)) || fallbackAddress
    if (matchedAddress) {
      result[matchedAddress.id] = [...(result[matchedAddress.id] || []), credential]
    }
  }

  return result
})
const statusCounts = computed<Record<ProjectStatus, number>>(() => {
  return statusOptions.reduce((result, status) => {
    result[status] = store.projects.filter((project) => project.status === status).length
    return result
  }, {} as Record<ProjectStatus, number>)
})
const statusSummary = computed(() => statusOptions.map((status) => ({ status, count: statusCounts.value[status] || 0 })))
const attentionCount = computed(() => statusCounts.value['异常'] + statusCounts.value['维护中'])

function statusClass(status: ProjectStatus) {
  return {
    available: status === '可用',
    error: status === '异常',
    maintenance: status === '维护中',
    unchecked: status === '未检测',
  }
}

function credentialsForAddress(addressId: number) {
  return credentialsByAddressId.value[addressId] || []
}

function visiblePassword(credential: ProjectCredential) {
  const credentialWithPassword = credential as ProjectCredential & { password?: string }
  return revealedPasswords.value[credential.id] || credentialWithPassword.password || credential.passwordMasked || '************'
}

function isCredentialForAddress(credential: ProjectCredential, address: ProjectAddress) {
  const credentialWithAddress = credential as ProjectCredential & { addressId?: number }
  if (credentialWithAddress.addressId !== undefined) return credentialWithAddress.addressId === address.id

  const addressText = `${address.name} ${address.type}`.toLowerCase()
  const credentialText = `${credential.name} ${credential.environment}`.toLowerCase()
  const addressKeywords = keywordsForAddress(address)
  const credentialKeywords = keywordsForCredential(credential)

  return (
    credentialKeywords.some((keyword) => addressText.includes(keyword)) ||
    addressKeywords.some((keyword) => credentialText.includes(keyword))
  )
}

function keywordsForAddress(address: ProjectAddress) {
  const source = `${address.name} ${address.type}`.toLowerCase()
  const keywords: string[] = []
  if (source.includes('正式')) keywords.push('正式')
  if (source.includes('测试')) keywords.push('测试')
  if (source.includes('演示')) keywords.push('演示')
  if (source.includes('后台')) keywords.push('后台', '管理')
  if (source.includes('文档')) keywords.push('文档')
  return keywords
}

function keywordsForCredential(credential: ProjectCredential) {
  const source = `${credential.name} ${credential.environment}`.toLowerCase()
  const keywords: string[] = []
  if (source.includes('正式')) keywords.push('正式')
  if (source.includes('测试')) keywords.push('测试')
  if (source.includes('演示')) keywords.push('演示')
  if (source.includes('后台') || source.includes('管理')) keywords.push('后台', '管理')
  if (source.includes('文档')) keywords.push('文档')
  return keywords
}

async function loadProjects() {
  await store.loadProjects()
}

function setStatusFilter(status: ProjectStatus) {
  store.filters.status = store.filters.status === status ? '' : status
  loadProjects()
}

function resetFilters() {
  store.filters = { keyword: '', category: '', status: '' }
  loadProjects()
}

async function openProject(projectId: number) {
  revealedPasswords.value = {}
  activeTab.value = 'entry'
  detailVisible.value = true
  const token = ++passwordLoadToken
  await store.openDetail(projectId)
  void loadVisiblePasswords(token)
}

function closeDetail() {
  detailVisible.value = false
}

function openAddress(url: string) {
  window.open(url, '_blank', 'noopener,noreferrer')
}

async function copyText(text: string, message: string) {
  await navigator.clipboard?.writeText(text)
  showToast(message)
}

function showToast(message: string) {
  toastMessage.value = message
  if (toastTimer) {
    window.clearTimeout(toastTimer)
  }
  toastTimer = window.setTimeout(() => {
    toastMessage.value = ''
  }, 1800)
}

async function copyCredential(id: number, field: 'username' | 'password') {
  const text = await store.copyCredential(id, field)
  await copyText(text, field === 'password' ? '密码已复制' : '账号已复制')
}

async function loadVisiblePasswords(token: number) {
  const credentials = (detail.value?.credentials || []).filter((credential) => !credential.password)
  if (credentials.length === 0) return

  const passwordEntries = await Promise.all(
    credentials.map(async (credential) => {
      try {
        return [credential.id, await store.revealPassword(credential.id)] as const
      } catch {
        return [credential.id, credential.passwordMasked || '************'] as const
      }
    }),
  )
  if (token !== passwordLoadToken) return
  revealedPasswords.value = {
    ...revealedPasswords.value,
    ...Object.fromEntries(passwordEntries),
  }
}

onMounted(loadProjects)
</script>

<style scoped>
.portal-shell {
  min-height: 100vh;
  position: relative;
  padding: 96px 16px 64px;
  color: var(--portal-ink-soft);
}

.portal-shell::before {
  content: '';
  position: fixed;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at 13% 18%, rgba(214, 182, 246, 0.12), transparent 24%),
    radial-gradient(circle at 88% 11%, rgba(98, 174, 240, 0.1), transparent 18%);
}

.portal-toast {
  position: fixed;
  top: 80px;
  left: 50%;
  z-index: 90;
  transform: translateX(-50%);
  padding: 11px 16px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 12px;
  color: #fff;
  background: rgba(0, 0, 0, 0.92);
  box-shadow: var(--portal-shadow-elevated);
  font-size: 13px;
  line-height: 1;
}

.portal-main {
  position: relative;
  z-index: 1;
  width: min(var(--portal-max-width), 100%);
  margin: 0 auto;
}

.hero-band {
  position: relative;
  min-height: 292px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 392px;
  gap: 28px;
  align-items: center;
  padding: 36px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 20px;
  color: #fff;
  background:
    radial-gradient(circle at 86% 18%, rgba(98, 174, 240, 0.26), transparent 26%),
    radial-gradient(circle at 64% 80%, rgba(214, 182, 246, 0.18), transparent 24%),
    linear-gradient(140deg, #213183, #17275f 55%, #101b47 100%);
  box-shadow: var(--portal-shadow-elevated);
  overflow: hidden;
}

.hero-band::before,
.hero-band::after {
  content: '';
  position: absolute;
  border-radius: 18px;
  opacity: 0.9;
}

.hero-band::before {
  top: 28px;
  right: 116px;
  width: 76px;
  height: 76px;
  background: linear-gradient(145deg, rgba(255, 100, 200, 0.92), rgba(214, 182, 246, 0.92));
  transform: rotate(-8deg);
}

.hero-band::after {
  right: 44px;
  bottom: 26px;
  width: 88px;
  height: 88px;
  background: linear-gradient(145deg, rgba(42, 157, 153, 0.9), rgba(98, 174, 240, 0.9));
  transform: rotate(12deg);
}

.hero-copy {
  position: relative;
  z-index: 1;
  align-self: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-width: 640px;
}

.eyebrow-row {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  padding: 6px 12px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 999px;
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.eyebrow-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: #fff;
  box-shadow: 0 0 0 5px rgba(255, 255, 255, 0.12);
}

.hero-copy h1,
.detail-aside h2,
.project-card h2,
.hero-copy p {
  margin: 0;
}

.hero-copy h1 {
  margin-top: 20px;
  color: #fff;
  font-size: clamp(38px, 5vw, 56px);
  line-height: 1.02;
  font-weight: 700;
  letter-spacing: -0.055em;
}

.hero-copy p {
  max-width: 540px;
  margin-top: 14px;
  color: rgba(255, 255, 255, 0.78);
  font-size: 16px;
  line-height: 1.7;
}

.hero-metrics {
  position: relative;
  z-index: 1;
  align-self: center;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.hero-metrics .metric-card:first-child {
  grid-column: 1 / -1;
}

.metric-card {
  min-height: 122px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
  padding: 18px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 16px;
  color: var(--portal-ink-soft);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 245, 242, 0.98)),
    #fff;
  box-shadow: var(--portal-shadow-soft);
}

.metric-card span {
  color: var(--portal-faint);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.metric-card small {
  color: var(--portal-muted);
  font-size: 12px;
}

.metric-card strong {
  color: var(--portal-ink);
  font-size: 36px;
  line-height: 1;
  letter-spacing: -0.04em;
}

.metric-card-warn {
  background: linear-gradient(180deg, rgba(255, 247, 235, 0.98), rgba(255, 255, 255, 0.98));
}

.metric-card-warn strong {
  color: var(--portal-orange);
}

.filter-panel {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  width: 100%;
  margin-top: -22px;
  padding: 16px;
  border: 1px solid var(--portal-hairline);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: var(--portal-shadow-soft);
  backdrop-filter: blur(16px);
}

.search-control,
.filter-select {
  height: 46px;
  border: 1px solid #dfdbd6;
  border-radius: 10px;
  background: #fff;
}

.search-control {
  width: auto;
  flex: 1 1 320px;
  min-width: 280px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  color: var(--portal-faint);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.search-control input,
.filter-select {
  width: 100%;
  border: 0;
  outline: 0;
  color: var(--portal-ink-soft);
  font-size: 15px;
}

.search-control input::placeholder {
  color: var(--portal-faint);
}

.filter-select {
  width: 150px;
  flex: 0 0 150px;
  padding: 0 12px;
  color: var(--portal-ink-soft);
}

button {
  border: 0;
  cursor: pointer;
  font: inherit;
}

.primary-btn,
.ghost-btn,
.detail-btn,
.resource-actions button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  min-height: 42px;
  padding: 0 18px;
  font-size: 14px;
  font-weight: 600;
}

.primary-btn,
.ghost-btn {
  flex: 0 0 auto;
  min-width: 82px;
  white-space: nowrap;
}

.primary-btn,
.detail-btn {
  color: #fff;
  border-radius: 999px;
  background: var(--portal-primary);
  box-shadow: 0 12px 26px rgba(0, 117, 222, 0.18);
}

.ghost-btn,
.resource-actions button {
  color: var(--portal-ink-soft);
  border: 1px solid #ece7e2;
  border-radius: 999px;
  background: #f7f5f2;
}

.primary-btn:hover,
.detail-btn:hover,
.ghost-btn:hover,
.resource-actions button:hover {
  transform: translateY(-1px);
}

.primary-btn:hover,
.detail-btn:hover {
  background: var(--portal-primary-active);
}

.overview-strip {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  width: fit-content;
  max-width: 100%;
  margin-top: 18px;
}

.status-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: clamp(176px, 15vw, 220px);
  min-height: 54px;
  padding: 0 16px;
  border: 1px solid var(--portal-hairline);
  border-radius: 16px;
  color: var(--portal-ink-soft);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: var(--portal-shadow-soft);
}

.status-summary:hover,
.status-summary.active {
  transform: translateY(-1px);
  border-color: rgba(0, 117, 222, 0.28);
  background: #fff;
  box-shadow: var(--portal-shadow-soft);
}

.status-summary span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--portal-muted);
}

.status-summary strong {
  font-size: 22px;
  line-height: 1;
  letter-spacing: -0.03em;
}

.summary-dot,
.status-dot {
  display: inline-block;
  border-radius: 999px;
  background: currentColor;
}

.summary-dot {
  width: 8px;
  height: 8px;
  box-shadow: 0 0 0 4px color-mix(in srgb, currentColor 14%, transparent);
}

.status-dot {
  width: 7px;
  height: 7px;
}

.content-grid {
  margin-top: 18px;
}

.content-header {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 24px;
  margin-top: 34px;
}

.content-copy {
  display: grid;
  gap: 10px;
}

.content-kicker {
  color: var(--portal-primary);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.03em;
  text-transform: uppercase;
}

.content-copy h2 {
  margin: 0;
  color: var(--portal-ink);
  font-size: clamp(28px, 3vw, 36px);
  line-height: 1.1;
  font-weight: 700;
  letter-spacing: -0.04em;
}

.content-count {
  display: inline-flex;
  align-items: center;
  min-height: 38px;
  padding: 0 14px;
  border: 1px solid #ece7e2;
  border-radius: 999px;
  color: var(--portal-ink-soft);
  background: rgba(255, 255, 255, 0.88);
  font-size: 13px;
  font-weight: 600;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(290px, 1fr));
  gap: 16px;
}

.project-card {
  position: relative;
  display: flex;
  min-height: 292px;
  flex-direction: column;
  padding: 20px;
  border: 1px solid var(--portal-hairline);
  border-radius: 18px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(249, 247, 244, 0.98)),
    #fff;
  box-shadow: var(--portal-shadow-soft);
  overflow: hidden;
}

.project-card:hover {
  transform: translateY(-3px);
  border-color: #d9d4ce;
  box-shadow: var(--portal-shadow-elevated);
}

.project-card.attention {
  border-color: rgba(221, 91, 0, 0.22);
}

.project-card.attention:hover {
  border-color: rgba(221, 91, 0, 0.34);
}

.card-top,
.card-title-line,
.resource-row,
.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-wrap,
.detail-logo-wrap {
  position: relative;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: #f7f5f2;
  border: 1px solid #ece7e2;
}

.logo-wrap {
  width: 58px;
  height: 58px;
}

.logo-wrap::after {
  content: '';
  position: absolute;
  right: -4px;
  bottom: -4px;
  width: 14px;
  height: 14px;
  border-radius: 999px;
  background: var(--portal-sky);
  box-shadow: 0 0 0 6px rgba(98, 174, 240, 0.12);
}

.project-logo,
.detail-logo-wrap img {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  object-fit: cover;
}

.modal-close-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  color: var(--portal-muted);
  border: 1px solid var(--portal-hairline);
  background: rgba(255, 255, 255, 0.92);
}

.card-title-line {
  gap: 14px;
  margin-top: 18px;
  align-items: flex-start;
}

.card-title-line h2 {
  color: var(--portal-ink);
  font-size: 20px;
  line-height: 1.18;
  font-weight: 700;
  letter-spacing: -0.03em;
}

.card-title-line div > span,
.project-desc,
.modal-state,
.resource-row span,
.resource-row small,
.info-list dt,
.detail-aside p,
.detail-status-card small,
.section-heading span {
  color: var(--portal-muted);
  font-size: 13px;
}

.card-title-line div > span {
  display: inline-block;
  margin-top: 6px;
  color: var(--portal-faint);
}

.project-desc {
  min-height: 48px;
  display: -webkit-box;
  margin-top: 12px;
  overflow: hidden;
  line-height: 1.65;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 28px;
  margin-top: 14px;
}

.category-chip,
.tag-chip,
.status-pill {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  min-height: 26px;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 600;
}

.status-pill {
  gap: 6px;
}

.category-chip {
  color: var(--portal-primary);
  background: rgba(0, 117, 222, 0.08);
}

.tag-chip {
  color: var(--portal-muted);
  border: 1px solid #ece7e2;
  background: #f7f5f2;
}

.available {
  color: var(--portal-green);
}

.error {
  color: #b53f24;
}

.maintenance {
  color: var(--portal-orange);
}

.unchecked {
  color: var(--portal-faint);
}

.status-pill.available {
  background: rgba(26, 174, 57, 0.12);
}

.status-pill.error {
  background: rgba(181, 63, 36, 0.12);
}

.status-pill.maintenance {
  background: rgba(221, 91, 0, 0.12);
}

.status-pill.unchecked {
  background: rgba(163, 158, 152, 0.16);
}

.detail-btn {
  width: 100%;
  min-height: 44px;
  margin-top: auto;
}

.state-box,
.modal-state {
  padding: 28px 22px;
  text-align: center;
}

.state-box {
  border: 1px solid var(--portal-hairline);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: var(--portal-shadow-soft);
}

.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 80;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px 20px;
  background: rgba(18, 20, 28, 0.44);
  backdrop-filter: blur(16px);
}

.detail-modal {
  position: relative;
  width: min(1120px, calc(100vw - 48px));
  height: min(620px, calc(100vh - 48px));
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.34);
  border-radius: 24px;
  background: #fff;
  box-shadow: var(--portal-shadow-elevated);
}

.detail-aside {
  position: relative;
  padding: 28px;
  color: #fff;
  background:
    radial-gradient(circle at 78% 20%, rgba(98, 174, 240, 0.28), transparent 20%),
    radial-gradient(circle at 30% 88%, rgba(214, 182, 246, 0.2), transparent 26%),
    linear-gradient(160deg, #213183, #182764 60%, #101a47);
}

.detail-aside::before {
  content: '';
  position: absolute;
  inset: auto 28px 30px auto;
  width: 84px;
  height: 84px;
  border-radius: 18px;
  background: linear-gradient(145deg, rgba(255, 100, 200, 0.86), rgba(214, 182, 246, 0.92));
  transform: rotate(10deg);
  opacity: 0.92;
}

.modal-close-btn {
  position: absolute;
  top: 16px;
  right: 18px;
  z-index: 3;
  color: var(--portal-muted);
  background: rgba(255, 255, 255, 0.94);
  box-shadow: var(--portal-shadow-soft);
}

.detail-logo-wrap {
  width: 82px;
  height: 82px;
  margin-top: 22px;
  border-color: rgba(255, 255, 255, 0.18);
  background: rgba(255, 255, 255, 0.14);
}

.detail-aside h2 {
  margin-top: 18px;
  font-size: 28px;
  line-height: 1.12;
  font-weight: 700;
  letter-spacing: -0.03em;
}

.detail-aside p {
  margin-top: 10px;
  max-width: 240px;
  color: rgba(255, 255, 255, 0.76);
  line-height: 1.7;
}

.detail-status-card {
  display: grid;
  gap: 10px;
  margin-top: 24px;
  padding: 16px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.detail-status-card strong {
  font-size: 28px;
  line-height: 1;
  letter-spacing: -0.04em;
}

.detail-status-card small {
  color: rgba(255, 255, 255, 0.66);
}

.detail-content {
  position: relative;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: var(--portal-canvas);
}

.detail-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 18px 78px 18px 18px;
  border-bottom: 1px solid var(--portal-hairline);
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(14px);
}

.detail-tabs button {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  min-height: 40px;
  padding: 0 14px;
  border: 1px solid var(--portal-hairline);
  border-radius: 999px;
  color: var(--portal-muted);
  background: #fff;
  box-shadow: var(--portal-shadow-soft);
}

.detail-tabs button.active {
  color: #fff;
  border-color: var(--portal-primary);
  background: var(--portal-primary);
}

.detail-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
  overflow-x: hidden;
  padding: 20px;
}

.detail-section {
  min-height: auto;
}

.info-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin: 0;
}

.info-list div,
.resource-row {
  border: 1px solid var(--portal-hairline);
  border-radius: 18px;
  background: #fff;
  box-shadow: var(--portal-shadow-soft);
}

.info-list div {
  min-height: 104px;
  padding: 18px;
}

.info-list dt {
  color: var(--portal-faint);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.info-list dd {
  margin: 8px 0 0;
  color: var(--portal-ink-soft);
  line-height: 1.7;
}

.instruction-list div:nth-child(3) {
  grid-column: 1 / -1;
}

.entry-group,
.resource-list,
.entry-table {
  display: grid;
  gap: 14px;
}

.entry-row {
  display: grid;
  grid-template-columns: 84px minmax(0, 1.05fr) minmax(0, 0.95fr);
  justify-content: stretch;
  align-items: center;
  min-height: 88px;
}

.entry-no-credential-row {
  grid-template-columns: 84px minmax(0, 1fr);
}

.entry-qrcode-row {
  min-height: 108px;
}

.entry-plain-row {
  min-height: 68px;
}

.entry-address-cell {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  grid-template-rows: auto auto;
  column-gap: 16px;
  row-gap: 6px;
  align-items: center;
  min-width: 0;
}

.entry-address-cell .entry-actions {
  grid-column: 2;
  grid-row: 1 / span 2;
  align-self: center;
  justify-self: end;
  justify-content: flex-end;
}

.entry-type {
  align-self: center;
  width: fit-content;
  min-width: 56px;
  padding: 6px 10px;
  border-radius: 999px;
  text-align: center;
  color: var(--portal-primary);
  background: rgba(0, 117, 222, 0.08);
  font-size: 12px;
  font-weight: 600;
}

.entry-credential-cell {
  min-width: 0;
}

.entry-credential-inline {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  grid-template-rows: auto auto;
  column-gap: 16px;
  row-gap: 6px;
  align-items: center;
  min-width: 0;
}

.entry-account-lines {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 14px;
  min-width: 0;
}

.entry-empty {
  display: inline-flex;
  align-items: center;
  min-height: 32px;
  color: var(--portal-faint);
  font-size: 13px;
}

.entry-actions,
.credential-actions {
  flex-wrap: nowrap;
  justify-content: flex-end;
}

.credential-actions {
  grid-column: 2;
  grid-row: 1 / span 2;
  align-self: center;
  justify-self: end;
}

.entry-qrcode-inline {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.entry-qrcode-inline span {
  overflow-wrap: anywhere;
}

.entry-no-credential-row .entry-credential-cell {
  display: none;
}

.entry-plain-row .entry-address-cell {
  display: flex;
  align-items: center;
  gap: 16px;
}

.entry-plain-row .entry-address-cell > strong,
.entry-plain-row .entry-address-cell > span {
  flex: 0 0 auto;
}

.entry-plain-row .entry-address-cell > span {
  flex: 1 1 auto;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.entry-plain-row .entry-actions {
  flex: 0 0 auto;
  margin-left: auto;
}

.credential-actions button,
.entry-actions button {
  min-height: 34px;
  padding: 0 12px;
  font-size: 12px;
}

.entry-qrcode-thumb {
  width: 58px;
  height: 58px;
  border: 1px solid var(--portal-hairline);
  border-radius: 12px;
  object-fit: cover;
}

.section-heading {
  min-height: 40px;
}

.section-heading strong {
  color: var(--portal-ink);
  font-size: 16px;
  font-weight: 700;
}

.resource-row {
  gap: 16px;
  padding: 16px;
}

.resource-row:not(.entry-row) > div:first-child {
  display: grid;
  gap: 6px;
  min-width: 0;
}

.resource-row strong {
  color: var(--portal-ink-soft);
}

.resource-row span,
.resource-row small {
  overflow-wrap: anywhere;
}

.resource-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.resource-actions button {
  min-height: 34px;
  padding: 0 12px;
  box-shadow: none;
}

@media (max-width: 1080px) {
  .hero-band {
    grid-template-columns: 1fr;
  }

  .hero-metrics {
    align-self: stretch;
  }

  .content-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .filter-panel {
    width: 100%;
  }

  .search-control {
    width: 100%;
    flex: 1 1 100%;
  }

  .filter-select {
    flex: 1 1 180px;
  }

  .overview-strip {
    width: 100%;
  }

  .status-summary {
    flex: 1 1 180px;
  }
}

@media (max-width: 760px) {
  .portal-shell {
    padding: 80px 12px 36px;
  }

  .hero-band {
    padding: 24px;
    border-radius: 18px;
  }

  .hero-copy h1 {
    font-size: 34px;
  }

  .hero-metrics,
  .info-list {
    grid-template-columns: 1fr;
  }

  .filter-panel,
  .overview-strip {
    width: 100%;
  }

  .filter-select,
  .primary-btn,
  .ghost-btn,
  .status-summary {
    flex: 1 1 100%;
    width: 100%;
  }

  .detail-modal {
    width: calc(100vw - 24px);
    height: calc(100vh - 44px);
    grid-template-columns: 1fr;
    border-radius: 20px;
  }

  .detail-aside {
    display: none;
  }

  .modal-close-btn {
    top: 12px;
    right: 12px;
    width: 36px;
    height: 36px;
  }

  .detail-tabs {
    padding-right: 58px;
  }

  .modal-mask {
    padding: 22px 12px;
  }

  .project-card {
    min-height: 276px;
  }

  .resource-row,
  .entry-row,
  .entry-credential-inline {
    display: grid;
    grid-template-columns: 1fr;
  }

  .entry-address-cell,
  .entry-plain-row .entry-address-cell,
  .entry-credential-inline {
    display: grid;
    grid-template-columns: 1fr;
  }

  .entry-address-cell .entry-actions,
  .credential-actions {
    grid-column: auto;
    grid-row: auto;
    justify-self: start;
  }

  .entry-plain-row .entry-address-cell > strong,
  .entry-plain-row .entry-address-cell > span,
  .entry-plain-row .entry-actions {
    flex: initial;
    margin-left: 0;
  }

  .entry-plain-row .entry-address-cell > span {
    white-space: normal;
  }

  .resource-actions,
  .entry-qrcode-inline {
    justify-content: flex-start;
  }
}
</style>
