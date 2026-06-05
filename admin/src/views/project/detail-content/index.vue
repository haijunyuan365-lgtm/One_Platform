<template>
  <div class="detail-content-page">
    <ElCard shadow="never" class="filter-card">
      <ElForm :model="queryForm">
        <div class="filter-form-content">
          <ElFormItem label="选择项目">
            <ElSelect v-model="queryForm.projectId" filterable placeholder="请选择项目" style="width: 320px" @change="loadDetail">
              <ElOption v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
            </ElSelect>
          </ElFormItem>
        </div>
      </ElForm>
    </ElCard>

    <ElCard shadow="never" class="data-card">
      <ElTabs v-model="activeTab" class="content-tabs">
        <ElTabPane label="访问地址维护" name="address">
          <div class="table-header">
            <ElButton type="primary" @click="handleAddAddress">
              <ElIcon><Plus /></ElIcon>
              新增地址
            </ElButton>
            <ElButton :loading="saveAddressLoading" @click="handleSaveAddresses">保存地址</ElButton>
          </div>
          <div class="table-container">
            <ElTable :data="addresses" height="100%" empty-text="暂未配置访问地址">
              <ElTableColumn prop="name" label="地址名称" min-width="130" />
              <ElTableColumn prop="type" label="地址类型" width="100" />
              <ElTableColumn prop="url" label="访问地址" min-width="260" show-overflow-tooltip />
              <ElTableColumn label="默认打开" width="100" align="center">
                <template #default="{ row }">
                  <ElTag v-if="row.isDefault" type="success">是</ElTag>
                  <span v-else>否</span>
                </template>
              </ElTableColumn>
              <ElTableColumn label="用于检测" width="100" align="center">
                <template #default="{ row }">
                  <ElTag v-if="row.isDetection" type="warning">是</ElTag>
                  <span v-else>否</span>
                </template>
              </ElTableColumn>
              <ElTableColumn prop="sort" label="排序" width="90" align="center" />
              <ElTableColumn label="状态" width="90" align="center">
                <template #default="{ row }">
                  <ElTag :type="row.status ? 'success' : 'info'">{{ row.status ? '启用' : '停用' }}</ElTag>
                </template>
              </ElTableColumn>
              <ElTableColumn label="操作" width="140" fixed="right" align="center">
                <template #default="{ row, $index }">
                  <ElButton link type="primary" @click="handleEditAddress(row, $index)">编辑</ElButton>
                  <ElButton link type="danger" @click="handleDeleteAddress($index)">删除</ElButton>
                </template>
              </ElTableColumn>
            </ElTable>
          </div>
        </ElTabPane>

        <ElTabPane label="说明维护" name="instruction">
          <ElScrollbar class="instruction-scrollbar">
            <ElForm ref="instructionFormRef" :model="instructionForm" :rules="instructionRules" label-width="120px" class="instruction-form">
              <ElFormItem label="浏览器要求">
                <ElInput v-model="instructionForm.browserRequirement" placeholder="如 Chrome 120+ 或 Edge 120+" />
              </ElFormItem>
              <ElFormItem label="VPN 要求">
                <ElInput v-model="instructionForm.vpnRequirement" placeholder="请输入 VPN 或网络访问要求" />
              </ElFormItem>
              <ElFormItem label="注意事项">
                <ElInput v-model="instructionForm.notes" type="textarea" :rows="5" maxlength="500" show-word-limit placeholder="请输入访问注意事项" />
              </ElFormItem>
              <ElFormItem label="维护联系人" prop="maintainer">
                <ElInput v-model="instructionForm.maintainer" placeholder="请输入维护联系人" />
              </ElFormItem>
              <ElFormItem label="联系电话">
                <ElInput v-model="instructionForm.contactPhone" placeholder="请输入联系电话" />
              </ElFormItem>
              <ElFormItem>
                <ElButton type="primary" :loading="saveInstructionLoading" @click="handleSaveInstruction">保存说明</ElButton>
              </ElFormItem>
            </ElForm>
          </ElScrollbar>
        </ElTabPane>
      </ElTabs>
    </ElCard>

    <ElDialog v-model="addressDialogVisible" :title="addressDialogTitle" width="620px" @closed="resetAddressForm">
      <ElForm ref="addressFormRef" :model="addressForm" :rules="addressRules" label-width="100px">
        <ElFormItem label="地址名称" prop="name">
          <ElInput v-model="addressForm.name" placeholder="如正式地址、测试地址、后台地址" />
        </ElFormItem>
        <ElFormItem label="地址类型" prop="type">
          <ElSelect v-model="addressForm.type" style="width: 100%">
            <ElOption v-for="item in addressTypes" :key="item" :label="item" :value="item" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="访问地址" prop="url">
          <ElInput v-model="addressForm.url" placeholder="请输入完整 URL 或访问说明" />
        </ElFormItem>
        <ElFormItem label="默认打开">
          <ElSwitch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
        </ElFormItem>
        <ElFormItem label="用于检测">
          <ElSwitch v-model="addressForm.isDetection" :active-value="1" :inactive-value="0" />
        </ElFormItem>
        <ElFormItem label="排序" prop="sort">
          <ElInputNumber v-model="addressForm.sort" :min="1" :max="9999" />
        </ElFormItem>
        <ElFormItem label="启用状态">
          <ElSwitch v-model="addressForm.status" :active-value="1" :inactive-value="0" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="addressDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleConfirmAddress">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, reactive, ref } from 'vue'
  import { Plus } from '@element-plus/icons-vue'
  import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
  import { getProjectDetailContent, getProjectOptions, saveProjectAddresses, saveProjectInstruction } from '@/api/project'
  import type { AddressType, ProjectAddress, ProjectInstruction, ProjectOption } from '@/types/project'

  defineOptions({ name: 'ProjectDetailContent' })

  const addressTypes: AddressType[] = ['Web', '文档', '后台', '其他']
  const projectOptions = ref<ProjectOption[]>([])
  const queryForm = reactive({ projectId: undefined as number | undefined })
  const activeTab = ref('address')
  const addresses = ref<ProjectAddress[]>([])
  const editingAddressIndex = ref(-1)
  const addressDialogVisible = ref(false)
  const saveAddressLoading = ref(false)
  const saveInstructionLoading = ref(false)
  const addressDialogTitle = computed(() => (editingAddressIndex.value >= 0 ? '编辑访问地址' : '新增访问地址'))

  const addressFormRef = ref<FormInstance>()
  const addressForm = reactive<ProjectAddress>({
    id: 0,
    projectId: 0,
    name: '',
    type: 'Web',
    url: '',
    isDefault: 0,
    isDetection: 0,
    sort: 1,
    status: 1
  })
  const addressRules: FormRules = {
    name: [{ required: true, message: '请输入地址名称', trigger: 'blur' }],
    type: [{ required: true, message: '请选择地址类型', trigger: 'change' }],
    url: [{ required: true, message: '请输入访问地址', trigger: 'blur' }],
    sort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
  }

  const instructionFormRef = ref<FormInstance>()
  const instructionForm = reactive<ProjectInstruction>({
    projectId: 0,
    browserRequirement: '',
    vpnRequirement: '',
    notes: '',
    maintainer: '',
    contactPhone: '',
    updateTime: ''
  })
  const instructionRules: FormRules = {
    maintainer: [{ required: true, message: '请输入维护联系人', trigger: 'blur' }]
  }

  async function loadProjects() {
    const res = await getProjectOptions()
    projectOptions.value = res.data
    if (!queryForm.projectId && projectOptions.value.length > 0) {
      queryForm.projectId = projectOptions.value[0].id
      await loadDetail()
    }
  }

  async function loadDetail() {
    if (!queryForm.projectId) return
    try {
      const res = await getProjectDetailContent(queryForm.projectId)
      addresses.value = res.data.addresses || []
      Object.assign(instructionForm, res.data.instruction || { projectId: queryForm.projectId })
    } catch (error: any) {
      ElMessage.error(error.message || '加载详情内容失败')
    }
  }

  function handleAddAddress() {
    editingAddressIndex.value = -1
    addressDialogVisible.value = true
  }

  function handleEditAddress(row: ProjectAddress, index: number) {
    editingAddressIndex.value = index
    Object.assign(addressForm, row)
    addressDialogVisible.value = true
  }

  async function handleDeleteAddress(index: number) {
    try {
      await ElMessageBox.confirm('确定要删除该访问地址吗？', '提示', { type: 'warning' })
      addresses.value.splice(index, 1)
      await handleSaveAddresses()
    } catch (error: any) {
      if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
    }
  }

  async function handleConfirmAddress() {
    try {
      await addressFormRef.value?.validate()
      const next = { ...addressForm, projectId: queryForm.projectId || 0 }
      if (editingAddressIndex.value >= 0) addresses.value.splice(editingAddressIndex.value, 1, next)
      else addresses.value.push(next)
      addressDialogVisible.value = false
    } catch (error: any) {
      if (error !== false) ElMessage.error(error.message || '请检查地址信息')
    }
  }

  async function handleSaveAddresses() {
    if (!queryForm.projectId) return
    saveAddressLoading.value = true
    try {
      const res = await saveProjectAddresses(queryForm.projectId, addresses.value)
      addresses.value = res.data
      ElMessage.success('访问地址已保存')
    } catch (error: any) {
      ElMessage.error(error.message || '保存访问地址失败')
    } finally {
      saveAddressLoading.value = false
    }
  }

  async function handleSaveInstruction() {
    if (!queryForm.projectId) return
    try {
      await instructionFormRef.value?.validate()
      saveInstructionLoading.value = true
      const res = await saveProjectInstruction(queryForm.projectId, instructionForm)
      Object.assign(instructionForm, res.data)
      ElMessage.success('访问说明已保存')
    } catch (error: any) {
      if (error !== false) ElMessage.error(error.message || '保存访问说明失败')
    } finally {
      saveInstructionLoading.value = false
    }
  }

  function resetAddressForm() {
    addressFormRef.value?.resetFields()
    Object.assign(addressForm, {
      id: 0,
      projectId: queryForm.projectId || 0,
      name: '',
      type: 'Web',
      url: '',
      isDefault: 0,
      isDetection: 0,
      sort: addresses.value.length + 1,
      status: 1
    })
  }

  onMounted(loadProjects)
</script>

<style scoped lang="scss">
  .detail-content-page {
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

    :deep(.el-card__body),
    .content-tabs,
    :deep(.el-tabs__content),
    :deep(.el-tab-pane) {
      height: 100%;
    }

    :deep(.el-card__body) {
      padding: 20px;
    }

    :deep(.el-tab-pane) {
      display: flex;
      flex-direction: column;
    }
  }

  .table-header {
    flex-shrink: 0;
    margin-bottom: 16px;

    .el-button:not(:first-child) {
      margin-left: 12px;
    }
  }

  .table-container,
  .instruction-scrollbar {
    flex: 1;
    overflow: hidden;
  }

  .instruction-form {
    max-width: 760px;
    padding: 8px 0 24px;
  }
</style>
