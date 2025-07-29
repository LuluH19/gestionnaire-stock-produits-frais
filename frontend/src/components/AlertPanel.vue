<template>
  <div class="alert-panel">
    <h2>⚠️ Produits à surveiller</h2>

    <div v-if="loading">Chargement des alertes...</div>
    <div v-else-if="alerts.length === 0">Aucun produit proche de la péremption.</div>

    <ul v-else>
      <li v-for="product in alerts" :key="product.id">
        <strong>{{ product.name }}</strong> - Périme le {{ formatDate(product.expirationDate) }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const alerts = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/products/expiring')
    alerts.value = res.data
  } catch (err) {
    console.error('Erreur chargement alertes', err)
  } finally {
    loading.value = false
  }
})

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('fr-FR')
}
</script>

<style scoped>
.alert-panel {
  max-width: 600px;
  margin: 1rem auto;
  padding: 1rem;
  background-color: #fff3cd;
  border: 1px solid #ffecb5;
  border-radius: 8px;
  color: #856404;
}

li {
  margin-bottom: 0.5rem;
}
</style> 