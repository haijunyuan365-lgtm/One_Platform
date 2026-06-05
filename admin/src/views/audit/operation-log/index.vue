<template>
  <div class="operation-log-page">
    <ElCard shadow="never" class="filter-card">
      <ElForm :model="queryParams">
        <div class="filter-form-content">
          <ElFormItem label="操作用户">
            <ElInput v-model="queryParams.operator" clearable placeholder="姓名/账号" style="width: 160px" />
          </ElFormItem>
          <ElFormItem label="项目名称">
            <ElInput v-model="queryParams.projectName" clearable placeholder="请输入项目名称" style="width: 180px" />
          </ElFormItem>
          <ElFormItem label="操作类型">
            <ElSelect v-model="queryParams.operationType" clearable placeholder="请选择" style="width: 160px">
              <ElOption v-for="item in operationTypes" :key="item" :label="item" :value="item" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="操作结果">
            <ElSelect v-model="queryParams.result" clearable placeholder="请选择" style="width: 130px">
              <ElOption label="成功" value="成功" />
              <ElOption label="失败" value="失败" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="时间范围">
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
              <ElButton :loading="exportLoading" @click="handleExport">导出</ElButton>
            </div>
          </ElFormItem>
        </div>
      </ElForm>
    </ElCard>

    <ElCard shadow="never" class="data-card">
      <div class="table-container">
        <ElTable v-loading="loading" :data="tableData" height="100%" empty-text="暂无日志数据">
          <ElTableColumn prop="operateTime" label="操作时间" width="170" fixed="left" />
          <ElTableColumn label="操作用户" min-width="150">
            <template #default="{ row }">
              <div>{{ row.operator }}</div>
              <div class="sub-text">{{ row.account }}</div>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="department" label="所属部门" min-width="170" />
          <ElTableColumn prop="projectName" label="项目名称" min-width="180" />
          <ElTableColumn prop="operationType" label="操作类型" width="130" />
          <ElTableColumn prop="target" label="操作对象" min-width="140" />
          <ElTableColumn label="操作结果" width="100" align="center">
            <template #default="{ row }">
              <ElTag :type="row.result === '成功' ? 'success' : 'danger'">{{ row.result }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="ip" label="IP地址" width="130" />
          <ElTableColumn prop="device" label="设备信息" min-width="160" />
          <ElTableColumn prop="remark" label="备注" min-width="180" show-overflow-tooltip />
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
  </div>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref } from 'vue'
  import { ElMessage } from 'element-plus'
  import { exportOperationLog, getOperationLogList } from '@/api/project'
  import type { OperationLog, OperationLogQueryParams } from '@/types/project'

  defineOptions({ name: 'OperationLog' })

  const operationTypes = ['登录成功', '登录失败', '打开项目详情', '查看密码', '复制密码', '复制账号', '后台修改', '修改权限', '修改检测状态', '立即检测']
  const loading = ref(false)
  const exportLoading = ref(false)
  const tableData = ref<OperationLog[]>([])
  const total = ref(0)
  const queryParams = reactive<OperationLogQueryParams>({
    operator: '',
    projectName: '',
    operationType: '',
    result: '',
    dateRange: [],
    page: 1,
    pageSize: 10
  })

  async function loadList() {
    loading.value = true
    try {
      const res = await getOperationLogList(queryParams)
      tableData.value = res.data.list
      total.value = res.data.total
    } catch (error: any) {
      ElMessage.error(error.message || '加载操作日志失败')
    } finally {
      loading.value = false
    }
  }

  function handleSearch() {
    queryParams.page = 1
    loadList()
  }

  function handleReset() {
    Object.assign(queryParams, {
      operator: '',
      projectName: '',
      operationType: '',
      result: '',
      dateRange: [],
      page: 1
    })
    loadList()
  }

  async function handleExport() {
    exportLoading.value = true
    try {
      const res = await exportOperationLog(queryParams)
      downloadCsv(res.data)
      ElMessage.success('日志已导出')
    } catch (error: any) {
      ElMessage.error(error.message || '导出失败')
    } finally {
      exportLoading.value = false
    }
  }

  function downloadCsv(rows: OperationLog[]) {
    const headers = ['操作时间', '操作用户', '账号', '所属部门', '项目名称', '操作类型', '操作对象', '操作结果', 'IP地址', '设备信息', '备注']
    const lines = rows.map((row) =>
      [
        row.operateTime,
        row.operator,
        row.account,
        row.department,
        row.projectName,
        row.operationType,
        row.target,
        row.result,
        row.ip,
        row.device,
        row.remark
      ]
        .map((value) => `"${String(value || '').replace(/"/g, '""')}"`)
        .join(',')
    )
    const blob = new Blob([[headers.join(','), ...lines].join('\n')], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `操作日志-${Date.now()}.csv`
    link.click()
    URL.revokeObjectURL(url)
  }

  onMounted(loadList)
</script>

<style scoped lang="scss">
  .operation-log-page {
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

  .sub-text {
    margin-top: 4px;
    color: #909399;
    font-size: 12px;
  }
</style>
