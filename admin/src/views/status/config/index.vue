<template>
  <div class="status-config-page">
    <ElCard shadow="never" class="filter-card">
      <ElForm :model="queryParams">
        <div class="filter-form-content">
          <ElFormItem label="项目">
            <ElSelect v-model="queryParams.projectId" clearable filterable placeholder="全部项目" style="width: 260px">
              <ElOption v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="当前状态">
            <ElSelect v-model="queryParams.status" clearable placeholder="请选择" style="width: 150px">
              <ElOption v-for="item in statusOptions" :key="item" :label="item" :value="item" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label=" ">
            <div class="filter-buttons">
              <ElButton type="primary" @click="loadList">搜索</ElButton>
              <ElButton @click="handleReset">重置</ElButton>
            </div>
          </ElFormItem>
        </div>
      </ElForm>
    </ElCard>

    <ElCard shadow="never" class="data-card">
      <div class="table-container">
        <ElTable v-loading="loading" :data="tableData" height="100%" empty-text="暂无检测配置">
          <ElTableColumn prop="projectName" label="项目名称" min-width="180" fixed="left" />
          <ElTableColumn label="自动检测" width="100" align="center">
            <template #default="{ row }">
              <ElTag :type="row.autoEnabled ? 'success' : 'info'">{{ row.autoEnabled ? '启用' : '停用' }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="method" label="检测方式" width="120" />
          <ElTableColumn prop="checkAddress" label="检测地址" min-width="240" show-overflow-tooltip />
          <ElTableColumn prop="frequencyMinutes" label="频率(分钟)" width="110" align="center" />
          <ElTableColumn prop="timeoutSeconds" label="超时(秒)" width="100" align="center" />
          <ElTableColumn label="当前状态" width="110" align="center">
            <template #default="{ row }">
              <ElTag :type="statusTagType(row.currentStatus)">{{ row.currentStatus }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="manualStatus" label="人工覆盖" width="120" />
          <ElTableColumn prop="lastCheckTime" label="最近检测" width="170">
            <template #default="{ row }">{{ row.lastCheckTime || '-' }}</template>
          </ElTableColumn>
          <ElTableColumn label="操作" width="220" fixed="right" align="center">
            <template #default="{ row }">
              <ElButton link type="primary" @click="handleEdit(row)">编辑</ElButton>
              <ElButton link type="success" @click="handleRun(row)">立即检测</ElButton>
              <ElButton link type="warning" @click="handleManual(row)">人工状态</ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </div>
    </ElCard>

    <ElDialog v-model="editVisible" title="编辑检测配置" width="680px" @closed="resetEditForm">
      <ElForm ref="editFormRef" :model="editForm" :rules="editRules" label-width="130px">
        <ElFormItem label="项目名称">
          <ElInput v-model="editForm.projectName" disabled />
        </ElFormItem>
        <ElFormItem label="自动检测" prop="autoEnabled">
          <ElRadioGroup v-model="editForm.autoEnabled">
            <ElRadio :value="1">启用</ElRadio>
            <ElRadio :value="0">停用</ElRadio>
          </ElRadioGroup>
        </ElFormItem>
        <ElFormItem label="检测方式" prop="method">
          <ElSelect v-model="editForm.method" style="width: 100%">
            <ElOption v-for="item in methodOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="检测地址" prop="checkAddress">
          <ElInput v-model="editForm.checkAddress" placeholder="请输入检测地址" />
        </ElFormItem>
        <ElFormItem v-if="editForm.method === 'TCP'" label="检测端口" prop="checkPort">
          <ElInputNumber v-model="editForm.checkPort" :min="1" :max="65535" />
        </ElFormItem>
        <ElFormItem label="超时时间(秒)" prop="timeoutSeconds">
          <ElInputNumber v-model="editForm.timeoutSeconds" :min="1" :max="60" />
        </ElFormItem>
        <ElFormItem label="检测频率(分钟)" prop="frequencyMinutes">
          <ElInputNumber v-model="editForm.frequencyMinutes" :min="1" :max="1440" />
        </ElFormItem>
        <ElFormItem label="期望状态码">
          <ElInput v-model="editForm.expectedStatusCodes" placeholder="如 200,302,401,403" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="editVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="submitLoading" @click="handleSubmitEdit">确定</ElButton>
      </template>
    </ElDialog>

    <ElDialog v-model="manualVisible" title="人工状态覆盖" width="560px" @closed="resetManualForm">
      <ElForm ref="manualFormRef" :model="manualForm" :rules="manualRules" label-width="120px">
        <ElFormItem label="项目名称">
          <ElInput v-model="manualForm.projectName" disabled />
        </ElFormItem>
        <ElFormItem label="覆盖状态" prop="manualStatus">
          <ElSelect v-model="manualForm.manualStatus" style="width: 100%">
            <ElOption v-for="item in manualOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="覆盖原因" prop="manualReason">
          <ElInput v-model="manualForm.manualReason" type="textarea" :rows="3" placeholder="状态为人工覆盖时必填" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="manualVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="manualLoading" @click="handleSubmitManual">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref } from 'vue'
  import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
  import { getProjectOptions, getStatusConfigList, runStatusCheck, setManualStatus, updateStatusConfig } from '@/api/project'
  import type { CheckMethod, ManualStatus, ProjectOption, ProjectStatus, StatusConfig } from '@/types/project'

  defineOptions({ name: 'StatusConfig' })

  const statusOptions: ProjectStatus[] = ['可用', '异常', '维护中', '未检测']
  const methodOptions: CheckMethod[] = ['HTTP/HTTPS', 'TCP', 'ping', '手动']
  const manualOptions: ManualStatus[] = ['无覆盖', '可用', '异常', '维护中', '未检测']
  const loading = ref(false)
  const submitLoading = ref(false)
  const manualLoading = ref(false)
  const projectOptions = ref<ProjectOption[]>([])
  const tableData = ref<StatusConfig[]>([])
  const queryParams = reactive({ projectId: '' as number | '', status: '' })

  const editVisible = ref(false)
  const manualVisible = ref(false)
  const editFormRef = ref<FormInstance>()
  const manualFormRef = ref<FormInstance>()
  const editForm = reactive<Partial<StatusConfig>>({})
  const manualForm = reactive({
    id: 0,
    projectName: '',
    manualStatus: '无覆盖' as ManualStatus,
    manualReason: ''
  })
  const editRules: FormRules = {
    method: [{ required: true, message: '请选择检测方式', trigger: 'change' }],
    checkAddress: [
      {
        validator: (_, value, callback) => {
          if (editForm.autoEnabled === 1 && !value) callback(new Error('请配置检测地址'))
          else callback()
        },
        trigger: 'blur'
      }
    ],
    timeoutSeconds: [{ required: true, message: '请输入超时时间', trigger: 'blur' }],
    frequencyMinutes: [{ required: true, message: '请输入检测频率', trigger: 'blur' }]
  }
  const manualRules: FormRules = {
    manualStatus: [{ required: true, message: '请选择覆盖状态', trigger: 'change' }],
    manualReason: [
      {
        validator: (_, value, callback) => {
          if (manualForm.manualStatus !== '无覆盖' && !value) callback(new Error('请填写覆盖原因'))
          else callback()
        },
        trigger: 'blur'
      }
    ]
  }

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
      const res = await getStatusConfigList(queryParams)
      tableData.value = res.data
    } catch (error: any) {
      ElMessage.error(error.message || '加载检测配置失败')
    } finally {
      loading.value = false
    }
  }

  function handleReset() {
    Object.assign(queryParams, { projectId: '', status: '' })
    loadList()
  }

  function handleEdit(row: StatusConfig) {
    Object.assign(editForm, row)
    editVisible.value = true
  }

  async function handleSubmitEdit() {
    if (!editForm.id) return
    try {
      await editFormRef.value?.validate()
      submitLoading.value = true
      await updateStatusConfig(editForm.id, editForm)
      ElMessage.success('检测配置已保存')
      editVisible.value = false
      loadList()
    } catch (error: any) {
      if (error !== false) ElMessage.error(error.message || '保存检测配置失败')
    } finally {
      submitLoading.value = false
    }
  }

  async function handleRun(row: StatusConfig) {
    try {
      await runStatusCheck(row.id)
      ElMessage.success('检测已完成，记录已新增')
      loadList()
    } catch (error: any) {
      ElMessage.error(error.message || '立即检测失败')
    }
  }

  function handleManual(row: StatusConfig) {
    Object.assign(manualForm, {
      id: row.id,
      projectName: row.projectName,
      manualStatus: row.manualStatus,
      manualReason: row.manualReason
    })
    manualVisible.value = true
  }

  async function handleSubmitManual() {
    try {
      await manualFormRef.value?.validate()
      manualLoading.value = true
      await setManualStatus(manualForm.id, manualForm.manualStatus, manualForm.manualReason)
      ElMessage.success('人工状态已保存')
      manualVisible.value = false
      loadList()
    } catch (error: any) {
      if (error !== false) ElMessage.error(error.message || '保存人工状态失败')
    } finally {
      manualLoading.value = false
    }
  }

  function resetEditForm() {
    editFormRef.value?.resetFields()
    Object.keys(editForm).forEach((key) => delete editForm[key as keyof StatusConfig])
  }

  function resetManualForm() {
    manualFormRef.value?.resetFields()
    Object.assign(manualForm, { id: 0, projectName: '', manualStatus: '无覆盖', manualReason: '' })
  }

  onMounted(async () => {
    await loadProjects()
    loadList()
  })
</script>

<style scoped lang="scss">
  .status-config-page {
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
  }
</style>
