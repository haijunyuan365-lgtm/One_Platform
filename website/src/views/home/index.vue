<template>
  <section class="portal-shell">
    <div v-if="toastMessage" class="portal-toast">{{ toastMessage }}</div>

    <div class="portal-main">
      <div class="portal-toolbar">
        <div>
          <p class="eyebrow">公司一体化平台</p>
          <h1>项目统一门户</h1>
        </div>
        <div class="toolbar-stats">
          <div>
            <strong>{{ store.projects.length }}</strong>
            <span>授权项目</span>
          </div>
          <div>
            <strong>{{ store.favoriteIds.length }}</strong>
            <span>收藏</span>
          </div>
          <div>
            <strong>{{ store.recentIds.length }}</strong>
            <span>最近访问</span>
          </div>
        </div>
      </div>

      <div class="filter-row">
        <input v-model="store.filters.keyword" class="filter-input" placeholder="搜索项目名称、简称、标签或说明" @keyup.enter="loadProjects" />
        <select v-model="store.filters.category" class="filter-select">
          <option value="">全部分类</option>
          <option v-for="item in categoryOptions" :key="item" :value="item">{{ item }}</option>
        </select>
        <select v-model="store.filters.status" class="filter-select">
          <option value="">全部状态</option>
          <option v-for="item in statusOptions" :key="item" :value="item">{{ item }}</option>
        </select>
        <label class="favorite-toggle">
          <input v-model="favoriteOnly" type="checkbox" />
          仅看收藏
        </label>
        <button class="primary-btn" @click="loadProjects">查询</button>
        <button class="ghost-btn" @click="resetFilters">重置</button>
      </div>

      <div class="content-grid">
        <main class="project-zone">
          <div v-if="store.loading" class="state-box">加载中...</div>
          <div v-else-if="visibleProjects.length === 0" class="state-box">暂无可访问项目，请联系管理员授权</div>
          <div v-else class="project-grid">
            <article v-for="project in visibleProjects" :key="project.id" class="project-card">
              <div class="card-head">
                <img :src="project.logo" :alt="project.name" class="project-logo" />
                <button class="icon-btn" :aria-label="store.isFavorite(project.id) ? '取消收藏' : '收藏项目'" @click="store.toggleFavorite(project.id)">
                  {{ store.isFavorite(project.id) ? '★' : '☆' }}
                </button>
              </div>
              <div class="card-title-line">
                <h2>{{ project.name }}</h2>
                <span :class="['status-pill', statusClass(project.status)]">{{ project.status }}</span>
              </div>
              <p class="project-desc">{{ project.description }}</p>
              <div class="tag-row">
                <span class="category-chip">{{ project.category }}</span>
                <span v-for="tag in project.tags" :key="tag" class="tag-chip">{{ tag }}</span>
              </div>
              <div class="card-meta">
                <span>维护人：{{ project.maintainer }}</span>
                <span>{{ project.responseTime ? `${project.responseTime}ms` : '未检测' }}</span>
              </div>
              <button class="detail-btn" @click="openProject(project.id)">查看详情</button>
            </article>
          </div>
        </main>

        <aside class="side-zone">
          <div class="side-panel">
            <div class="panel-title">收藏项目</div>
            <button v-for="project in store.favoriteProjects" :key="project.id" class="side-link" @click="openProject(project.id)">
              <span>{{ project.shortName }}</span>
              <em>{{ project.status }}</em>
            </button>
            <div v-if="store.favoriteProjects.length === 0" class="mini-empty">暂无收藏</div>
          </div>
          <div class="side-panel">
            <div class="panel-title">最近访问</div>
            <button v-for="project in store.recentProjects" :key="project.id" class="side-link" @click="openProject(project.id)">
              <span>{{ project.shortName }}</span>
              <em>{{ project.category }}</em>
            </button>
            <div v-if="store.recentProjects.length === 0" class="mini-empty">暂无最近访问</div>
          </div>
        </aside>
      </div>
    </div>
  </section>

  <div v-if="detailVisible" class="modal-mask" @click.self="closeDetail">
    <section class="detail-modal">
      <header class="detail-header">
        <div class="detail-title">
          <img :src="detail?.project.logo" :alt="detail?.project.name" />
          <div>
            <h2>{{ detail?.project.name }}</h2>
            <p>{{ detail?.project.description }}</p>
          </div>
        </div>
        <button class="close-btn" @click="closeDetail">×</button>
      </header>

      <nav class="detail-tabs">
        <button v-for="tab in tabs" :key="tab.key" :class="{ active: activeTab === tab.key }" @click="activeTab = tab.key">
          {{ tab.label }}
        </button>
      </nav>

      <div v-if="store.detailLoading" class="modal-state">加载中...</div>
      <div v-else-if="detail" class="detail-body">
        <div v-if="activeTab === 'basic'" class="detail-section">
          <div class="status-block">
            <span :class="['status-pill', statusClass(detail.project.status)]">{{ detail.project.status }}</span>
            <strong>{{ detail.project.responseTime ? `${detail.project.responseTime}ms` : '未检测' }}</strong>
            <span>{{ detail.project.lastCheckTime || '暂无检测时间' }}</span>
          </div>
          <dl class="info-list">
            <div><dt>项目分类</dt><dd>{{ detail.project.category }}</dd></div>
            <div><dt>项目标签</dt><dd>{{ detail.project.tags.join('、') }}</dd></div>
            <div><dt>维护人</dt><dd>{{ detail.project.maintainer }}</dd></div>
            <div><dt>异常原因</dt><dd>{{ detail.project.abnormalReason || '-' }}</dd></div>
          </dl>
        </div>

        <div v-if="activeTab === 'entry'" class="detail-section entry-layout">
          <section class="entry-group">
            <div class="entry-heading">
              <strong>访问地址</strong>
              <span>Web、后台和其他可直接打开的入口</span>
            </div>

            <div v-if="detail.addresses.length === 0" class="modal-state">暂未配置访问地址</div>
            <div v-else class="entry-addresses">
              <div v-for="address in detail.addresses" :key="address.id" class="resource-row">
                <div>
                  <strong>{{ address.name }}</strong>
                  <span>{{ address.type }} · {{ address.url }}</span>
                </div>
                <div class="resource-actions">
                  <button @click="openAddress(address.url)">打开</button>
                  <button @click="copyText(address.url, '地址已复制')">复制</button>
                </div>
              </div>
            </div>
          </section>

          <section class="entry-group">
            <div class="entry-heading">
              <strong>小程序入口</strong>
            </div>

            <div v-if="detail.qrcodes.length === 0" class="modal-state">暂未配置小程序二维码</div>
            <div v-else class="qrcode-grid entry-qrcode-grid">
              <div v-for="qrcode in detail.qrcodes" :key="qrcode.id" class="qrcode-card">
                <img :src="qrcode.image" :alt="qrcode.name" />
                <strong>{{ qrcode.name }}</strong>
                <span>{{ qrcode.audience }}</span>
                <p>{{ qrcode.description }}</p>
              </div>
            </div>
            <p v-if="detail.qrcodes.length > 0" class="entry-hint">扫码访问移动端或小程序资源</p>
          </section>
        </div>

        <div v-if="activeTab === 'credential'" class="detail-section">
          <div v-if="detail.credentials.length === 0" class="modal-state">暂无可查看账号凭据</div>
          <div v-for="credential in detail.credentials" :key="credential.id" class="resource-row">
            <div>
              <strong>{{ credential.name }} · {{ credential.environment }}</strong>
              <span>账号：{{ credential.username }}</span>
              <span>密码：{{ revealedIds.includes(credential.id) ? credential.password : '••••••••••••' }}</span>
              <small>{{ credential.description }}</small>
            </div>
            <div class="resource-actions">
              <button @click="revealPassword(credential.id)">查看</button>
              <button @click="copyText(credential.username, '账号已复制')">复制账号</button>
              <button @click="copyText(credential.password, '密码已复制')">复制密码</button>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'instruction'" class="detail-section">
          <dl class="info-list">
            <div><dt>浏览器要求</dt><dd>{{ detail.instruction.browserRequirement }}</dd></div>
            <div><dt>VPN 要求</dt><dd>{{ detail.instruction.vpnRequirement }}</dd></div>
            <div><dt>注意事项</dt><dd>{{ detail.instruction.notes }}</dd></div>
            <div><dt>维护联系人</dt><dd>{{ detail.instruction.maintainer }} · {{ detail.instruction.contactPhone }}</dd></div>
          </dl>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { usePortalStore } from '@/stores/portal'
import type { ProjectCategory, ProjectStatus } from '@/types/project'

const store = usePortalStore()
const favoriteOnly = ref(false)
const detailVisible = ref(false)
const activeTab = ref('basic')
const revealedIds = ref<number[]>([])
const toastMessage = ref('')
let toastTimer: number | undefined
const categoryOptions: ProjectCategory[] = ['内部系统', '客户项目', 'AI工具', '数据平台', '运维服务', '小程序']
const statusOptions: ProjectStatus[] = ['可用', '异常', '维护中', '未检测']
const tabs = [
  { key: 'basic', label: '基本信息' },
  { key: 'entry', label: '访问入口' },
  { key: 'credential', label: '账号凭据' },
  { key: 'instruction', label: '访问说明' },
]

const detail = computed(() => store.detail)
const visibleProjects = computed(() =>
  favoriteOnly.value ? store.projects.filter((project) => store.isFavorite(project.id)) : store.projects,
)

function statusClass(status: ProjectStatus) {
  return {
    available: status === '可用',
    error: status === '异常',
    maintenance: status === '维护中',
    unchecked: status === '未检测',
  }
}

async function loadProjects() {
  await store.loadProjects()
}

function resetFilters() {
  store.filters = { keyword: '', category: '', status: '' }
  favoriteOnly.value = false
  loadProjects()
}

async function openProject(projectId: number) {
  revealedIds.value = []
  activeTab.value = 'basic'
  detailVisible.value = true
  await store.openDetail(projectId)
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

function revealPassword(id: number) {
  if (!revealedIds.value.includes(id)) revealedIds.value.push(id)
}

onMounted(loadProjects)
</script>

<style scoped>
.portal-shell {
  min-height: 100vh;
  background: #f7f9fc;
  padding: 88px 24px 40px;
  position: relative;
}

.portal-toast {
  position: fixed;
  top: 78px;
  left: 50%;
  z-index: 60;
  transform: translateX(-50%);
  border: 1px solid rgba(17, 24, 39, 0.1);
  border-radius: 8px;
  background: rgba(17, 24, 39, 0.94);
  color: #fff;
  font-size: 13px;
  line-height: 1;
  padding: 10px 14px;
  box-shadow: 0 14px 28px rgba(17, 24, 39, 0.16);
}

.portal-main {
  max-width: 1280px;
  margin: 0 auto;
}

.portal-toolbar,
.filter-row,
.project-card,
.side-panel,
.detail-modal {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.portal-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
}

.eyebrow {
  color: #2563eb;
  font-size: 13px;
  font-weight: 600;
  margin: 0 0 8px;
}

h1,
h2,
p {
  margin: 0;
}

.portal-toolbar h1 {
  font-size: 30px;
  font-weight: 700;
  color: #111827;
}

.toolbar-stats {
  display: flex;
  gap: 16px;
}

.toolbar-stats div {
  min-width: 96px;
  padding: 12px 14px;
  border-radius: 8px;
  background: #f3f6fb;
}

.toolbar-stats strong {
  display: block;
  color: #111827;
  font-size: 24px;
}

.toolbar-stats span,
.card-meta,
.project-desc,
.side-link em,
.sub-text {
  color: #6b7280;
  font-size: 13px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  padding: 14px;
  margin-top: 16px;
}

.filter-input,
.filter-select {
  height: 40px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0 12px;
  color: #111827;
  background: #fff;
}

.filter-input {
  min-width: 300px;
  flex: 1;
}

.favorite-toggle {
  height: 40px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  color: #374151;
  font-size: 14px;
}

button {
  border: 0;
  cursor: pointer;
}

.primary-btn,
.ghost-btn,
.detail-btn {
  height: 40px;
  padding: 0 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
}

.primary-btn,
.detail-btn {
  background: #2563eb;
  color: #fff;
}

.ghost-btn {
  background: #edf2f7;
  color: #374151;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 16px;
  margin-top: 16px;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.project-card {
  padding: 18px;
}

.card-head,
.card-title-line,
.card-meta,
.resource-row,
.detail-header,
.detail-title {
  display: flex;
  align-items: center;
}

.card-head,
.card-title-line,
.card-meta,
.resource-row,
.detail-header {
  justify-content: space-between;
}

.project-logo,
.detail-title img {
  width: 52px;
  height: 52px;
  border-radius: 10px;
}

.icon-btn,
.close-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: #f3f4f6;
  color: #2563eb;
  font-size: 18px;
}

.card-title-line {
  gap: 10px;
  margin-top: 16px;
}

.card-title-line h2 {
  font-size: 17px;
  font-weight: 700;
}

.project-desc {
  line-height: 1.7;
  margin-top: 10px;
  min-height: 46px;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 14px 0;
}

.category-chip,
.tag-chip,
.status-pill {
  border-radius: 999px;
  padding: 4px 9px;
  font-size: 12px;
}

.category-chip {
  background: #eef2ff;
  color: #3730a3;
}

.tag-chip {
  background: #f3f4f6;
  color: #4b5563;
}

.status-pill.available {
  background: #dcfce7;
  color: #166534;
}

.status-pill.error {
  background: #fee2e2;
  color: #991b1b;
}

.status-pill.maintenance {
  background: #fef3c7;
  color: #92400e;
}

.status-pill.unchecked {
  background: #e5e7eb;
  color: #4b5563;
}

.detail-btn {
  width: 100%;
  margin-top: 16px;
}

.side-zone {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.side-panel {
  padding: 16px;
}

.panel-title {
  color: #111827;
  font-weight: 700;
  margin-bottom: 10px;
}

.side-link {
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  background: transparent;
  color: #111827;
  border-bottom: 1px solid #f3f4f6;
  text-align: left;
}

.mini-empty,
.state-box,
.modal-state {
  color: #6b7280;
  padding: 18px;
  text-align: center;
}

.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 80;
  background: rgba(15, 23, 42, 0.45);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 56px 20px 24px;
}

.detail-modal {
  width: min(1120px, calc(100vw - 48px));
  height: min(680px, calc(100vh - 80px));
  max-height: calc(100vh - 80px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.detail-header {
  padding: 18px 20px;
  border-bottom: 1px solid #e5e7eb;
}

.detail-title {
  gap: 14px;
}

.detail-title h2 {
  font-size: 20px;
  font-weight: 700;
}

.detail-title p {
  color: #6b7280;
  margin-top: 4px;
}

.detail-tabs {
  display: flex;
  gap: 8px;
  padding: 12px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.detail-tabs button {
  padding: 8px 12px;
  border-radius: 8px;
  color: #4b5563;
  background: #f3f4f6;
}

.detail-tabs button.active {
  background: #2563eb;
  color: #fff;
}

.detail-body {
  overflow: auto;
  padding: 22px 24px;
}

.status-block {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 18px;
}

.info-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin: 0;
}

.info-list div,
.resource-row,
.qrcode-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 14px;
}

.entry-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.25fr) minmax(300px, 0.75fr);
  gap: 18px;
  align-items: start;
}

.entry-group {
  display: grid;
  gap: 12px;
  min-width: 0;
}

.entry-heading {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  align-items: flex-end;
}

.entry-heading strong {
  color: #111827;
  font-size: 15px;
}

.entry-heading span {
  color: #6b7280;
  font-size: 13px;
  line-height: 1.4;
  text-align: right;
}

.entry-addresses {
  display: grid;
  gap: 12px;
}

.info-list dt {
  color: #6b7280;
  font-size: 13px;
}

.info-list dd {
  margin: 6px 0 0;
  color: #111827;
  line-height: 1.6;
}

.resource-row {
  gap: 16px;
  margin-bottom: 12px;
}

.resource-row div:first-child {
  display: grid;
  gap: 6px;
  min-width: 0;
}

.resource-row span,
.resource-row small {
  color: #6b7280;
  overflow-wrap: anywhere;
}

.entry-addresses .resource-row {
  margin-bottom: 0;
}

.resource-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: flex-end;
}

.resource-actions button {
  height: 34px;
  padding: 0 10px;
  border-radius: 8px;
  background: #edf2f7;
  color: #1f2937;
}

.qrcode-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 14px;
}

.qrcode-card {
  display: grid;
  gap: 8px;
}

.qrcode-card img {
  width: 120px;
  height: 120px;
  border-radius: 8px;
}

.qrcode-card p {
  margin: 0;
  color: #6b7280;
  line-height: 1.5;
}

.entry-qrcode-grid {
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
}

.entry-hint {
  width: 214px;
  min-height: 24px;
  margin: -10px 0 0;
  color: #6b7280;
  font-size: 13px;
  line-height: 1.5;
}

@media (max-width: 920px) {
  .portal-toolbar,
  .content-grid {
    display: block;
  }

  .toolbar-stats {
    margin-top: 18px;
  }

  .side-zone {
    margin-top: 16px;
  }

  .info-list {
    grid-template-columns: 1fr;
  }

  .modal-mask {
    padding: 48px 12px 18px;
  }

  .detail-modal {
    width: min(100%, calc(100vw - 24px));
    height: min(720px, calc(100vh - 66px));
    max-height: calc(100vh - 66px);
  }

  .entry-layout {
    grid-template-columns: 1fr;
  }

  .entry-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .entry-heading span {
    text-align: left;
  }
}
</style>
