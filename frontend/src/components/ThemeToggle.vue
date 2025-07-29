<template>
  <div class="theme-toggle">
    <button @click="toggleTheme" class="theme-btn" :title="theme === 'dark' ? 'Passer au mode clair' : 'Passer au mode sombre'">
      <span v-if="theme === 'dark'">☀️</span>
      <span v-else>🌙</span>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const theme = ref('light')

function toggleTheme() {
  theme.value = theme.value === 'light' ? 'dark' : 'light'
  document.documentElement.setAttribute('data-theme', theme.value)
  localStorage.setItem('theme', theme.value)
}

onMounted(() => {
  const savedTheme = localStorage.getItem('theme') || 'light'
  theme.value = savedTheme
  document.documentElement.setAttribute('data-theme', savedTheme)
})
</script>

<style scoped>
.theme-toggle {
  position: fixed;
  top: 1rem;
  right: 1rem;
  z-index: 1000;
}

.theme-btn {
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid #e1e5e9;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.theme-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

[data-theme="dark"] .theme-btn {
  background: rgba(30, 30, 30, 0.9);
  border-color: #444;
  color: #fff;
}
</style> 