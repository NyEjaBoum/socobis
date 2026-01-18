<!-- src/components/AppSidebar.vue -->
<template>
  <aside class="sidebar">
    <div class="sidebar-header">
      <div class="logo">
        <div class="logo-icon">🏭</div>
        <h1 class="logo-text">Socobis</h1>
      </div>
      <p class="logo-subtitle">Gestion de fabrication</p>
    </div>

    <nav class="sidebar-nav">
      <div class="nav-section">
        <p class="nav-section-title">Production</p>
        
        <router-link to="/of" class="nav-item" :class="{ active: $route.path === '/of' }">
          <span class="nav-icon">📋</span>
          <span class="nav-text">Ordres de Fabrication</span>
        </router-link>

        <router-link to="/of/create" class="nav-item" :class="{ active: $route.path === '/of/create' }">
          <span class="nav-icon">➕</span>
          <span class="nav-text">Nouvel OF</span>
        </router-link>

        <router-link to="/fabrication" class="nav-item" :class="{ active: $route.path.startsWith('/fabrication') }">
          <span class="nav-icon">🏭</span>
          <span class="nav-text">Fabrications</span>
        </router-link>
      </div>
    </nav>

    <div class="sidebar-footer">
      <div class="user-info">
        <div class="user-avatar">👤</div>
        <div class="user-details">
          <p class="user-name">Utilisateur</p>
          <p class="user-role">Admin</p>
        </div>
      </div>
    </div>
  </aside>
</template>

<script>
export default {
  name: 'AppSidebar'
}
</script>

<style scoped>
.sidebar {
  width: 280px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  color: var(--white);
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-xl);
  z-index: 1000;
}

.sidebar-header {
  padding: var(--spacing-xl) var(--spacing-lg);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.logo-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.logo-text {
  font-size: var(--font-size-2xl);
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #fff 0%, rgba(255, 255, 255, 0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-subtitle {
  font-size: var(--font-size-xs);
  opacity: 0.8;
  margin: 0;
  padding-left: 48px;
}

.sidebar-nav {
  flex: 1;
  padding: var(--spacing-lg) 0;
  overflow-y: auto;
}

.nav-section {
  margin-bottom: var(--spacing-xl);
}

.nav-section-title {
  font-size: var(--font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 0 var(--spacing-lg);
  margin-bottom: var(--spacing-md);
  opacity: 0.6;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: 12px var(--spacing-lg);
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  transition: all var(--transition-base);
  position: relative;
  margin: 0 var(--spacing-md);
  border-radius: var(--border-radius-md);
}

.nav-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: var(--white);
  border-radius: 0 2px 2px 0;
  transition: height var(--transition-base);
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--white);
  transform: translateX(4px);
}

.nav-item:hover::before {
  height: 24px;
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.15);
  color: var(--white);
  font-weight: 600;
}

.nav-item.active::before {
  height: 32px;
}

.nav-icon {
  font-size: 20px;
  width: 24px;
  text-align: center;
}

.nav-text {
  font-size: var(--font-size-sm);
}

.sidebar-footer {
  padding: var(--spacing-lg);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--border-radius-md);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: var(--font-size-sm);
  font-weight: 600;
  margin: 0;
}

.user-role {
  font-size: var(--font-size-xs);
  opacity: 0.7;
  margin: 0;
}

@media (max-width: 768px) {
  .sidebar {
    width: 70px;
  }
  
  .logo-text,
  .logo-subtitle,
  .nav-section-title,
  .nav-text,
  .user-details {
    display: none;
  }
  
  .sidebar-header {
    padding: var(--spacing-lg) var(--spacing-sm);
  }
  
  .logo {
    justify-content: center;
  }
  
  .nav-item {
    justify-content: center;
    padding: 12px var(--spacing-sm);
  }
}
</style>
