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

        <div v-if="store.detailLoading || detailPreparing" class="modal-state">加载中...</div>
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
                  :class="{ 'entry-qrcode-row': entry.kind === 'qrcode' }"
                >
                  <div class="entry-info">
                    <span class="entry-type">{{ entry.typeLabel }}</span>
                    <div class="entry-info-main">
                      <strong>{{ entry.name }}</strong>
                      <span>{{ entry.description }}</span>
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
                  </div>

                  <div class="entry-credential-cell">
                    <span v-if="entry.credentials.length === 0" class="entry-empty">无账号</span>
                    <div
                      v-for="credential in entry.credentials"
                      :key="credential.id"
                      class="entry-credential-inline"
                    >
                      <strong>{{ credential.name }} · {{ credential.environment }}</strong>
                      <div>
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

                  <div v-if="entry.kind === 'qrcode'" class="entry-qrcode-cell">
                    <img :src="entry.image" :alt="entry.name" class="entry-qrcode-thumb" />
                    <span>{{ entry.audience }}</span>
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
const detailPreparing = ref(false)
const toastMessage = ref('')
let toastTimer: number | undefined

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
  detailPreparing.value = true
  detailVisible.value = true
  try {
    await store.openDetail(projectId)
    await loadVisiblePasswords()
  } finally {
    detailPreparing.value = false
  }
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

async function loadVisiblePasswords() {
  const credentials = detail.value?.credentials || []
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
  revealedPasswords.value = Object.fromEntries(passwordEntries)
}

onMounted(loadProjects)
</script>

<style scoped>
.portal-shell {
  min-height: 100vh;
  position: relative;
  padding: 88px 24px 42px;
  color: #172033;
  background:
    linear-gradient(90deg, rgba(15, 23, 42, 0.035) 1px, transparent 1px),
    linear-gradient(rgba(15, 23, 42, 0.035) 1px, transparent 1px),
    #f3f6f8;
  background-size: 28px 28px;
}

.portal-shell::before {
  content: '';
  position: fixed;
  inset: 0;
  pointer-events: none;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0.28));
}

.portal-toast {
  position: fixed;
  top: 80px;
  left: 50%;
  z-index: 90;
  transform: translateX(-50%);
  padding: 11px 16px;
  border: 1px solid rgba(12, 19, 33, 0.12);
  border-radius: 8px;
  color: #fff;
  background: rgba(12, 19, 33, 0.94);
  box-shadow: 0 18px 36px rgba(12, 19, 33, 0.18);
  font-size: 13px;
  line-height: 1;
}

.portal-main {
  position: relative;
  z-index: 1;
  width: 100%;
}

.hero-band {
  min-height: 232px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 430px;
  gap: 24px;
  align-items: center;
  padding: 30px 28px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 8px;
  color: #fff;
  background:
    linear-gradient(135deg, rgba(9, 20, 36, 0.98), rgba(18, 48, 74, 0.96)),
    #101d2d;
  box-shadow: 0 24px 54px rgba(15, 23, 42, 0.18);
  overflow: hidden;
}

.hero-copy {
  align-self: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-width: 720px;
}

.eyebrow-row {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  padding: 7px 10px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 999px;
  color: #b9d5ff;
  background: rgba(255, 255, 255, 0.08);
  font-size: 13px;
  font-weight: 600;
}

.eyebrow-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: #35d5a5;
  box-shadow: 0 0 0 4px rgba(53, 213, 165, 0.16);
}

.hero-copy h1,
.detail-aside h2,
.project-card h2,
.hero-copy p {
  margin: 0;
}

.hero-copy h1 {
  margin-top: 18px;
  color: #fff;
  font-size: 38px;
  line-height: 1.18;
  font-weight: 800;
}

.hero-copy p {
  max-width: 580px;
  margin-top: 14px;
  color: rgba(232, 240, 250, 0.78);
  font-size: 15px;
  line-height: 1.8;
}

.hero-metrics {
  align-self: center;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.metric-card {
  min-height: 132px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
  padding: 18px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.09);
}

.metric-card span {
  color: rgba(232, 240, 250, 0.76);
  font-size: 13px;
}

.metric-card small {
  color: rgba(232, 240, 250, 0.58);
  font-size: 12px;
}

.metric-card strong {
  color: #fff;
  font-size: 34px;
  line-height: 1;
}

.metric-card-warn strong {
  color: #ffd36b;
}

.filter-panel {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  width: fit-content;
  max-width: 100%;
  margin-top: -26px;
  padding: 14px;
  border: 1px solid rgba(217, 225, 235, 0.9);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.09);
  backdrop-filter: blur(16px);
}

.search-control,
.filter-select {
  height: 42px;
  border: 1px solid #dbe3ed;
  border-radius: 8px;
  background: #fff;
}

.search-control {
  width: clamp(360px, 38vw, 620px);
  flex: 0 1 620px;
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 0 13px;
  color: #5d6b82;
}

.search-control input,
.filter-select {
  width: 100%;
  border: 0;
  outline: 0;
  color: #172033;
  font-size: 14px;
}

.search-control input::placeholder {
  color: #8a96a8;
}

.filter-select {
  width: 150px;
  flex: 0 0 150px;
  padding: 0 12px;
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
  height: 42px;
  padding: 0 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    background 0.18s ease;
}

.primary-btn,
.ghost-btn {
  flex: 0 0 auto;
  min-width: 82px;
}

.primary-btn,
.detail-btn {
  color: #fff;
  background: #1e63d6;
  box-shadow: 0 12px 24px rgba(30, 99, 214, 0.22);
}

.ghost-btn,
.resource-actions button {
  color: #243149;
  background: #eef3f8;
}

.primary-btn:hover,
.detail-btn:hover,
.ghost-btn:hover,
.resource-actions button:hover {
  transform: translateY(-1px);
}

.overview-strip {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  width: fit-content;
  max-width: 100%;
  margin-top: 14px;
}

.status-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: clamp(176px, 12vw, 220px);
  height: 48px;
  padding: 0 14px;
  border: 1px solid #dde6ef;
  border-radius: 8px;
  color: #172033;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.05);
  transition:
    transform 0.18s ease,
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    background 0.18s ease;
}

.status-summary:hover,
.status-summary.active {
  transform: translateY(-1px);
  border-color: rgba(30, 99, 214, 0.35);
  background: #fff;
  box-shadow: 0 16px 34px rgba(15, 23, 42, 0.09);
}

.status-summary span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #68758a;
}

.status-summary strong {
  font-size: 20px;
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

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 14px;
}

.project-card {
  border: 1px solid #dde6ef;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 16px 34px rgba(15, 23, 42, 0.06);
}

.project-card {
  display: flex;
  min-height: 268px;
  flex-direction: column;
  padding: 14px;
  transition:
    transform 0.18s ease,
    border-color 0.18s ease,
    box-shadow 0.18s ease;
}

.project-card:hover {
  transform: translateY(-3px);
  border-color: #b9c9da;
  box-shadow: 0 22px 48px rgba(15, 23, 42, 0.11);
}

.project-card.attention {
  border-color: #f4d8a0;
  box-shadow: 0 16px 34px rgba(161, 92, 5, 0.08);
}

.project-card.attention:hover {
  border-color: #e8be65;
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
  display: grid;
  place-items: center;
  border-radius: 8px;
  background: #edf3f8;
}

.logo-wrap {
  width: 50px;
  height: 50px;
}

.project-logo,
.detail-logo-wrap img {
  width: 42px;
  height: 42px;
  border-radius: 8px;
  object-fit: cover;
}

.modal-close-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 8px;
  color: #5d6b82;
  background: #eef3f8;
}

.card-title-line {
  gap: 10px;
  margin-top: 14px;
  align-items: flex-start;
}

.card-title-line h2 {
  color: #142033;
  font-size: 16px;
  line-height: 1.35;
  font-weight: 800;
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
  color: #68758a;
  font-size: 13px;
}

.project-desc {
  min-height: 42px;
  display: -webkit-box;
  margin-top: 10px;
  overflow: hidden;
  line-height: 1.55;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  min-height: 26px;
  margin-top: 12px;
}

.category-chip,
.tag-chip,
.status-pill {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  min-height: 23px;
  border-radius: 999px;
  padding: 3px 8px;
  font-size: 12px;
  font-weight: 700;
}

.status-pill {
  gap: 6px;
}

.category-chip {
  color: #1d4d7b;
  background: #e6f0f9;
}

.tag-chip {
  color: #566176;
  background: #f0f3f7;
}

.available {
  color: #0b7a55;
}

.error {
  color: #b42318;
}

.maintenance {
  color: #a15c05;
}

.unchecked {
  color: #667085;
}

.status-pill.available {
  background: #dff8ed;
}

.status-pill.error {
  background: #ffe8e3;
}

.status-pill.maintenance {
  background: #fff3d3;
}

.status-pill.unchecked {
  background: #eceff3;
}

.detail-btn {
  width: 100%;
  height: 38px;
  margin-top: auto;
}

.state-box,
.modal-state {
  padding: 22px;
  text-align: center;
}

.state-box {
  border: 1px solid #dde6ef;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.88);
}

.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 80;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px 20px;
  background: rgba(12, 19, 33, 0.58);
  backdrop-filter: blur(10px);
}

.detail-modal {
  position: relative;
  width: min(1080px, calc(100vw - 48px));
  height: min(640px, calc(100vh - 48px));
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 28px 68px rgba(0, 0, 0, 0.24);
}

.detail-aside {
  position: relative;
  padding: 26px;
  color: #fff;
  background:
    linear-gradient(160deg, rgba(14, 31, 51, 0.98), rgba(24, 67, 86, 0.95)),
    #102033;
}

.modal-close-btn {
  position: absolute;
  top: 16px;
  right: 18px;
  z-index: 3;
  color: #526078;
  background: #eef3f8;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.1);
}

.detail-logo-wrap {
  width: 76px;
  height: 76px;
  margin-top: 22px;
  background: rgba(255, 255, 255, 0.14);
}

.detail-aside h2 {
  margin-top: 18px;
  font-size: 22px;
  line-height: 1.35;
}

.detail-aside p {
  margin-top: 10px;
  color: rgba(232, 240, 250, 0.72);
  line-height: 1.75;
}

.detail-status-card {
  display: grid;
  gap: 10px;
  margin-top: 24px;
  padding: 16px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
}

.detail-status-card strong {
  font-size: 24px;
}

.detail-status-card small {
  color: rgba(232, 240, 250, 0.66);
}

.detail-content {
  position: relative;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: #f7f9fb;
}

.detail-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 16px 76px 16px 18px;
  border-bottom: 1px solid #dfe7ef;
  background: #fff;
}

.detail-tabs button {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  height: 38px;
  padding: 0 12px;
  border-radius: 8px;
  color: #526078;
  background: #eef3f8;
}

.detail-tabs button.active {
  color: #fff;
  background: #1e63d6;
}

.detail-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 18px;
}

.detail-section {
  min-height: auto;
}

.info-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin: 0;
}

.info-list div,
.resource-row {
  border: 1px solid #dde6ef;
  border-radius: 8px;
  background: #fff;
}

.info-list div {
  min-height: 104px;
  padding: 16px;
}

.info-list dd {
  margin: 8px 0 0;
  color: #172033;
  line-height: 1.7;
}

.instruction-list div:nth-child(3) {
  grid-column: 1 / -1;
}

.entry-group,
.resource-list,
.entry-table {
  display: grid;
  gap: 12px;
}

.entry-row {
  display: grid;
  grid-template-columns: minmax(340px, 1.05fr) minmax(360px, 1fr);
  align-items: center;
  min-height: 82px;
}

.entry-qrcode-row {
  grid-template-columns: minmax(340px, 1.05fr) minmax(260px, 1fr) auto;
}

.entry-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  min-width: 0;
}

.entry-info-main {
  display: grid;
  gap: 6px;
  min-width: 0;
}

.entry-info-main .entry-actions {
  justify-content: flex-start;
  margin-top: 2px;
}

.entry-type {
  flex: 0 0 auto;
  min-width: 50px;
  padding: 4px 8px;
  border-radius: 999px;
  text-align: center;
  color: #1d4d7b;
  background: #e6f0f9;
  font-size: 12px;
  font-weight: 800;
}

.entry-credential-cell {
  min-width: 0;
}

.entry-credential-inline {
  display: grid;
  grid-template-columns: minmax(92px, 0.75fr) minmax(190px, 1.35fr) auto;
  gap: 10px;
  align-items: center;
  min-width: 0;
}

.entry-credential-inline > div:first-of-type {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 12px;
  min-width: 0;
}

.entry-empty {
  color: #8a96a8;
  font-size: 13px;
}

.entry-actions,
.credential-actions {
  flex-wrap: nowrap;
}

.credential-actions button,
.entry-actions button {
  height: 32px;
  padding: 0 9px;
  font-size: 12px;
}

.entry-qrcode-cell {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  min-width: 104px;
}

.entry-qrcode-thumb {
  width: 54px;
  height: 54px;
  border: 1px solid #dde6ef;
  border-radius: 8px;
  object-fit: cover;
}

.section-heading {
  min-height: 38px;
}

.section-heading strong {
  color: #172033;
  font-size: 16px;
}

.resource-row {
  gap: 16px;
  padding: 14px;
}

.resource-row:not(.entry-row) > div:first-child {
  display: grid;
  gap: 6px;
  min-width: 0;
}

.resource-row strong {
  color: #172033;
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
  height: 34px;
  padding: 0 10px;
  box-shadow: none;
}

@media (max-width: 1080px) {
  .hero-band {
    grid-template-columns: 1fr;
  }

  .hero-metrics {
    align-self: stretch;
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
    padding: 78px 14px 28px;
  }

  .hero-band {
    padding: 22px;
  }

  .hero-copy h1 {
    font-size: 30px;
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

  .resource-row,
  .entry-row,
  .entry-credential-inline {
    display: grid;
    grid-template-columns: 1fr;
  }

  .resource-actions,
  .entry-qrcode-cell {
    justify-content: flex-start;
  }
}
</style>
