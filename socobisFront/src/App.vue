<template>
  <div class="app">
    <AppSidebar />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
import AppSidebar from './components/AppSidebar.vue'

export default {
  name: 'App',
  components: {
    AppSidebar
  }
}
</script>

<style>
.app {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
}

.main-content {
  flex: 1;
  margin-left: 280px;
  padding: var(--spacing-xl);
  animation: fadeIn 0.3s ease-out;
}

@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
    padding: var(--spacing-md);
  }
}
</style>
