<template>
  <div class="product-list">
    <h2>📦 Liste des produits</h2>

    <div v-if="loading">Chargement...</div>
    <div v-else-if="products.length === 0">Aucun produit trouvé.</div>

    <ul v-else>
      <li v-for="product in products" :key="product.id">
        <strong>{{ product.name }}</strong> - {{ product.quantity }} unités
        <br />
        Catégorie : {{ product.category }} | Péremption : {{ formatDate(product.expirationDate) }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const products = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/products')
    products.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement des produits :', error)
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
.product-list {
  max-width: 600px;
  margin: 0 auto;
  padding: 1rem;
}

li {
  margin-bottom: 1rem;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 8px;
}
</style> 