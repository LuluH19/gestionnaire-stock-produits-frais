<template>
  <div class="statistics-panel">
    <h2>📊 Statistiques</h2>
    
    <div v-if="loading">Chargement des statistiques...</div>
    
    <div v-else class="stats-grid">
      <!-- Statistiques générales -->
      <div class="stat-card">
        <div class="stat-icon">📦</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalProducts }}</div>
          <div class="stat-label">Total produits</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">⚠️</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.expiringSoon }}</div>
          <div class="stat-label">Périment bientôt</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalQuantity }}</div>
          <div class="stat-label">Total unités</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📅</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.averageExpirationDays }}</div>
          <div class="stat-label">Jours avant péremption (moy.)</div>
        </div>
      </div>
    </div>
    
    <!-- Répartition par catégorie -->
    <div class="category-stats">
      <h3>Répartition par catégorie</h3>
      <div class="category-chart">
        <div 
          v-for="category in stats.categoryBreakdown" 
          :key="category.name"
          class="category-bar"
        >
          <div class="category-info">
            <span class="category-name">{{ category.name }}</span>
            <span class="category-count">{{ category.count }} produits</span>
          </div>
          <div class="bar-container">
            <div 
              class="bar-fill" 
              :style="{ width: category.percentage + '%' }"
              :class="getCategoryColor(category.name)"
            ></div>
          </div>
          <span class="percentage">{{ category.percentage }}%</span>
        </div>
      </div>
    </div>
    
    <!-- Produits les plus critiques -->
    <div class="critical-products">
      <h3>Produits les plus critiques</h3>
      <div v-if="stats.criticalProducts.length === 0" class="no-critical">
        Aucun produit critique
      </div>
      <ul v-else class="critical-list">
        <li 
          v-for="product in stats.criticalProducts" 
          :key="product.id"
          class="critical-item"
        >
          <div class="product-info">
            <strong>{{ product.name }}</strong>
            <span class="days-left" :class="getDaysClass(product.daysUntilExpiration)">
              {{ product.daysUntilExpiration }} jours
            </span>
          </div>
          <div class="product-details">
            {{ product.quantity }} unités - {{ getCategoryLabel(product.category) }}
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const products = ref([])
const loading = ref(true)

const stats = computed(() => {
  if (products.value.length === 0) {
    return {
      totalProducts: 0,
      expiringSoon: 0,
      totalQuantity: 0,
      averageExpirationDays: 0,
      categoryBreakdown: [],
      criticalProducts: []
    }
  }

  const now = new Date()
  const threeDaysFromNow = new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000)
  
  const expiringSoon = products.value.filter(product => 
    new Date(product.expirationDate) <= threeDaysFromNow
  ).length

  const totalQuantity = products.value.reduce((sum, product) => sum + product.quantity, 0)
  
  const averageExpirationDays = Math.round(
    products.value.reduce((sum, product) => {
      const daysUntilExpiration = Math.ceil(
        (new Date(product.expirationDate) - now) / (1000 * 60 * 60 * 24)
      )
      return sum + Math.max(0, daysUntilExpiration)
    }, 0) / products.value.length
  )

  // Répartition par catégorie
  const categoryCount = {}
  products.value.forEach(product => {
    categoryCount[product.category] = (categoryCount[product.category] || 0) + 1
  })

  const categoryBreakdown = Object.entries(categoryCount).map(([category, count]) => ({
    name: category,
    count,
    percentage: Math.round((count / products.value.length) * 100)
  }))

  // Produits critiques (périment dans les 3 jours)
  const criticalProducts = products.value
    .filter(product => new Date(product.expirationDate) <= threeDaysFromNow)
    .map(product => ({
      ...product,
      daysUntilExpiration: Math.ceil(
        (new Date(product.expirationDate) - now) / (1000 * 60 * 60 * 24)
      )
    }))
    .sort((a, b) => a.daysUntilExpiration - b.daysUntilExpiration)
    .slice(0, 5)

  return {
    totalProducts: products.value.length,
    expiringSoon,
    totalQuantity,
    averageExpirationDays,
    categoryBreakdown,
    criticalProducts
  }
})

function getCategoryLabel(category) {
  const labels = {
    'LEGUMES': 'Légumes',
    'FRUITS': 'Fruits',
    'VIANDES': 'Viandes',
    'LAITIERS': 'Laitiers'
  }
  return labels[category] || category
}

function getCategoryColor(category) {
  const colors = {
    'LEGUMES': 'category-legumes',
    'FRUITS': 'category-fruits',
    'VIANDES': 'category-viandes',
    'LAITIERS': 'category-laitiers'
  }
  return colors[category] || 'category-default'
}

function getDaysClass(days) {
  if (days < 0) return 'days-expired'
  if (days <= 1) return 'days-critical'
  if (days <= 3) return 'days-warning'
  return 'days-ok'
}

async function loadProducts() {
  try {
    const response = await axios.get('http://localhost:8080/api/products')
    products.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement des statistiques :', error)
  } finally {
    loading.value = false
  }
}

onMounted(loadProducts)
</script>

<style scoped>
.statistics-panel {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 2rem;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 12px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 0.9rem;
  color: #666;
  margin-top: 0.25rem;
}

.category-stats {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-stats h3 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: #333;
}

.category-chart {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.category-bar {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.category-info {
  display: flex;
  flex-direction: column;
  min-width: 120px;
}

.category-name {
  font-weight: 500;
  color: #333;
}

.category-count {
  font-size: 0.8rem;
  color: #666;
}

.bar-container {
  flex: 1;
  height: 20px;
  background: #f1f3f4;
  border-radius: 10px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.3s ease;
}

.category-legumes {
  background: #4caf50;
}

.category-fruits {
  background: #ff9800;
}

.category-viandes {
  background: #f44336;
}

.category-laitiers {
  background: #2196f3;
}

.category-default {
  background: #9e9e9e;
}

.percentage {
  font-weight: 500;
  color: #333;
  min-width: 40px;
  text-align: right;
}

.critical-products {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.critical-products h3 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: #333;
}

.no-critical {
  text-align: center;
  color: #666;
  font-style: italic;
  padding: 2rem;
}

.critical-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.critical-item {
  padding: 1rem;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  margin-bottom: 0.5rem;
  background: #fff8e1;
}

.critical-item:last-child {
  margin-bottom: 0;
}

.product-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.days-left {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.days-expired {
  background: #ffebee;
  color: #d32f2f;
}

.days-critical {
  background: #fff3e0;
  color: #f57c00;
}

.days-warning {
  background: #fff8e1;
  color: #f57f17;
}

.days-ok {
  background: #e8f5e8;
  color: #388e3c;
}

.product-details {
  font-size: 0.9rem;
  color: #666;
}

/* Responsive */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 1rem;
  }
  
  .stat-card {
    padding: 1rem;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 1.5rem;
  }
  
  .stat-value {
    font-size: 1.5rem;
  }
  
  .category-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 0.5rem;
  }
  
  .category-info {
    min-width: auto;
  }
  
  .percentage {
    text-align: left;
  }
}
</style> 