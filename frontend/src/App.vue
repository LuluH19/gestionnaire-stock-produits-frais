<script setup>
import { ref, provide } from 'vue'
import ProductForm from './components/ProductForm.vue'
import ProductList from './components/ProductList.vue'
import AlertPanel from './components/AlertPanel.vue'
import StatisticsPanel from './components/StatisticsPanel.vue'
import ExportPanel from './components/ExportPanel.vue'
import ThemeToggle from './components/ThemeToggle.vue'

const activeTab = ref('add')
const notifications = ref([])
let notificationId = 0

const tabs = [
  { id: 'add', label: 'Ajouter', icon: '➕' },
  { id: 'list', label: 'Liste', icon: '📦' },
  { id: 'alerts', label: 'Alertes', icon: '⚠️' },
  { id: 'stats', label: 'Statistiques', icon: '📊' },
  { id: 'export', label: 'Export', icon: '📤' }
]

function addNotification(message, type = 'info') {
  const icons = {
    success: '✅',
    error: '❌',
    warning: '⚠️',
    info: 'ℹ️'
  }
  
  const notification = {
    id: ++notificationId,
    message,
    type,
    icon: icons[type]
  }
  
  notifications.value.push(notification)
  
  // Auto-remove after 5 seconds
  setTimeout(() => {
    removeNotification(notification.id)
  }, 5000)
}

function removeNotification(id) {
  const index = notifications.value.findIndex(n => n.id === id)
  if (index > -1) {
    notifications.value.splice(index, 1)
  }
}

// Provide notification function to child components
provide('addNotification', addNotification)
</script>

<template>
  <div id="app">
    <ThemeToggle />
    
    <header class="app-header">
      <div class="header-content">
        <h1 class="app-title">🥬 Gestionnaire de Stock - Produits Frais</h1>
        <nav class="app-nav">
          <button 
            v-for="tab in tabs" 
            :key="tab.id"
            @click="activeTab = tab.id"
            :class="['nav-btn', { active: activeTab === tab.id }]"
          >
            {{ tab.icon }} {{ tab.label }}
          </button>
        </nav>
      </div>
    </header>

    <main class="app-main">
      <div class="container">
        <!-- Onglet Ajouter -->
        <div v-if="activeTab === 'add'" class="tab-content">
          <ProductForm />
        </div>

        <!-- Onglet Liste -->
        <div v-if="activeTab === 'list'" class="tab-content">
          <ProductList />
        </div>

        <!-- Onglet Alertes -->
        <div v-if="activeTab === 'alerts'" class="tab-content">
          <AlertPanel />
        </div>

        <!-- Onglet Statistiques -->
        <div v-if="activeTab === 'stats'" class="tab-content">
          <StatisticsPanel />
        </div>

        <!-- Onglet Export -->
        <div v-if="activeTab === 'export'" class="tab-content">
          <ExportPanel />
        </div>
      </div>
    </main>

    <!-- Notifications -->
    <div class="notifications" v-if="notifications.length > 0">
      <div 
        v-for="notification in notifications" 
        :key="notification.id"
        :class="['notification', notification.type]"
      >
        <span class="notification-icon">{{ notification.icon }}</span>
        <span class="notification-message">{{ notification.message }}</span>
        <button @click="removeNotification(notification.id)" class="notification-close">×</button>
      </div>
    </div>
  </div>
</template>

<style>
/* Variables CSS pour le thème */
:root {
  --bg-primary: #ffffff;
  --bg-secondary: #f8f9fa;
  --text-primary: #333333;
  --text-secondary: #666666;
  --border-color: #e1e5e9;
  --accent-color: #007bff;
  --success-color: #28a745;
  --warning-color: #ffc107;
  --error-color: #dc3545;
  --shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  --shadow-hover: 0 4px 16px rgba(0, 0, 0, 0.15);
}

[data-theme="dark"] {
  --bg-primary: #1a1a1a;
  --bg-secondary: #2d2d2d;
  --text-primary: #ffffff;
  --text-secondary: #cccccc;
  --border-color: #444444;
  --accent-color: #4dabf7;
  --success-color: #51cf66;
  --warning-color: #ffd43b;
  --error-color: #ff6b6b;
  --shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  --shadow-hover: 0 4px 16px rgba(0, 0, 0, 0.4);
}

/* Reset et styles de base */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  line-height: 1.6;
  transition: background-color 0.3s ease, color 0.3s ease;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Header */
.app-header {
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
}

.app-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 1rem;
  text-align: center;
}

.app-nav {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.nav-btn {
  padding: 0.75rem 1.5rem;
  border: 2px solid var(--border-color);
  background: var(--bg-primary);
  color: var(--text-secondary);
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-btn:hover {
  border-color: var(--accent-color);
  color: var(--accent-color);
  transform: translateY(-1px);
}

.nav-btn.active {
  background: var(--accent-color);
  color: white;
  border-color: var(--accent-color);
}

/* Main content */
.app-main {
  flex: 1;
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Notifications */
.notifications {
  position: fixed;
  top: 1rem;
  left: 1rem;
  right: 1rem;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-width: 400px;
}

.notification {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  box-shadow: var(--shadow);
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.notification.success {
  border-left: 4px solid var(--success-color);
}

.notification.error {
  border-left: 4px solid var(--error-color);
}

.notification.warning {
  border-left: 4px solid var(--warning-color);
}

.notification.info {
  border-left: 4px solid var(--accent-color);
}

.notification-icon {
  font-size: 1.2rem;
}

.notification-message {
  flex: 1;
  color: var(--text-primary);
}

.notification-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.notification-close:hover {
  background: var(--bg-secondary);
}

/* Responsive */
@media (max-width: 768px) {
  .app-title {
    font-size: 1.5rem;
  }
  
  .app-nav {
    justify-content: stretch;
  }
  
  .nav-btn {
    flex: 1;
    justify-content: center;
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }
  
  .notifications {
    left: 0.5rem;
    right: 0.5rem;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 0.5rem;
  }
  
  .app-main {
    padding: 1rem 0;
  }
  
  .container {
    padding: 0 0.5rem;
  }
}
</style>
