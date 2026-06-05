<template>
  <div class="project-permission-page">
    <ElCard shadow="never" class="filter-card">
      <ElForm :model="queryForm">
        <div class="filter-form-content">
          <ElFormItem label="项目">
            <ElSelect v-model="queryForm.projectId" clearable filterable placeholder="全部项目" style="width: 300px" @change="loadList">
              <ElOption v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label=" ">
            <ElButton type="primary" @click="handleAdd">
              <ElIcon><Plus /></ElIcon>
              新增授权
            </ElButton>
          </ElFormItem>
        </div>
      </ElForm>
    </ElCard>

    <ElCard shadow="never" class="data-card">
      <div class="table-container">
        <ElTable v-loading="loading" :data="tableData" height="100%" empty-text="暂无授权数据">
          <ElTableColumn prop="projectName" label="项目名称" min-width="180" fixed="left" />
          <ElTableColumn prop="targetType" label="授权对象类型" width="120" />
          <ElTableColumn prop="targetName" label="授权对象" min-width="170" />
          <ElTableColumn label="项目可见" width="100" align="center">
            <template #default="{ row }"><ElTag :type="row.visible ? 'success' : 'info'">{{ row.visible ? '允许' : '关闭' }}</ElTag></template>
          </ElTableColumn>
          <ElTableColumn label="查看密码" width="100" align="center">
            <template #default="{ row }"><ElTag :type="row.passwordView ? 'warning' : 'info'">{{ row.passwordView ? '允许' : '关闭' }}</ElTag></template>
          </ElTableColumn>
          <ElTableColumn label="复制密码" width="100" align="center">
            <template #default="{ row }"><ElTag :type="row.passwordCopy ? 'danger' : 'info'">{{ row.passwordCopy ? '允许' : '关闭' }}</ElTag></template>
          </ElTableColumn>
          <ElTableColumn label="二维码查看" width="110" align="center">
            <template #default="{ row }"><ElTag :type="row.qrcodeView ? 'success' : 'info'">{{ row.qrcodeView ? '允许' : '关闭' }}</ElTag></template>
          </ElTableColumn>
          <ElTableColumn label="状态" width="90" align="center">
            <template #default="{ row }">
              <ElTag :type="row.status ? 'success' : 'info'">{{ row.status ? '启用' : '停用' }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="updateTime" label="更新时间" width="170" />
          <ElTableColumn label="操作" width="140" fixed="right" align="center">
            <template #default="{ row }">
              <ElButton link type="primary" @click="handleEdit(row)">编辑</ElButton>
              <ElButton link type="danger" @click="handleDelete(row)">删除</ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </div>
    </ElCard>

    <ElDialog v-model="dialogVisible" :title="dialogTitle" width="620px" @closed="resetForm">
      <ElForm ref="formRef" :model="form" :rules="rules" label-width="120px">
        <ElFormItem label="项目" prop="projectId">
          <ElSelect v-model="form.projectId" filterable placeholder="请选择项目" style="width: 100%">
            <ElOption v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="对象类型" prop="targetType">
          <ElRadioGroup v-model="form.targetType" @change="handleTargetTypeChange">
            <ElRadio value="部门">部门</ElRadio>
            <ElRadio value="角色">角色</ElRadio>
            <ElRadio value="用户">用户</ElRadio>
          </ElRadioGroup>
        </ElFormItem>
        <ElFormItem label="授权对象" prop="targetId">
          <ElSelect v-model="form.targetId" filterable placeholder="请选择授权对象" style="width: 100%" @change="handleTargetChange">
            <ElOption v-for="item in targetOptions" :key="item.value" :label="item.label" :value="item.value" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="权限范围">
          <ElCheckbox v-model="form.visible">项目可见</ElCheckbox>
          <ElCheckbox v-model="form.passwordView">查看密码</ElCheckbox>
          <ElCheckbox v-model="form.passwordCopy">复制密码</ElCheckbox>
          <ElCheckbox v-model="form.qrcodeView">查看二维码</ElCheckbox>
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
  import { getDepartmentList, getRoleList, getUserList } from '@/api/organization'
  import { deleteProjectPermission, getProjectOptions, getProjectPermissionList, saveProjectPermission } from '@/api/project'
  import type { AdminUser, Department, Role } from '@/types/api'
  import type { PermissionTargetType, ProjectOption, ProjectPermission, SelectOption } from '@/types/project'

  defineOptions({ name: 'ProjectPermission' })

  type PermissionRow = ProjectPermission & { projectName?: string }

  const loading = ref(false)
  const submitLoading = ref(false)
  const projectOptions = ref<ProjectOption[]>([])
  const tableData = ref<PermissionRow[]>([])
  const departments = ref<SelectOption<number>[]>([])
  const roles = ref<SelectOption<number>[]>([])
  const users = ref<SelectOption<number>[]>([])
  const queryForm = reactive({ projectId: '' as number | '' })
  const dialogVisible = ref(false)
  const isEditing = ref(false)
  const dialogTitle = computed(() => (isEditing.value ? '编辑项目授权' : '新增项目授权'))
  const targetOptions = computed(() => {
    if (form.targetType === '部门') return departments.value
    if (form.targetType === '角色') return roles.value
    return users.value
  })
  const formRef = ref<FormInstance>()
  const form = reactive<Partial<ProjectPermission>>({
    id: undefined,
    projectId: undefined,
    targetType: '部门',
    targetId: undefined,
    targetName: '',
    visible: true,
    passwordView: false,
    passwordCopy: false,
    qrcodeView: true,
    status: 1
  })
  const rules: FormRules = {
    projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
    targetType: [{ required: true, message: '请选择对象类型', trigger: 'change' }],
    targetId: [{ required: true, message: '请选择授权对象', trigger: 'change' }]
  }

  function flattenDepartments(list: Department[], prefix = '') {
    list.forEach((item) => {
      const label = prefix ? `${prefix}/${item.name}` : item.name
      departments.value.push({ label, value: item.id })
      if (item.children?.length) flattenDepartments(item.children, label)
    })
  }

  async function loadSourceOptions() {
    const [projectRes, deptRes, roleRes, userRes] = await Promise.all([
      getProjectOptions(),
      getDepartmentList(),
      getRoleList({ page: 1, pageSize: 100 }),
      getUserList({ page: 1, pageSize: 100 })
    ])
    projectOptions.value = projectRes.data
    departments.value = []
    flattenDepartments(deptRes.data)
    roles.value = (roleRes.data.list as Role[]).map((item) => ({ label: item.name, value: item.id }))
    users.value = (userRes.data.list as AdminUser[]).map((item) => ({ label: item.realName || item.username, value: item.id }))
  }

  async function loadList() {
    loading.value = true
    try {
      const res = await getProjectPermissionList(queryForm.projectId)
      tableData.value = res.data
    } catch (error: any) {
      ElMessage.error(error.message || '加载授权配置失败')
    } finally {
      loading.value = false
    }
  }

  function handleAdd() {
    isEditing.value = false
    if (queryForm.projectId) form.projectId = queryForm.projectId
    dialogVisible.value = true
  }

  function handleEdit(row: PermissionRow) {
    isEditing.value = true
    Object.assign(form, row)
    dialogVisible.value = true
  }

  function handleTargetTypeChange() {
    form.targetId = undefined
    form.targetName = ''
  }

  function handleTargetChange(value: number) {
    form.targetName = targetOptions.value.find((item) => item.value === value)?.label || ''
  }

  async function handleSubmit() {
    try {
      await formRef.value?.validate()
      submitLoading.value = true
      await saveProjectPermission(form)
      ElMessage.success('授权配置已保存')
      dialogVisible.value = false
      loadList()
    } catch (error: any) {
      if (error !== false) ElMessage.error(error.message || '保存授权失败')
    } finally {
      submitLoading.value = false
    }
  }

  async function handleDelete(row: PermissionRow) {
    try {
      await ElMessageBox.confirm(`确定要删除"${row.targetName}"的授权吗？`, '提示', { type: 'warning' })
      await deleteProjectPermission(row.id)
      ElMessage.success('删除成功')
      loadList()
    } catch (error: any) {
      if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
    }
  }

  function resetForm() {
    formRef.value?.resetFields()
    Object.assign(form, {
      id: undefined,
      projectId: undefined,
      targetType: '部门' as PermissionTargetType,
      targetId: undefined,
      targetName: '',
      visible: true,
      passwordView: false,
      passwordCopy: false,
      qrcodeView: true,
      status: 1
    })
  }

  onMounted(async () => {
    await loadSourceOptions()
    loadList()
  })
</script>

<style scoped lang="scss">
  .project-permission-page {
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
