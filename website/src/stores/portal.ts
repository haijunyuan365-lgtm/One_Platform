import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { getPortalProjectDetail, getPortalProjects, recordPortalAction } from '@/api/portal'
import type { PortalProjectDetail, PortalProjectQuery, ProjectCard } from '@/types/project'

function readNumberList(key: string) {
  try {
    const value = localStorage.getItem(key)
    return value ? (JSON.parse(value) as number[]) : []
  } catch {
    return []
  }
}

export const usePortalStore = defineStore('portal', () => {
  const projects = ref<ProjectCard[]>([])
  const detail = ref<PortalProjectDetail | null>(null)
  const loading = ref(false)
  const detailLoading = ref(false)
  const favoriteIds = ref<number[]>(readNumberList('portal.favoriteIds'))
  const recentIds = ref<number[]>(readNumberList('portal.recentIds'))
  const filters = ref<PortalProjectQuery>({ keyword: '', category: '', status: '' })

  const favoriteProjects = computed(() =>
    favoriteIds.value
      .map((id) => projects.value.find((project) => project.id === id))
      .filter(Boolean) as ProjectCard[]
  )
  const recentProjects = computed(() =>
    recentIds.value
      .map((id) => projects.value.find((project) => project.id === id))
      .filter(Boolean) as ProjectCard[]
  )

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
      addRecent(projectId)
      await recordPortalAction(projectId, '打开项目详情', '项目详情')
    } finally {
      detailLoading.value = false
    }
  }

  function toggleFavorite(projectId: number) {
    const index = favoriteIds.value.indexOf(projectId)
    if (index >= 0) favoriteIds.value.splice(index, 1)
    else favoriteIds.value.unshift(projectId)
    localStorage.setItem('portal.favoriteIds', JSON.stringify(favoriteIds.value))
  }

  function addRecent(projectId: number) {
    recentIds.value = [projectId, ...recentIds.value.filter((id) => id !== projectId)].slice(0, 6)
    localStorage.setItem('portal.recentIds', JSON.stringify(recentIds.value))
  }

  function isFavorite(projectId: number) {
    return favoriteIds.value.includes(projectId)
  }

  return {
    projects,
    detail,
    loading,
    detailLoading,
    favoriteIds,
    recentIds,
    filters,
    favoriteProjects,
    recentProjects,
    loadProjects,
    openDetail,
    toggleFavorite,
    isFavorite,
  }
})
