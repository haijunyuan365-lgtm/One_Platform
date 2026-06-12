<template>
  <div class="site-shell">
    <AppHeader />
    <main class="site-main">
      <RouterView v-slot="{ Component, route }">
        <Transition name="page" mode="out-in">
          <div :key="route.path">
            <component :is="Component" />
          </div>
        </Transition>
      </RouterView>
    </main>
  </div>
</template>

<script setup lang="ts">
import AppHeader from './AppHeader.vue'
</script>

<style scoped>
.site-shell {
  min-height: 100vh;
}

.site-main {
  min-height: 100vh;
}

.page-enter-active {
  transition:
    opacity 0.24s ease,
    transform 0.24s ease,
    filter 0.24s ease;
}

.page-leave-active {
  transition: opacity 0.18s ease;
}

.page-enter-from {
  opacity: 0;
  filter: blur(8px);
  transform: translateY(12px);
}

.page-leave-to {
  opacity: 0;
}
</style>
