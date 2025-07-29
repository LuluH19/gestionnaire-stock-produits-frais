<template>
  <div class="product-list">
    <h2>📦 Liste des produits</h2>

    <!-- Barre de recherche et filtres -->
    <div class="search-filters">
      <div class="search-box">
        <input 
          v-model="searchTerm" 
          placeholder="Rechercher un produit..." 
          class="search-input"
        />
      </div>
      <div class="filters">
        <select v-model="selectedCategory" class="filter-select">
          <option value="">Toutes les catégories</option>
          <option value="LEGUMES">Légumes</option>
          <option value="FRUITS">Fruits</option>
          <option value="VIANDES">Viandes</option>
          <option value="LAITIERS">Laitiers</option>
        </select>
        <select v-model="sortBy" class="filter-select">
          <option value="name">Trier par nom</option>
          <option value="expirationDate">Trier par date de péremption</option>
          <option value="quantity">Trier par quantité</option>
          <option value="category">Trier par catégorie</option>
        </select>
      </div>
    </div>

    <div v-if="loading">Chargement...</div>
    <div v-else-if="filteredProducts.length === 0">Aucun produit trouvé.</div>

    <div v-else class="products-container">
      <!-- Pagination en haut -->
      <div class="pagination-info">
        Affichage {{ startIndex + 1 }}-{{ endIndex }} sur {{ filteredProducts.length }} produits
      </div>

      <ul class="products-grid">
        <li v-for="product in paginatedProducts" :key="product.id" class="product-card">
          <div class="product-header">
            <strong class="product-name">{{ product.name }}</strong>
            <div class="product-actions">
              <button @click="editProduct(product)" class="btn-edit" title="Modifier">
                ✏️
              </button>
              <button @click="deleteProduct(product.id)" class="btn-delete" title="Supprimer">
                🗑️
              </button>
            </div>
          </div>
          
          <div class="product-details">
            <div class="quantity-badge">
              {{ product.quantity }} unités
            </div>
            <div class="category-badge" :class="getCategoryClass(product.category)">
              {{ getCategoryLabel(product.category) }}
            </div>
          </div>
          
          <div class="expiration-info">
            <span class="expiration-label">Péremption :</span>
            <span class="expiration-date" :class="getExpirationClass(product.expirationDate)">
              {{ formatDate(product.expirationDate) }}
            </span>
          </div>
        </li>
      </ul>

      <!-- Pagination en bas -->
      <div class="pagination">
        <button 
          @click="previousPage" 
          :disabled="currentPage === 1"
          class="pagination-btn"
        >
          ← Précédent
        </button>
        <span class="page-info">Page {{ currentPage }} sur {{ totalPages }}</span>
        <button 
          @click="nextPage" 
          :disabled="currentPage === totalPages"
          class="pagination-btn"
        >
          Suivant →
        </button>
      </div>
    </div>

    <!-- Modal de modification -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <h3>Modifier le produit</h3>
        <form @submit.prevent="updateProduct">
          <label>
            Nom :
            <input v-model="editForm.name" required />
          </label>
          <label>
            Quantité :
            <input type="number" v-model="editForm.quantity" required min="0" />
          </label>
          <label>
            Date de péremption :
            <input type="date" v-model="editForm.expirationDate" required />
          </label>
          <label>
            Catégorie :
            <select v-model="editForm.category" required>
              <option value="LEGUMES">Légumes</option>
              <option value="FRUITS">Fruits</option>
              <option value="VIANDES">Viandes</option>
              <option value="LAITIERS">Laitiers</option>
            </select>
          </label>
          <div class="modal-actions">
            <button type="button" @click="closeEditModal" class="btn-cancel">Annuler</button>
            <button type="submit" class="btn-save">Sauvegarder</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const products = ref([])
const loading = ref(true)
const searchTerm = ref('')
const selectedCategory = ref('')
const sortBy = ref('name')
const currentPage = ref(1)
const itemsPerPage = 6

// Modal d'édition
const showEditModal = ref(false)
const editForm = ref({
  id: null,
  name: '',
  quantity: 0,
  expirationDate: '',
  category: ''
})

// Produits filtrés et triés
const filteredProducts = computed(() => {
  let filtered = products.value

  // Filtrage par recherche
  if (searchTerm.value) {
    filtered = filtered.filter(product => 
      product.name.toLowerCase().includes(searchTerm.value.toLowerCase())
    )
  }

  // Filtrage par catégorie
  if (selectedCategory.value) {
    filtered = filtered.filter(product => 
      product.category === selectedCategory.value
    )
  }

  // Tri
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'expirationDate':
        return new Date(a.expirationDate) - new Date(b.expirationDate)
      case 'quantity':
        return b.quantity - a.quantity
      case 'category':
        return a.category.localeCompare(b.category)
      default:
        return 0
    }
  })

  return filtered
})

// Pagination
const totalPages = computed(() => Math.ceil(filteredProducts.value.length / itemsPerPage))
const startIndex = computed(() => (currentPage.value - 1) * itemsPerPage)
const endIndex = computed(() => Math.min(startIndex.value + itemsPerPage, filteredProducts.value.length))
const paginatedProducts = computed(() => 
  filteredProducts.value.slice(startIndex.value, endIndex.value)
)

// Méthodes de pagination
function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

function previousPage() {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

// Méthodes d'édition
function editProduct(product) {
  editForm.value = {
    id: product.id,
    name: product.name,
    quantity: product.quantity,
    expirationDate: product.expirationDate,
    category: product.category
  }
  showEditModal.value = true
}

function closeEditModal() {
  showEditModal.value = false
  editForm.value = {
    id: null,
    name: '',
    quantity: 0,
    expirationDate: '',
    category: ''
  }
}

async function updateProduct() {
  try {
    await axios.put(`http://localhost:8080/api/products/${editForm.value.id}`, editForm.value)
    await loadProducts()
    closeEditModal()
  } catch (error) {
    console.error('Erreur lors de la modification :', error)
  }
}

// Méthodes de suppression
async function deleteProduct(id) {
  if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
    try {
      await axios.delete(`http://localhost:8080/api/products/${id}`)
      await loadProducts()
    } catch (error) {
      console.error('Erreur lors de la suppression :', error)
    }
  }
}

// Méthodes utilitaires
function getCategoryLabel(category) {
  const labels = {
    'LEGUMES': 'Légumes',
    'FRUITS': 'Fruits',
    'VIANDES': 'Viandes',
    'LAITIERS': 'Laitiers'
  }
  return labels[category] || category
}

function getCategoryClass(category) {
  return `category-${category.toLowerCase()}`
}

function getExpirationClass(expirationDate) {
  const daysUntilExpiration = Math.ceil((new Date(expirationDate) - new Date()) / (1000 * 60 * 60 * 24))
  if (daysUntilExpiration < 0) return 'expired'
  if (daysUntilExpiration <= 3) return 'expiring-soon'
  if (daysUntilExpiration <= 7) return 'expiring-week'
  return 'expiring-later'
}

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('fr-FR')
}

// Chargement des données
async function loadProducts() {
  try {
    const response = await axios.get('http://localhost:8080/api/products')
    products.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement des produits :', error)
  } finally {
    loading.value = false
  }
}

onMounted(loadProducts)
</script>

<style scoped>
.product-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
}

.search-filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.search-box {
  flex: 1;
  min-width: 200px;
}

.search-input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #007bff;
}

.filters {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.filter-select {
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  background: white;
  font-size: 1rem;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.filter-select:focus {
  outline: none;
  border-color: #007bff;
}

.products-container {
  margin-top: 2rem;
}

.pagination-info {
  text-align: center;
  margin-bottom: 1rem;
  color: #666;
  font-size: 0.9rem;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  list-style: none;
  padding: 0;
  margin: 0;
}

.product-card {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.product-name {
  font-size: 1.2rem;
  color: #333;
  margin: 0;
}

.product-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-edit, .btn-delete {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.btn-edit:hover {
  background-color: #e3f2fd;
}

.btn-delete:hover {
  background-color: #ffebee;
}

.product-details {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.quantity-badge {
  background: #e3f2fd;
  color: #1976d2;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.category-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  color: white;
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

.expiration-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.expiration-label {
  font-size: 0.9rem;
  color: #666;
}

.expiration-date {
  font-weight: 500;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

.expired {
  background: #ffebee;
  color: #d32f2f;
}

.expiring-soon {
  background: #fff3e0;
  color: #f57c00;
}

.expiring-week {
  background: #fff8e1;
  color: #f57f17;
}

.expiring-later {
  background: #e8f5e8;
  color: #388e3c;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.pagination-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #007bff;
  background: white;
  color: #007bff;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #007bff;
  color: white;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-weight: 500;
  color: #666;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: #333;
}

.modal-content label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.modal-content input,
.modal-content select {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 6px;
  margin-bottom: 1rem;
  font-size: 1rem;
}

.modal-content input:focus,
.modal-content select:focus {
  outline: none;
  border-color: #007bff;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

.btn-cancel {
  padding: 0.75rem 1.5rem;
  border: 1px solid #ccc;
  background: white;
  color: #666;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel:hover {
  background: #f5f5f5;
}

.btn-save {
  padding: 0.75rem 1.5rem;
  border: none;
  background: #007bff;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-save:hover {
  background: #0056b3;
}

/* Responsive */
@media (max-width: 768px) {
  .search-filters {
    flex-direction: column;
  }
  
  .filters {
    justify-content: stretch;
  }
  
  .filter-select {
    flex: 1;
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .product-header {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .modal-content {
    margin: 1rem;
    width: calc(100% - 2rem);
  }
}
</style> 