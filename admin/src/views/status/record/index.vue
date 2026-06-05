<template>
  <div class="status-record-page">
    <ElCard shadow="never" class="filter-card">
      <ElForm :model="queryParams">
        <div class="filter-form-content">
          <ElFormItem label="项目">
            <ElSelect v-model="queryParams.projectId" clearable filterable placeholder="全部项目" style="width: 260px">
              <ElOption v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="检测结果">
            <ElSelect v-model="queryParams.result" clearable placeholder="请选择" style="width: 150px">
              <ElOption v-for="item in statusOptions" :key="item" :label="item" :value="item" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="检测时间">
            <ElDatePicker
              v-model="queryParams.dateRange"
              type="daterange"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </ElFormItem>
          <ElFormItem label=" ">
            <div class="filter-buttons">
              <ElButton type="primary" @click="handleSearch">搜索</ElButton>
              <ElButton @click="handleReset">重置</ElButton>
            </div>
          </ElFormItem>
        </div>
      </ElForm>
    </ElCard>

    <ElCard shadow="never" class="data-card">
      <div class="table-container">
        <ElTable v-loading="loading" :data="tableData" height="100%" empty-text="暂无检测记录">
          <ElTableColumn prop="projectName" label="项目名称" min-width="180" fixed="left" />
          <ElTableColumn prop="method" label="检测方式" width="120" />
          <ElTableColumn prop="checkAddress" label="检测地址" min-width="260" show-overflow-tooltip />
          <ElTableColumn label="检测结果" width="110" align="center">
            <template #default="{ row }">
              <ElTag :type="statusTagType(row.result)">{{ row.result }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="responseTime" label="响应耗时(ms)" width="130" align="center" />
          <ElTableColumn prop="errorReason" label="错误原因" min-width="160">
            <template #default="{ row }">{{ row.errorReason || '-' }}</template>
          </ElTableColumn>
          <ElTableColumn prop="operator" label="执行人" width="110" />
          <ElTableColumn prop="checkTime" label="检测时间" width="170" />
          <ElTableColumn label="操作" width="90" fixed="right" align="center">
            <template #default="{ row }">
              <ElButton link type="primary" @click="handleDetail(row)">详情</ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </div>
      <div class="pagination-container">
        <ElPagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="loadList"
        />
      </div>
    </ElCard>

    <ElDialog v-model="detailVisible" title="检测记录详情" width="560px">
      <ElDescriptions :column="1" border>
        <ElDescriptionsItem label="项目名称">{{ detailRow?.projectName }}</ElDescriptionsItem>
        <ElDescriptionsItem label="检测方式">{{ detailRow?.method }}</ElDescriptionsItem>
        <ElDescriptionsItem label="检测地址">{{ detailRow?.checkAddress }}</ElDescriptionsItem>
        <ElDescriptionsItem label="检测结果">{{ detailRow?.result }}</ElDescriptionsItem>
        <ElDescriptionsItem label="响应耗时">{{ detailRow?.responseTime }} ms</ElDescriptionsItem>
        <ElDescriptionsItem label="错误原因">{{ detailRow?.errorReason || '-' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="检测时间">{{ detailRow?.checkTime }}</ElDescriptionsItem>
      </ElDescriptions>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref } from 'vue'
  import { ElMessage } from 'element-plus'
  import { getProjectOptions, getStatusRecordList } from '@/api/project'
  import type { ProjectOption, ProjectStatus, StatusRecord } from '@/types/project'

  defineOptions({ name: 'StatusRecord' })

  const statusOptions: ProjectStatus[] = ['可用', '异常', '维护中', '未检测']
  const loading = ref(false)
  const tableData = ref<StatusRecord[]>([])
  const total = ref(0)
  const projectOptions = ref<ProjectOption[]>([])
  const detailVisible = ref(false)
  const detailRow = ref<StatusRecord>()
  const queryParams = reactive({
    projectId: '' as number | '',
    result: '' as ProjectStatus | '',
    dateRange: [] as [string, string] | [],
    page: 1,
    pageSize: 10
  })

  function statusTagType(status: ProjectStatus) {
    const map = { 可用: 'success', 异常: 'danger', 维护中: 'warning', 未检测: 'info' } as const
    return map[status]
  }

  async function loadProjects() {
    const res = await getProjectOptions()
    projectOptions.value = res.data
  }

  async function loadList() {
    loading.value = true
    try {
      const res = await getStatusRecordList(queryParams)
      tableData.value = res.data.list
      total.value = res.data.total
    } catch (error: any) {
      ElMessage.error(error.message || '加载检测记录失败')
    } finally {
      loading.value = false
    }
  }

  function handleSearch() {
    queryParams.page = 1
    loadList()
  }

  function handleReset() {
    Object.assign(queryParams, { projectId: '', result: '', dateRange: [], page: 1 })
    loadList()
  }

  function handleDetail(row: StatusRecord) {
    detailRow.value = row
    detailVisible.value = true
  }

  onMounted(async () => {
    await loadProjects()
    loadList()
  })
</script>

<style scoped lang="scss">
  .status-record-page {
    height: 100%;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .filter-card,
  .data-card {
    border: none !important;
    border-radius: 12px;
    box-shadow: none !important;
  }

  .filter-card {
    flex-shrink: 0;

    :deep(.el-card__body) {
      padding: 12px 20px;
    }

    .filter-form-content {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      align-items: center;

      :deep(.el-form-item) {
        margin-bottom: 0;
      }
    }

    .filter-buttons {
      display: flex;

      .el-button:not(:first-child) {
        margin-left: 12px;
      }
    }
  }

  .data-card {
    flex: 1;
    overflow: hidden;

    :deep(.el-card__body) {
      height: 100%;
      padding: 20px;
      display: flex;
      flex-direction: column;
    }

    .table-container {
      flex: 1;
      overflow: hidden;
    }

    .pagination-container {
      flex-shrink: 0;
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }
</style>
