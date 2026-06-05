<template>
  <div class="project-card-page">
    <ElCard shadow="never" class="filter-card">
      <ElForm :model="queryParams">
        <div class="filter-form-content">
          <ElFormItem label="项目查询">
            <ElInput v-model="queryParams.keyword" clearable placeholder="名称/简称/标签/简介" style="width: 220px" />
          </ElFormItem>
          <ElFormItem label="项目分类">
            <ElSelect v-model="queryParams.category" clearable placeholder="请选择" style="width: 150px">
              <ElOption v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="项目状态">
            <ElSelect v-model="queryParams.status" clearable placeholder="请选择" style="width: 150px">
              <ElOption v-for="item in statusOptions" :key="item" :label="item" :value="item" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="维护人">
            <ElInput v-model="queryParams.maintainer" clearable placeholder="请输入维护人" style="width: 150px" />
          </ElFormItem>
          <ElFormItem label="启用状态">
            <ElSelect v-model="queryParams.enabled" clearable placeholder="请选择" style="width: 130px">
              <ElOption label="启用" :value="1" />
              <ElOption label="停用" :value="0" />
            </ElSelect>
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
      <div class="table-header">
        <ElButton type="primary" @click="handleAdd">
          <ElIcon><Plus /></ElIcon>
          新增项目
        </ElButton>
      </div>
      <div class="table-container">
        <ElTable v-loading="loading" :data="tableData" height="100%" empty-text="暂无项目数据">
          <ElTableColumn label="项目名称" min-width="230" fixed="left">
            <template #default="{ row }">
              <div class="project-name-cell">
                <ElImage class="project-logo" :src="row.logo" fit="cover">
                  <template #error>
                    <div class="logo-fallback">{{ row.shortName?.slice(0, 2) || '项' }}</div>
                  </template>
                </ElImage>
                <div>
                  <div class="project-title">{{ row.name }}</div>
                  <div class="project-subtitle">{{ row.shortName || '-' }}</div>
                </div>
              </div>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="category" label="分类" width="120" />
          <ElTableColumn label="标签" min-width="180">
            <template #default="{ row }">
              <ElTag v-for="tag in row.tags" :key="tag" class="tag-item" type="info">{{ tag }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="maintainer" label="维护人" width="110" />
          <ElTableColumn label="项目状态" width="110" align="center">
            <template #default="{ row }">
              <ElTag :type="statusTagType(row.status)">{{ row.status }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn label="启用状态" width="110" align="center">
            <template #default="{ row }">
              <ElSwitch
                v-model="row.enabled"
                :active-value="1"
                :inactive-value="0"
                @change="(value) => handleEnabledChange(row, Number(value) as 0 | 1)"
              />
            </template>
          </ElTableColumn>
          <ElTableColumn prop="sort" label="排序" width="90" align="center" />
          <ElTableColumn prop="updateTime" label="更新时间" width="170" />
          <ElTableColumn label="操作" width="150" align="center" fixed="right">
            <template #default="{ row }">
              <ElButton link type="primary" @click="handleEdit(row)">编辑</ElButton>
              <ElButton link type="danger" @click="handleDelete(row)">删除</ElButton>
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

    <ElDialog v-model="dialogVisible" :title="dialogTitle" width="720px" @closed="resetForm">
      <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
        <ElFormItem label="项目Logo">
          <div class="logo-upload">
            <ElImage class="logo-preview" :src="form.logo" fit="cover">
              <template #error>
                <div class="logo-fallback">Logo</div>
              </template>
            </ElImage>
            <ElUpload :auto-upload="false" :show-file-list="false" accept="image/*" @change="handleLogoChange">
              <ElButton>上传/替换</ElButton>
            </ElUpload>
          </div>
        </ElFormItem>
        <ElFormItem label="项目名称" prop="name">
          <ElInput v-model="form.name" maxlength="100" show-word-limit placeholder="请输入项目名称" />
        </ElFormItem>
        <ElFormItem label="项目简称">
          <ElInput v-model="form.shortName" maxlength="30" show-word-limit placeholder="请输入项目简称" />
        </ElFormItem>
        <ElFormItem label="项目分类" prop="category">
          <ElSelect v-model="form.category" placeholder="请选择项目分类" style="width: 100%">
            <ElOption v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="项目标签">
          <ElSelect v-model="form.tags" multiple filterable allow-create default-first-option placeholder="请选择或输入标签" style="width: 100%">
            <ElOption v-for="item in tagOptions" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="项目简介">
          <ElInput v-model="form.description" type="textarea" maxlength="300" show-word-limit :rows="3" placeholder="请输入项目简介" />
        </ElFormItem>
        <ElFormItem label="维护人">
          <ElInput v-model="form.maintainer" placeholder="请输入维护人" />
        </ElFormItem>
        <ElFormItem label="排序" prop="sort">
          <ElInputNumber v-model="form.sort" :min="1" :max="9999" style="width: 180px" />
        </ElFormItem>
        <ElFormItem label="启用状态" prop="enabled">
          <ElRadioGroup v-model="form.enabled">
            <ElRadio :value="1">启用</ElRadio>
            <ElRadio :value="0">停用</ElRadio>
          </ElRadioGroup>
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
  import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
  import { addProject, deleteProject, getProjectList, updateProject, updateProjectEnabled } from '@/api/project'
  import type { ProjectCard, ProjectCategory, ProjectStatus } from '@/types/project'

  defineOptions({ name: 'ProjectCard' })

  const categoryOptions: ProjectCategory[] = ['内部系统', '客户项目', 'AI工具', '数据平台', '运维服务', '小程序']
  const statusOptions: ProjectStatus[] = ['可用', '异常', '维护中', '未检测']
  const tagOptions = ['正式', '测试', '演示', '内网', '外网', '小程序', 'AI', '报表', '监控', 'SSO']

  const loading = ref(false)
  const submitLoading = ref(false)
  const tableData = ref<ProjectCard[]>([])
  const total = ref(0)
  const queryParams = reactive({
    keyword: '',
    category: '' as ProjectCategory | '',
    maintainer: '',
    enabled: '' as 0 | 1 | '',
    status: '' as ProjectStatus | '',
    page: 1,
    pageSize: 10
  })

  const dialogVisible = ref(false)
  const isEditing = ref(false)
  const dialogTitle = computed(() => (isEditing.value ? '编辑项目卡片' : '新增项目卡片'))
  const formRef = ref<FormInstance>()
  const form = reactive<Partial<ProjectCard>>({
    id: undefined,
    name: '',
    shortName: '',
    logo: '',
    category: '内部系统',
    tags: [],
    description: '',
    maintainer: '',
    sort: 100,
    enabled: 1
  })
  const rules: FormRules = {
    name: [
      { required: true, message: '请输入项目名称', trigger: 'blur' },
      { min: 2, max: 100, message: '项目名称长度为2-100字', trigger: 'blur' }
    ],
    category: [{ required: true, message: '请选择项目分类', trigger: 'change' }],
    sort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
    enabled: [{ required: true, message: '请选择启用状态', trigger: 'change' }]
  }

  function statusTagType(status: ProjectStatus) {
    const map = { 可用: 'success', 异常: 'danger', 维护中: 'warning', 未检测: 'info' } as const
    return map[status]
  }

  async function loadList() {
    loading.value = true
    try {
      const res = await getProjectList(queryParams)
      tableData.value = res.data.list
      total.value = res.data.total
    } catch (error: any) {
      ElMessage.error(error.message || '加载项目列表失败')
    } finally {
      loading.value = false
    }
  }

  function handleSearch() {
    queryParams.page = 1
    loadList()
  }

  function handleReset() {
    Object.assign(queryParams, { keyword: '', category: '', maintainer: '', enabled: '', status: '', page: 1 })
    loadList()
  }

  function handleAdd() {
    isEditing.value = false
    dialogVisible.value = true
  }

  function handleEdit(row: ProjectCard) {
    isEditing.value = true
    Object.assign(form, { ...row, tags: [...row.tags] })
    dialogVisible.value = true
  }

  async function handleDelete(row: ProjectCard) {
    try {
      await ElMessageBox.confirm(`确定要删除项目"${row.name}"吗？`, '提示', { type: 'warning' })
      await deleteProject(row.id)
      ElMessage.success('删除成功')
      loadList()
    } catch (error: any) {
      if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
    }
  }

  async function handleEnabledChange(row: ProjectCard, enabled: 0 | 1) {
    try {
      await updateProjectEnabled(row.id, enabled)
      ElMessage.success(enabled ? '已启用' : '已停用')
    } catch (error: any) {
      row.enabled = enabled === 1 ? 0 : 1
      ElMessage.error(error.message || '状态更新失败')
    }
  }

  async function handleLogoChange(file: UploadFile) {
    const raw = file.raw
    if (!raw) return
    const reader = new FileReader()
    reader.onload = () => {
      form.logo = String(reader.result || '')
    }
    reader.readAsDataURL(raw)
  }

  async function handleSubmit() {
    try {
      await formRef.value?.validate()
      submitLoading.value = true
      if (isEditing.value && form.id) {
        await updateProject(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await addProject(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadList()
    } catch (error: any) {
      if (error !== false) ElMessage.error(error.message || '保存失败')
    } finally {
      submitLoading.value = false
    }
  }

  function resetForm() {
    formRef.value?.resetFields()
    Object.assign(form, {
      id: undefined,
      name: '',
      shortName: '',
      logo: '',
      category: '内部系统',
      tags: [],
      description: '',
      maintainer: '',
      sort: 100,
      enabled: 1
    })
  }

  onMounted(loadList)
</script>

<style scoped lang="scss">
  .project-card-page {
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

    .pagination-container {
      flex-shrink: 0;
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }

  .project-name-cell,
  .logo-upload {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .project-logo,
  .logo-preview,
  .logo-fallback {
    width: 44px;
    height: 44px;
    border-radius: 10px;
  }

  .logo-preview,
  .logo-fallback {
    width: 64px;
    height: 64px;
  }

  .logo-fallback {
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    background: #94a3b8;
    font-size: 12px;
  }

  .project-title {
    font-weight: 500;
    color: #303133;
  }

  .project-subtitle {
    margin-top: 4px;
    font-size: 12px;
    color: #909399;
  }

  .tag-item {
    margin: 2px 4px 2px 0;
  }
</style>
