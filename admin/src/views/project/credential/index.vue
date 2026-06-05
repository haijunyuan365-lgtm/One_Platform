<template>
  <div class="credential-page">
    <ElCard shadow="never" class="filter-card">
      <ElForm :model="queryParams">
        <div class="filter-form-content">
          <ElFormItem label="项目">
            <ElSelect v-model="queryParams.projectId" clearable filterable placeholder="全部项目" style="width: 260px">
              <ElOption v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="适用环境">
            <ElSelect v-model="queryParams.environment" clearable placeholder="请选择" style="width: 140px">
              <ElOption v-for="item in envOptions" :key="item" :label="item" :value="item" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="启用状态">
            <ElSelect v-model="queryParams.status" clearable placeholder="请选择" style="width: 130px">
              <ElOption label="启用" :value="1" />
              <ElOption label="停用" :value="0" />
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
      <div class="table-header">
        <ElButton type="primary" @click="handleAdd">
          <ElIcon><Plus /></ElIcon>
          新增凭据
        </ElButton>
      </div>
      <div class="table-container">
        <ElTable v-loading="loading" :data="tableData" height="100%" empty-text="暂无凭据数据">
          <ElTableColumn prop="projectName" label="项目名称" min-width="180" />
          <ElTableColumn prop="name" label="凭据名称" min-width="140" />
          <ElTableColumn prop="username" label="用户名" min-width="150" />
          <ElTableColumn label="密码" min-width="180">
            <template #default="{ row }">
              <span>{{ revealedPasswords[row.id] || row.passwordMasked }}</span>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="environment" label="环境" width="100" />
          <ElTableColumn prop="expireDate" label="到期时间" width="120">
            <template #default="{ row }">{{ row.expireDate || '-' }}</template>
          </ElTableColumn>
          <ElTableColumn label="状态" width="100" align="center">
            <template #default="{ row }">
              <ElSwitch
                v-model="row.status"
                :active-value="1"
                :inactive-value="0"
                @change="(value) => handleStatusChange(row, Number(value) as 0 | 1)"
              />
            </template>
          </ElTableColumn>
          <ElTableColumn label="操作" width="260" fixed="right" align="center">
            <template #default="{ row }">
              <ElButton link type="primary" @click="handleReveal(row)">查看密码</ElButton>
              <ElButton link type="primary" @click="handleCopy(row, 'username')">复制账号</ElButton>
              <ElButton link type="primary" @click="handleCopy(row, 'password')">复制密码</ElButton>
              <ElButton link type="primary" @click="handleEdit(row)">编辑</ElButton>
              <ElButton link type="danger" @click="handleDelete(row)">删除</ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </div>
    </ElCard>

    <ElDialog v-model="dialogVisible" :title="dialogTitle" width="620px" @closed="resetForm">
      <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
        <ElFormItem label="所属项目" prop="projectId">
          <ElSelect v-model="form.projectId" filterable placeholder="请选择项目" style="width: 100%">
            <ElOption v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="凭据名称" prop="name">
          <ElInput v-model="form.name" placeholder="如管理员账号、演示账号" />
        </ElFormItem>
        <ElFormItem label="用户名" prop="username">
          <ElInput v-model="form.username" placeholder="请输入用户名" />
        </ElFormItem>
        <ElFormItem label="密码" prop="password">
          <ElInput v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </ElFormItem>
        <ElFormItem label="适用环境" prop="environment">
          <ElSelect v-model="form.environment" style="width: 100%">
            <ElOption v-for="item in envOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="到期时间">
          <ElDatePicker v-model="form.expireDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择到期时间" style="width: 100%" />
        </ElFormItem>
        <ElFormItem label="账号说明">
          <ElInput v-model="form.description" type="textarea" :rows="3" placeholder="请输入账号使用说明" />
        </ElFormItem>
        <ElFormItem label="启用状态">
          <ElSwitch v-model="form.status" :active-value="1" :inactive-value="0" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="submitLoading" @click="handleSubmit">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, reactive, ref } from 'vue'
  import { Plus } from '@element-plus/icons-vue'
  import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
  import {
    copyCredential,
    deleteProjectCredential,
    getProjectCredentials,
    getProjectOptions,
    revealProjectPassword,
    saveProjectCredential,
    updateProjectCredentialStatus
  } from '@/api/project'
  import type { CredentialEnv, ProjectCredential, ProjectOption } from '@/types/project'

  defineOptions({ name: 'ProjectCredential' })

  type CredentialRow = ProjectCredential & { projectName?: string }

  const envOptions: CredentialEnv[] = ['正式', '测试', '演示', '其他']
  const loading = ref(false)
  const submitLoading = ref(false)
  const tableData = ref<CredentialRow[]>([])
  const projectOptions = ref<ProjectOption[]>([])
  const revealedPasswords = reactive<Record<number, string>>({})
  const queryParams = reactive({ projectId: '' as number | '', environment: '', status: '' as number | '' })

  const dialogVisible = ref(false)
  const isEditing = ref(false)
  const dialogTitle = computed(() => (isEditing.value ? '编辑账号凭据' : '新增账号凭据'))
  const formRef = ref<FormInstance>()
  const form = reactive<Partial<ProjectCredential> & { password?: string }>({
    id: undefined,
    projectId: undefined,
    name: '',
    username: '',
    password: '',
    environment: '测试',
    description: '',
    expireDate: '',
    status: 1
  })
  const rules: FormRules = {
    projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
    name: [{ required: true, message: '请输入凭据名称', trigger: 'blur' }],
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [
      {
        validator: (_, value, callback) => {
          if (!isEditing.value && !value) callback(new Error('请输入密码'))
          else callback()
        },
        trigger: 'blur'
      }
    ],
    environment: [{ required: true, message: '请选择适用环境', trigger: 'change' }]
  }

  async function loadProjects() {
    const res = await getProjectOptions()
    projectOptions.value = res.data
  }

  async function loadList() {
    loading.value = true
    try {
      const res = await getProjectCredentials(queryParams)
      tableData.value = res.data
    } catch (error: any) {
      ElMessage.error(error.message || '加载凭据失败')
    } finally {
      loading.value = false
    }
  }

  function handleReset() {
    Object.assign(queryParams, { projectId: '', environment: '', status: '' })
    loadList()
  }

  function handleAdd() {
    isEditing.value = false
    dialogVisible.value = true
  }

  function handleEdit(row: CredentialRow) {
    isEditing.value = true
    Object.assign(form, { ...row, password: '' })
    dialogVisible.value = true
  }

  async function handleSubmit() {
    try {
      await formRef.value?.validate()
      submitLoading.value = true
      await saveProjectCredential(form)
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadList()
    } catch (error: any) {
      if (error !== false) ElMessage.error(error.message || '保存失败')
    } finally {
      submitLoading.value = false
    }
  }

  async function handleDelete(row: CredentialRow) {
    try {
      await ElMessageBox.confirm(`确定要删除凭据"${row.name}"吗？`, '提示', { type: 'warning' })
      await deleteProjectCredential(row.id)
      ElMessage.success('删除成功')
      loadList()
    } catch (error: any) {
      if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
    }
  }

  async function handleStatusChange(row: CredentialRow, status: 0 | 1) {
    try {
      await updateProjectCredentialStatus(row.id, status)
      ElMessage.success(status ? '已启用' : '已停用')
    } catch (error: any) {
      row.status = status === 1 ? 0 : 1
      ElMessage.error(error.message || '状态更新失败')
    }
  }

  async function handleReveal(row: CredentialRow) {
    try {
      const res = await revealProjectPassword(row.id)
      revealedPasswords[row.id] = res.data
      ElMessage.success('密码已显示')
    } catch (error: any) {
      ElMessage.error(error.message || '查看密码失败')
    }
  }

  async function handleCopy(row: CredentialRow, field: 'username' | 'password') {
    try {
      const res = await copyCredential(row.id, field)
      await navigator.clipboard?.writeText(res.data)
      ElMessage.success(field === 'password' ? '密码已复制' : '账号已复制')
    } catch (error: any) {
      ElMessage.error(error.message || '复制失败')
    }
  }

  function resetForm() {
    formRef.value?.resetFields()
    Object.assign(form, {
      id: undefined,
      projectId: undefined,
      name: '',
      username: '',
      password: '',
      environment: '测试',
      description: '',
      expireDate: '',
      status: 1
    })
  }

  onMounted(async () => {
    await loadProjects()
    loadList()
  })
</script>

<style scoped lang="scss">
  .credential-page {
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

    .table-header {
      flex-shrink: 0;
      margin-bottom: 16px;
    }

    .table-container {
      flex: 1;
      overflow: hidden;
    }
  }
</style>
