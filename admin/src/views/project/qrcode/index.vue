<template>
  <div class="qrcode-page">
    <ElCard shadow="never" class="filter-card">
      <ElForm :model="queryParams">
        <div class="filter-form-content">
          <ElFormItem label="项目">
            <ElSelect v-model="queryParams.projectId" clearable filterable placeholder="全部项目" style="width: 260px">
              <ElOption v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
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
          新增二维码
        </ElButton>
      </div>
      <div class="table-container">
        <ElTable v-loading="loading" :data="tableData" height="100%" empty-text="暂无二维码数据">
          <ElTableColumn prop="projectName" label="项目名称" min-width="180" />
          <ElTableColumn prop="name" label="二维码名称" min-width="150" />
          <ElTableColumn label="图片" width="110" align="center">
            <template #default="{ row }">
              <ElImage class="qrcode-thumb" :src="row.image" fit="cover" />
            </template>
          </ElTableColumn>
          <ElTableColumn prop="audience" label="适用人群" min-width="130" />
          <ElTableColumn prop="description" label="说明" min-width="220" show-overflow-tooltip />
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
          <ElTableColumn prop="updateTime" label="更新时间" width="170" />
          <ElTableColumn label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <ElButton link type="primary" @click="handlePreview(row)">预览</ElButton>
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
        <ElFormItem label="二维码名称" prop="name">
          <ElInput v-model="form.name" placeholder="请输入二维码名称" />
        </ElFormItem>
        <ElFormItem label="二维码图片" prop="image">
          <div class="upload-line">
            <ElImage class="qrcode-preview" :src="form.image" fit="cover" />
            <ElUpload :auto-upload="false" :show-file-list="false" accept="image/*" @change="handleImageChange">
              <ElButton>上传/替换</ElButton>
            </ElUpload>
          </div>
        </ElFormItem>
        <ElFormItem label="适用人群">
          <ElInput v-model="form.audience" placeholder="请输入适用人群" />
        </ElFormItem>
        <ElFormItem label="说明">
          <ElInput v-model="form.description" type="textarea" :rows="3" placeholder="请输入二维码说明" />
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

    <ElDialog v-model="previewVisible" title="二维码预览" width="360px">
      <div class="preview-box">
        <ElImage class="preview-image" :src="previewRow?.image" fit="cover" />
        <div class="preview-title">{{ previewRow?.name }}</div>
        <div class="preview-desc">{{ previewRow?.description || '暂无说明' }}</div>
      </div>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, reactive, ref } from 'vue'
  import { Plus } from '@element-plus/icons-vue'
  import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
  import {
    deleteProjectQrcode,
    getProjectOptions,
    getProjectQrcodes,
    saveProjectQrcode,
    updateProjectQrcodeStatus
  } from '@/api/project'
  import type { ProjectOption, ProjectQrcode } from '@/types/project'

  defineOptions({ name: 'ProjectQrcode' })

  type QrcodeRow = ProjectQrcode & { projectName?: string }

  const loading = ref(false)
  const submitLoading = ref(false)
  const tableData = ref<QrcodeRow[]>([])
  const projectOptions = ref<ProjectOption[]>([])
  const queryParams = reactive({ projectId: '' as number | '', status: '' as number | '' })
  const dialogVisible = ref(false)
  const previewVisible = ref(false)
  const previewRow = ref<QrcodeRow>()
  const isEditing = ref(false)
  const dialogTitle = computed(() => (isEditing.value ? '编辑二维码' : '新增二维码'))
  const formRef = ref<FormInstance>()
  const form = reactive<Partial<ProjectQrcode>>({
    id: undefined,
    projectId: undefined,
    name: '',
    image: '',
    audience: '',
    description: '',
    status: 1
  })
  const rules: FormRules = {
    projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
    name: [{ required: true, message: '请输入二维码名称', trigger: 'blur' }],
    image: [{ required: true, message: '请上传二维码图片', trigger: 'change' }]
  }

  async function loadProjects() {
    const res = await getProjectOptions()
    projectOptions.value = res.data
  }

  async function loadList() {
    loading.value = true
    try {
      const res = await getProjectQrcodes(queryParams)
      tableData.value = res.data
    } catch (error: any) {
      ElMessage.error(error.message || '加载二维码失败')
    } finally {
      loading.value = false
    }
  }

  function handleReset() {
    Object.assign(queryParams, { projectId: '', status: '' })
    loadList()
  }

  function handleAdd() {
    isEditing.value = false
    dialogVisible.value = true
  }

  function handleEdit(row: QrcodeRow) {
    isEditing.value = true
    Object.assign(form, row)
    dialogVisible.value = true
  }

  function handlePreview(row: QrcodeRow) {
    previewRow.value = row
    previewVisible.value = true
  }

  async function handleSubmit() {
    try {
      await formRef.value?.validate()
      submitLoading.value = true
      await saveProjectQrcode(form)
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadList()
    } catch (error: any) {
      if (error !== false) ElMessage.error(error.message || '保存失败')
    } finally {
      submitLoading.value = false
    }
  }

  async function handleDelete(row: QrcodeRow) {
    try {
      await ElMessageBox.confirm(`确定要删除二维码"${row.name}"吗？`, '提示', { type: 'warning' })
      await deleteProjectQrcode(row.id)
      ElMessage.success('删除成功')
      loadList()
    } catch (error: any) {
      if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
    }
  }

  async function handleStatusChange(row: QrcodeRow, status: 0 | 1) {
    try {
      await updateProjectQrcodeStatus(row.id, status)
      ElMessage.success(status ? '已启用' : '已停用')
    } catch (error: any) {
      row.status = status === 1 ? 0 : 1
      ElMessage.error(error.message || '状态更新失败')
    }
  }

  function handleImageChange(file: UploadFile) {
    const raw = file.raw
    if (!raw) return
    const reader = new FileReader()
    reader.onload = () => {
      form.image = String(reader.result || '')
      formRef.value?.validateField('image')
    }
    reader.readAsDataURL(raw)
  }

  function resetForm() {
    formRef.value?.resetFields()
    Object.assign(form, {
      id: undefined,
      projectId: undefined,
      name: '',
      image: '',
      audience: '',
      description: '',
      status: 1
    })
  }

  onMounted(async () => {
    await loadProjects()
    loadList()
  })
</script>

<style scoped lang="scss">
  .qrcode-page {
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

  .qrcode-thumb,
  .qrcode-preview {
    width: 56px;
    height: 56px;
    border-radius: 8px;
    border: 1px solid #ebeef5;
  }

  .upload-line {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .preview-box {
    text-align: center;
  }

  .preview-image {
    width: 220px;
    height: 220px;
    border-radius: 12px;
    border: 1px solid #ebeef5;
  }

  .preview-title {
    margin-top: 14px;
    font-weight: 600;
  }

  .preview-desc {
    margin-top: 6px;
    color: #909399;
    font-size: 13px;
  }
</style>
