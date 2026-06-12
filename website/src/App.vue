<template>
  <div class="catalog-shell">
    <el-scrollbar ref="scrollbarRef" class="catalog-scrollbar" height="100vh" @scroll="onScroll">
      <div class="catalog-frame">
        <RouterView />
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { ref, provide } from 'vue'

const scrollY = ref(0)
provide('scrollY', scrollY)

function onScroll({ scrollTop }: { scrollTop: number }) {
  scrollY.value = scrollTop
}
</script>

<style scoped>
.catalog-shell {
  min-height: 100%;
  background: #000;
}

.catalog-scrollbar {
  height: 100vh;
}

.catalog-frame {
  min-height: calc(100vh - 16px);
  margin: 8px;
  border: 8px solid #000;
  background: #fff;
}
</style>
