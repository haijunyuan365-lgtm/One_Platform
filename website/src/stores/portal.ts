import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  copyPortalCredential,
  getPortalProjectDetail,
  getPortalProjects,
  recordPortalAction,
  revealPortalPassword,
} from '@/api/portal'
import type { PortalProjectDetail, PortalProjectQuery, ProjectCard } from '@/types/project'

export const usePortalStore = defineStore('portal', () => {
  const projects = ref<ProjectCard[]>([])
  const detail = ref<PortalProjectDetail | null>(null)
  const loading = ref(false)
  const detailLoading = ref(false)
  const filters = ref<PortalProjectQuery>({ keyword: '', category: '', status: '' })

  async function loadProjects() {
    loading.value = true
    try {
      projects.value = await getPortalProjects(filters.value)
    } finally {
      loading.value = false
    }
  }

  async function openDetail(projectId: number) {
    detailLoading.value = true
    try {
      detail.value = await getPortalProjectDetail(projectId)
      await recordPortalAction(projectId, '打开项目详情', '项目详情')
    } finally {
      detailLoading.value = false
    }
  }

  function revealPassword(credentialId: number) {
    return revealPortalPassword(credentialId)
  }

  function copyCredential(credentialId: number, field: 'username' | 'password') {
    return copyPortalCredential(credentialId, field)
  }

  return {
    projects,
    detail,
    loading,
    detailLoading,
    filters,
    loadProjects,
    openDetail,
    revealPassword,
    copyCredential,
  }
})
