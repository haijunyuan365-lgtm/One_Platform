<template>
  <section class="portal-shell" id="overview">
    <div v-if="toastMessage" class="portal-toast">{{ toastMessage }}</div>

    <div class="portal-main">
      <section class="hero-band">
        <div class="hero-copy">
          <div class="hero-kicker">
            <span class="hero-kicker-dot"></span>
            <span>OnePlatform / visual workspace</span>
          </div>
          <h1>把项目入口、账号和状态收进一张干净的桌面</h1>
          <p>
            统一查看系统地址、账号凭据、二维码入口和维护说明，减少来回切页与重复询问。
          </p>

          <div class="hero-actions">
            <button type="button" class="primary-btn" @click="scrollToSection('projects')">
              查看项目
            </button>
            <button type="button" class="secondary-btn" @click="loadProjects">刷新数据</button>
          </div>

          <div class="hero-metrics">
            <article class="hero-metric metric-yellow">
              <span>项目总数</span>
              <strong>{{ store.projects.length }}</strong>
              <small>当前可访问入口</small>
            </article>
            <article class="hero-metric metric-teal">
              <span>可用项目</span>
              <strong>{{ statusCounts['可用'] }}</strong>
              <small>可直接进入</small>
            </article>
            <article class="hero-metric metric-rose">
              <span>需要关注</span>
              <strong>{{ attentionCount }}</strong>
              <small>异常或维护中</small>
            </article>
          </div>
        </div>

        <div class="hero-visual" aria-hidden="true">
          <div class="board-shell">
            <div class="board-head">
              <div>
                <span class="board-label">Workspace board</span>
                <strong>门户概览板</strong>
              </div>
              <span class="board-live"><i></i> Live</span>
            </div>

            <div class="board-grid">
              <article class="board-card board-card-wide">
                <span class="board-card-label">总入口</span>
                <strong>{{ store.projects.length }}</strong>
                <p>地址、账号、二维码和说明统一收纳。</p>
              </article>

              <article class="board-card board-card-yellow">
                <span class="board-card-label">可用</span>
                <strong>{{ statusCounts['可用'] }}</strong>
                <p>当前可直接访问的项目。</p>
              </article>

              <article class="board-card board-card-coral">
                <span class="board-card-label">关注</span>
                <strong>{{ attentionCount }}</strong>
                <p>异常或维护中的项目。</p>
              </article>

              <article class="board-card board-card-teal">
                <span class="board-card-label">维护中</span>
                <strong>{{ statusCounts['维护中'] }}</strong>
                <p>需要后续跟进的资源。</p>
              </article>

              <div class="board-preview">
                <img :src="heroImage" alt="" />
                <span>AI 原型视觉占位</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="filter-panel" id="filters">
        <label class="search-control">
          <ElIcon><Search /></ElIcon>
          <input
            v-model="store.filters.keyword"
            placeholder="搜索项目名、简称、标签或说明"
            @keyup.enter="loadProjects"
          />
        </label>

        <label class="filter-select">
          <span>项目分类</span>
          <select v-model="store.filters.category">
            <option value="">全部分类</option>
            <option v-for="item in categoryOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </label>

        <label class="filter-select">
          <span>运行状态</span>
          <select v-model="store.filters.status">
            <option value="">全部状态</option>
            <option v-for="item in statusOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </label>

        <button type="button" class="primary-btn" @click="loadProjects">
          <ElIcon><Search /></ElIcon>
          查询
        </button>
        <button type="button" class="ghost-btn" @click="resetFilters">
          <ElIcon><Refresh /></ElIcon>
          重置
        </button>
      </section>

      <div class="overview-strip" id="status">
        <button
          v-for="item in statusSummary"
          :key="item.status"
          class="status-summary"
          :class="[statusToneClass(item.status), { active: store.filters.status === item.status }]"
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

      <div class="content-grid" id="projects">
        <main class="project-zone">
          <div v-if="store.loading" class="state-box">正在加载项目...</div>
          <div v-else-if="store.projects.length === 0" class="state-box">
            暂无可访问项目，请联系管理员授权
          </div>
          <div v-else class="project-grid">
            <article
              v-for="(project, index) in store.projects"
              :key="project.id"
              class="project-card"
              :class="[projectToneClass(index), { attention: project.status !== '可用' }]"
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
                <span :class="['status-pill', statusToneClass(project.status)]">
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
    <section class="detail-modal" id="detail">
      <button type="button" class="modal-close-btn" aria-label="关闭详情" @click="closeDetail">
        <ElIcon><Close /></ElIcon>
      </button>

      <aside class="detail-aside">
        <div class="detail-logo-wrap">
          <img :src="detail?.project.logo" :alt="detail?.project.name" />
        </div>
        <h2>{{ detail?.project.name }}</h2>
        <p>{{ detail?.project.description }}</p>
        <div v-if="detail" class="detail-status-card">
          <span :class="['status-pill', statusToneClass(detail.project.status)]">
            {{ detail.project.status }}
          </span>
          <strong>{{ detail.project.responseTime ? `${detail.project.responseTime}ms` : '未检测' }}</strong>
          <small>{{ detail.project.lastCheckTime || '暂无巡检时间' }}</small>
        </div>
      </aside>

      <div class="detail-content">
        <nav class="detail-tabs">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            type="button"
            :class="{ active: activeTab === tab.key }"
            @click="activeTab = tab.key"
          >
            <ElIcon>
              <component :is="tab.icon" />
            </ElIcon>
            {{ tab.label }}
          </button>
        </nav>

        <div v-if="store.detailLoading" class="modal-state">正在加载...</div>
        <div v-else-if="detail" class="detail-body">
          <section v-if="activeTab === 'basic'" class="detail-section">
            <dl class="info-list">
              <div><dt>项目分类</dt><dd>{{ detail.project.category }}</dd></div>
              <div><dt>项目标签</dt><dd>{{ detail.project.tags.join('、') }}</dd></div>
              <div><dt>维护人</dt><dd>{{ detail.project.maintainer }}</dd></div>
              <div><dt>响应耗时</dt><dd>{{ detail.project.responseTime ? `${detail.project.responseTime}ms` : '未检测' }}</dd></div>
              <div><dt>最近巡检</dt><dd>{{ detail.project.lastCheckTime || '暂无巡检时间' }}</dd></div>
              <div><dt>异常原因</dt><dd>{{ detail.project.abnormalReason || '-' }}</dd></div>
            </dl>
          </section>

          <section v-if="activeTab === 'entry'" class="detail-section">
            <div class="entry-group">
              <div class="section-heading">
                <strong>访问入口</strong>
                <span>{{ projectEntries.length }} 个入口</span>
              </div>
              <div v-if="projectEntries.length === 0" class="modal-state">暂无可配置入口</div>
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
import heroImage from '@/assets/hero.png'
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
const statusOptions: ProjectStatus[] = ['可用', '异常', '维护中', '未检查']
const tabs = [
  { key: 'entry', label: '访问入口', icon: Link },
  { key: 'basic', label: '基础信息', icon: Monitor },
  { key: 'instruction', label: '使用说明', icon: Reading },
]
const projectTones = ['white', 'yellow', 'rose', 'teal', 'coral'] as const

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

function projectToneClass(index: number) {
  return `tone-${projectTones[index % projectTones.length]}`
}

function statusToneClass(status: ProjectStatus) {
  return {
    available: status === '可用',
    error: status === '异常',
    maintenance: status === '维护中',
    unchecked: status === '未检查',
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
  if (source.includes('后台') || source.includes('管理')) keywords.push('后台', '管理')
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

function scrollToSection(id: string) {
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
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
  padding: 96px 24px 42px;
  color: var(--portal-ink);
}

.portal-main {
  position: relative;
  z-index: 1;
  width: min(1280px, 100%);
  margin: 0 auto;
}

.portal-toast {
  position: fixed;
  top: 82px;
  left: 50%;
  z-index: 90;
  transform: translateX(-50%);
  padding: 11px 16px;
  border: 1px solid rgba(28, 28, 30, 0.08);
  border-radius: 999px;
  color: #fff;
  background: rgba(28, 28, 30, 0.96);
  box-shadow: rgba(5, 0, 56, 0.18) 0 18px 36px -8px;
  font-size: 13px;
  line-height: 1;
}

.hero-band {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(360px, 0.92fr);
  gap: 28px;
  align-items: center;
  padding: 0 0 28px;
}

.hero-copy {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 18px;
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  padding: 7px 12px;
  border: 1px solid rgba(224, 226, 232, 0.9);
  border-radius: 999px;
  color: var(--portal-muted);
  background: rgba(255, 255, 255, 0.84);
  font-size: 13px;
  font-weight: 700;
}

.hero-kicker-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: var(--portal-yellow);
  box-shadow: 0 0 0 4px rgba(255, 208, 47, 0.18);
}

.hero-copy h1,
.hero-copy p,
.board-head strong,
.board-card strong,
.detail-aside h2,
.project-card h2,
.hero-copy p {
  margin: 0;
}

.hero-copy h1 {
  max-width: 760px;
  color: var(--portal-ink);
  font-family: var(--portal-font-display);
  font-size: clamp(40px, 5vw, 60px);
  line-height: 1.08;
  font-weight: 700;
}

.hero-copy p {
  max-width: 640px;
  color: var(--portal-muted);
  font-size: 16px;
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.primary-btn,
.secondary-btn,
.ghost-btn,
.detail-btn,
.resource-actions button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 44px;
  padding: 0 18px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 700;
  transition:
    transform 0.18s ease,
    background 0.18s ease,
    color 0.18s ease,
    box-shadow 0.18s ease,
    border-color 0.18s ease;
}

.primary-btn {
  color: #fff;
  background: var(--portal-ink);
  box-shadow: rgba(5, 0, 56, 0.14) 0 14px 24px -10px;
}

.primary-btn:hover {
  transform: translateY(-1px);
  background: #2c2c34;
}

.secondary-btn,
.ghost-btn,
.resource-actions button {
  color: var(--portal-ink);
  background: #fff;
  border: 1px solid var(--portal-border-strong);
}

.secondary-btn:hover,
.ghost-btn:hover,
.resource-actions button:hover {
  transform: translateY(-1px);
  background: #f7f8fa;
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.hero-metric {
  min-height: 132px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
  padding: 18px;
  border: 1px solid rgba(224, 226, 232, 0.9);
  border-radius: 24px;
  box-shadow: var(--portal-shadow-soft);
}

.hero-metric span {
  color: var(--portal-muted);
  font-size: 13px;
  font-weight: 700;
}

.hero-metric strong {
  color: var(--portal-ink);
  font-family: var(--portal-font-display);
  font-size: 38px;
  line-height: 1;
  font-weight: 700;
}

.hero-metric small {
  color: var(--portal-subtle);
  font-size: 12px;
}

.metric-yellow {
  background: var(--portal-yellow-soft);
}

.metric-teal {
  background: var(--portal-teal);
}

.metric-rose {
  background: var(--portal-rose);
}

.hero-visual {
  min-width: 0;
  display: flex;
  justify-content: flex-end;
}

.board-shell {
  width: min(100%, 540px);
  padding: 18px;
  border: 1px solid rgba(224, 226, 232, 0.92);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: var(--portal-shadow-mockup);
}

.board-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.board-label {
  display: block;
  color: var(--portal-subtle);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.board-head strong {
  display: block;
  margin-top: 4px;
  color: var(--portal-ink);
  font-size: 18px;
  font-weight: 700;
}

.board-live {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 7px 12px;
  border-radius: 999px;
  color: #187574;
  background: var(--portal-teal);
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
}

.board-live i {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: #0fbcb0;
  box-shadow: 0 0 0 4px rgba(15, 188, 176, 0.18);
}

.board-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.board-card,
.board-preview {
  min-height: 132px;
  padding: 16px;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 22px;
  background: #fff;
}

.board-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.board-card-wide {
  grid-column: 1 / -1;
  min-height: 150px;
}

.board-card-label {
  color: var(--portal-muted);
  font-size: 12px;
  font-weight: 700;
}

.board-card strong {
  color: var(--portal-ink);
  font-family: var(--portal-font-display);
  font-size: 42px;
  line-height: 1;
  font-weight: 700;
}

.board-card p {
  color: var(--portal-subtle);
  font-size: 13px;
  line-height: 1.6;
}

.board-card-yellow {
  background: var(--portal-yellow-soft);
}

.board-card-coral {
  background: #ffc6c6;
}

.board-card-teal {
  background: var(--portal-teal);
}

.board-preview {
  grid-column: 1 / -1;
  display: grid;
  gap: 10px;
  align-content: start;
  min-height: 164px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.82), rgba(250, 250, 247, 0.92));
}

.board-preview img {
  width: 100%;
  height: 124px;
  object-fit: contain;
}

.board-preview span {
  color: var(--portal-subtle);
  font-size: 12px;
  font-weight: 700;
  text-align: center;
}

.filter-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) repeat(2, minmax(160px, 0.8fr)) auto auto;
  gap: 12px;
  align-items: end;
  margin-top: 28px;
  padding: 16px;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: var(--portal-shadow-card);
  backdrop-filter: blur(16px);
}

.search-control,
.filter-select {
  min-width: 0;
  display: grid;
  gap: 8px;
}

.search-control {
  height: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  border: 1px solid var(--portal-border-strong);
  border-radius: 999px;
  background: #fff;
}

.search-control input,
.filter-select select {
  width: 100%;
  min-width: 0;
  border: 0;
  outline: 0;
  color: var(--portal-ink);
  background: transparent;
  font: inherit;
}

.search-control input::placeholder {
  color: var(--portal-subtle);
}

.search-control :deep(svg) {
  color: var(--portal-muted);
}

.filter-select span {
  padding-left: 4px;
  color: var(--portal-subtle);
  font-size: 12px;
  font-weight: 700;
}

.filter-select select {
  height: 44px;
  padding: 0 14px;
  border: 1px solid var(--portal-border-strong);
  border-radius: 999px;
  background: #fff;
}

.overview-strip {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}

.status-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  width: 100%;
  min-height: 72px;
  padding: 0 16px;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 24px;
  color: var(--portal-ink);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: var(--portal-shadow-soft);
}

.status-summary span {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--portal-muted);
  font-size: 13px;
  font-weight: 700;
}

.status-summary strong {
  color: var(--portal-ink);
  font-family: var(--portal-font-display);
  font-size: 24px;
  line-height: 1;
  font-weight: 700;
}

.status-summary.available {
  background: var(--portal-teal);
}

.status-summary.error {
  background: #ffd7d2;
}

.status-summary.maintenance {
  background: var(--portal-yellow-soft);
}

.status-summary.unchecked {
  background: #f0f1f5;
}

.status-summary.active {
  border-color: var(--portal-ink);
  background: var(--portal-ink);
}

.status-summary.active span,
.status-summary.active strong {
  color: #fff;
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
  position: relative;
  display: flex;
  min-height: 276px;
  flex-direction: column;
  padding: 16px;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 24px;
  background: #fff;
  box-shadow: var(--portal-shadow-card);
  transition:
    transform 0.18s ease,
    border-color 0.18s ease,
    box-shadow 0.18s ease;
}

.project-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto;
  height: 5px;
  border-radius: 24px 24px 0 0;
  background: var(--portal-border);
}

.project-card:hover {
  transform: translateY(-3px);
  border-color: rgba(28, 28, 30, 0.18);
  box-shadow: rgba(5, 0, 56, 0.1) 0 18px 32px -12px;
}

.project-card.attention {
  border-color: rgba(255, 122, 122, 0.3);
}

.project-card.attention:hover {
  border-color: rgba(255, 122, 122, 0.48);
}

.tone-white::before {
  background: var(--portal-border);
}

.tone-yellow {
  background: linear-gradient(180deg, rgba(255, 244, 196, 0.8), rgba(255, 255, 255, 0.92));
}

.tone-yellow::before {
  background: var(--portal-yellow);
}

.tone-rose {
  background: linear-gradient(180deg, rgba(253, 224, 240, 0.82), rgba(255, 255, 255, 0.92));
}

.tone-rose::before {
  background: #f4a7d5;
}

.tone-teal {
  background: linear-gradient(180deg, rgba(195, 250, 245, 0.82), rgba(255, 255, 255, 0.92));
}

.tone-teal::before {
  background: #0fbcb0;
}

.tone-coral {
  background: linear-gradient(180deg, rgba(255, 230, 205, 0.82), rgba(255, 255, 255, 0.92));
}

.tone-coral::before {
  background: #ff9999;
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
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.84);
}

.logo-wrap {
  width: 54px;
  height: 54px;
  border: 1px solid rgba(224, 226, 232, 0.92);
}

.project-logo,
.detail-logo-wrap img {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  object-fit: cover;
}

.card-title-line {
  gap: 10px;
  margin-top: 14px;
  align-items: flex-start;
}

.card-title-line h2 {
  color: var(--portal-ink);
  font-size: 17px;
  line-height: 1.35;
  font-weight: 700;
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

.project-desc {
  min-height: 42px;
  display: -webkit-box;
  margin-top: 10px;
  overflow: hidden;
  line-height: 1.65;
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
  min-height: 24px;
  border-radius: 999px;
  padding: 3px 10px;
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
  color: #187574;
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
  margin-top: auto;
  background: var(--portal-ink);
  color: #fff;
}

.detail-btn:hover {
  background: #2c2c34;
}

.state-box,
.modal-state {
  padding: 22px;
  text-align: center;
}

.state-box {
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.84);
}

.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 80;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px 20px;
  background: rgba(12, 19, 33, 0.52);
  backdrop-filter: blur(10px);
}

.detail-modal {
  position: relative;
  width: min(1120px, calc(100vw - 48px));
  height: min(620px, calc(100vh - 48px));
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr);
  overflow: hidden;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: rgba(5, 0, 56, 0.22) 0 24px 64px -18px;
}

.detail-aside {
  position: relative;
  padding: 28px;
  color: var(--portal-ink);
  background:
    linear-gradient(180deg, rgba(255, 244, 196, 0.86), rgba(255, 255, 255, 0.96)),
    #fff;
}

.modal-close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 3;
  width: 40px;
  height: 40px;
  display: inline-grid;
  place-items: center;
  border-radius: 999px;
  color: var(--portal-muted);
  background: #fff;
  box-shadow: var(--portal-shadow-soft);
}

.detail-logo-wrap {
  width: 78px;
  height: 78px;
  margin-top: 24px;
  background: rgba(255, 255, 255, 0.88);
}

.detail-aside h2 {
  margin-top: 18px;
  font-family: var(--portal-font-display);
  font-size: 22px;
  line-height: 1.25;
  font-weight: 700;
}

.detail-aside p {
  margin-top: 10px;
  color: var(--portal-muted);
  line-height: 1.75;
}

.detail-status-card {
  display: grid;
  gap: 10px;
  margin-top: 24px;
  padding: 16px;
  border: 1px solid rgba(224, 226, 232, 0.95);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.84);
}

.detail-status-card strong {
  font-family: var(--portal-font-display);
  font-size: 26px;
  line-height: 1;
  font-weight: 700;
}

.detail-content {
  position: relative;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.detail-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 16px 76px 16px 18px;
  border-bottom: 1px solid var(--portal-border);
  background: rgba(250, 250, 247, 0.82);
}

.detail-tabs button {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  height: 38px;
  padding: 0 12px;
  border-radius: 999px;
  color: var(--portal-muted);
  background: #fff;
  border: 1px solid var(--portal-border-strong);
}

.detail-tabs button.active {
  color: #fff;
  background: var(--portal-ink);
  border-color: var(--portal-ink);
}

.detail-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
  overflow-x: hidden;
  padding: 18px;
}

.info-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin: 0;
}

.info-list div,
.resource-row {
  border: 1px solid var(--portal-border);
  border-radius: 20px;
  background: #fff;
}

.info-list div {
  min-height: 104px;
  padding: 16px;
}

.info-list dd {
  margin: 8px 0 0;
  color: var(--portal-ink);
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
  grid-template-columns: 72px minmax(0, 1.05fr) minmax(0, 0.95fr);
  justify-content: stretch;
  align-items: center;
  min-height: 76px;
}

.entry-no-credential-row {
  grid-template-columns: 72px minmax(0, 1fr);
}

.entry-qrcode-row {
  min-height: 96px;
}

.entry-plain-row {
  min-height: 58px;
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
  color: var(--portal-subtle);
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
  height: 32px;
  padding: 0 10px;
  font-size: 12px;
  box-shadow: none;
}

.entry-qrcode-thumb {
  width: 54px;
  height: 54px;
  border: 1px solid var(--portal-border);
  border-radius: 12px;
  object-fit: cover;
}

.section-heading {
  min-height: 38px;
}

.section-heading strong {
  color: var(--portal-ink);
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
  color: var(--portal-ink);
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
  background: #fff;
}

@media (max-width: 1120px) {
  .hero-band {
    grid-template-columns: 1fr;
  }

  .hero-visual {
    justify-content: flex-start;
  }

  .board-shell {
    width: 100%;
  }

  .filter-panel {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .overview-strip {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .portal-shell {
    padding: 82px 14px 28px;
  }

  .hero-band {
    gap: 22px;
    padding-bottom: 20px;
  }

  .hero-copy h1 {
    font-size: 34px;
  }

  .hero-metrics,
  .filter-panel,
  .overview-strip,
  .info-list {
    grid-template-columns: 1fr;
  }

  .filter-panel {
    padding: 14px;
  }

  .primary-btn,
  .secondary-btn,
  .ghost-btn,
  .status-summary {
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
