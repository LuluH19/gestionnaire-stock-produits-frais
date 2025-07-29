<template>
  <div class="export-panel">
    <h2>📤 Export/Import des données</h2>
    
    <div class="export-options">
      <div class="export-card">
        <h3>📊 Export CSV</h3>
        <p>Exporter tous les produits au format CSV</p>
        <button @click="exportCSV" class="export-btn csv-btn">
          📄 Exporter CSV
        </button>
      </div>
      
      <div class="export-card">
        <h3>📋 Rapport PDF</h3>
        <p>Générer un rapport PDF avec statistiques</p>
        <button @click="exportPDF" class="export-btn pdf-btn">
          📄 Générer PDF
        </button>
      </div>
      
      <div class="export-card">
        <h3>📅 Alertes</h3>
        <p>Exporter les produits proches de la péremption</p>
        <button @click="exportAlerts" class="export-btn alerts-btn">
          ⚠️ Exporter Alertes
        </button>
      </div>
    </div>

    <!-- Section Import -->
    <div class="import-section">
      <h3>📥 Import des données</h3>
      <div class="import-card">
        <p>Importer des produits depuis un fichier CSV</p>
        <div class="file-upload">
          <input 
            type="file" 
            ref="fileInput" 
            @change="handleFileUpload" 
            accept=".csv"
            class="file-input"
          />
          <button @click="triggerFileUpload" class="upload-btn">
            📁 Choisir un fichier CSV
          </button>
        </div>
        <div v-if="importPreview.length > 0" class="import-preview">
          <h4>Aperçu des données à importer :</h4>
          <div class="preview-table">
            <table>
              <thead>
                <tr>
                  <th>Nom</th>
                  <th>Catégorie</th>
                  <th>Quantité</th>
                  <th>Date de péremption</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in importPreview.slice(0, 5)" :key="index">
                  <td>{{ item.name }}</td>
                  <td>{{ getCategoryLabel(item.category) }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>{{ item.expirationDate }}</td>
                </tr>
              </tbody>
            </table>
            <p v-if="importPreview.length > 5" class="preview-note">
              ... et {{ importPreview.length - 5 }} autres produits
            </p>
          </div>
          <div class="import-actions">
            <button @click="confirmImport" class="confirm-btn" :disabled="importing">
              ✅ Confirmer l'import ({{ importPreview.length }} produits)
            </button>
            <button @click="cancelImport" class="cancel-btn">
              ❌ Annuler
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="exporting || importing" class="export-status">
      <div class="loading-spinner"></div>
      <span>{{ exporting ? 'Génération en cours...' : 'Import en cours...' }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import axios from 'axios'

const addNotification = inject('addNotification')
const exporting = ref(false)
const importing = ref(false)
const fileInput = ref(null)
const importPreview = ref([])

async function exportCSV() {
  exporting.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/products')
    const products = response.data
    
    const csvContent = generateCSV(products)
    downloadFile(csvContent, 'produits.csv', 'text/csv')
    
    addNotification('Export CSV réussi !', 'success')
  } catch (error) {
    addNotification('Erreur lors de l\'export CSV', 'error')
    console.error(error)
  } finally {
    exporting.value = false
  }
}

async function exportPDF() {
  exporting.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/products')
    const products = response.data
    
    // Créer un PDF simple avec jsPDF ou une alternative
    const pdfContent = generatePDFContent(products)
    downloadFile(pdfContent, 'rapport-produits.txt', 'text/plain')
    
    addNotification('Rapport généré ! (fichier texte)', 'success')
  } catch (error) {
    addNotification('Erreur lors de la génération du rapport', 'error')
    console.error(error)
  } finally {
    exporting.value = false
  }
}

async function exportAlerts() {
  exporting.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/products/expiring')
    const alerts = response.data
    
    const csvContent = generateCSV(alerts)
    downloadFile(csvContent, 'alertes-peremption.csv', 'text/csv')
    
    addNotification('Export des alertes réussi !', 'success')
  } catch (error) {
    addNotification('Erreur lors de l\'export des alertes', 'error')
    console.error(error)
  } finally {
    exporting.value = false
  }
}

function generateCSV(products) {
  const headers = ['Nom', 'Catégorie', 'Quantité', 'Date de péremption', 'Jours restants']
  const rows = products.map(product => {
    const daysUntilExpiration = Math.ceil(
      (new Date(product.expirationDate) - new Date()) / (1000 * 60 * 60 * 24)
    )
    return [
      product.name,
      getCategoryLabel(product.category),
      product.quantity,
      formatDate(product.expirationDate),
      daysUntilExpiration
    ]
  })
  
  const csvContent = [headers, ...rows]
    .map(row => row.map(cell => `"${cell}"`).join(','))
    .join('\n')
  
  return '\ufeff' + csvContent // BOM pour Excel
}

function generatePDFContent(products) {
  const stats = calculateStats(products)
  
  const content = `
RAPPORT PRODUITS FRAIS
======================

Date de génération: ${new Date().toLocaleDateString('fr-FR')}

STATISTIQUES GÉNÉRALES:
- Total produits: ${stats.totalProducts}
- Total unités: ${stats.totalQuantity}
- Produits périment bientôt: ${stats.expiringSoon}
- Moyenne jours avant péremption: ${stats.averageExpirationDays}

RÉPARTITION PAR CATÉGORIE:
${stats.categoryBreakdown.map(cat => `- ${cat.name}: ${cat.count} produits (${cat.percentage}%)`).join('\n')}

PRODUITS LES PLUS CRITIQUES:
${stats.criticalProducts.map(p => `- ${p.name}: ${p.daysUntilExpiration} jours restants (${p.quantity} unités)`).join('\n')}

LISTE COMPLÈTE DES PRODUITS:
${products.map(p => `- ${p.name} (${getCategoryLabel(p.category)}): ${p.quantity} unités, périme le ${formatDate(p.expirationDate)}`).join('\n')}
  `
  
  return content
}

function calculateStats(products) {
  const now = new Date()
  const threeDaysFromNow = new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000)
  
  const expiringSoon = products.filter(product => 
    new Date(product.expirationDate) <= threeDaysFromNow
  ).length

  const totalQuantity = products.reduce((sum, product) => sum + product.quantity, 0)
  
  const averageExpirationDays = Math.round(
    products.reduce((sum, product) => {
      const daysUntilExpiration = Math.ceil(
        (new Date(product.expirationDate) - now) / (1000 * 60 * 60 * 24)
      )
      return sum + Math.max(0, daysUntilExpiration)
    }, 0) / products.length
  )
  
  const categoryCount = {}
  products.forEach(product => {
    categoryCount[product.category] = (categoryCount[product.category] || 0) + 1
  })

  const categoryBreakdown = Object.entries(categoryCount).map(([category, count]) => ({
    name: getCategoryLabel(category),
    count,
    percentage: Math.round((count / products.length) * 100)
  }))

  const criticalProducts = products
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
    totalProducts: products.length,
    expiringSoon,
    totalQuantity,
    averageExpirationDays,
    categoryBreakdown,
    criticalProducts
  }
}

// Fonctions d'import
function triggerFileUpload() {
  fileInput.value.click()
}

function handleFileUpload(event) {
  const file = event.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const csvContent = e.target.result
      console.log('Contenu CSV brut:', csvContent.substring(0, 500)) // Debug
      const products = parseCSV(csvContent)
      console.log('Produits parsés:', products) // Debug
      importPreview.value = products
      addNotification(`${products.length} produits détectés dans le fichier`, 'info')
    } catch (error) {
      addNotification('Erreur lors de la lecture du fichier CSV', 'error')
      console.error('Erreur parsing CSV:', error)
    }
  }
  reader.readAsText(file)
}

function parseCSV(csvContent) {
  const lines = csvContent.split('\n').filter(line => line.trim())
  const headers = lines[0].split(',').map(h => h.replace(/"/g, '').trim())
  
  const products = []
  for (let i = 1; i < lines.length; i++) {
    const values = lines[i].split(',').map(v => v.replace(/"/g, '').trim())
    if (values.length >= 4) {
      const product = {
        name: values[0],
        category: getCategoryFromLabel(values[1]),
        quantity: parseInt(values[2]) || 1,
        expirationDate: parseDate(values[3])
      }
      products.push(product)
    }
  }
  
  return products
}

function getCategoryFromLabel(label) {
  const categoryMap = {
    'Légumes': 'LEGUMES',
    'Légume': 'LEGUMES',
    'Vegetables': 'LEGUMES',
    'Vegetable': 'LEGUMES',
    'Fruits': 'FRUITS',
    'Fruit': 'FRUITS',
    'Viandes': 'VIANDES',
    'Viande': 'VIANDES',
    'Meat': 'VIANDES',
    'Laitiers': 'LAITIERS',
    'Laitier': 'LAITIERS',
    'Dairy': 'LAITIERS',
    'LEGUMES': 'LEGUMES',
    'FRUITS': 'FRUITS',
    'VIANDES': 'VIANDES',
    'LAITIERS': 'LAITIERS'
  }
  
  // Nettoyer le label et chercher une correspondance
  const cleanLabel = label.trim().toLowerCase()
  
  for (const [key, value] of Object.entries(categoryMap)) {
    if (cleanLabel === key.toLowerCase()) {
      return value
    }
  }
  
  // Si aucune correspondance, essayer de deviner basé sur le nom du produit
  return guessCategoryFromName(label)
}

function guessCategoryFromName(name) {
  const nameLower = name.toLowerCase()
  
  // Fruits
  if (nameLower.includes('pomme') || nameLower.includes('banane') || 
      nameLower.includes('orange') || nameLower.includes('fraise') ||
      nameLower.includes('raisin') || nameLower.includes('poire') ||
      nameLower.includes('ananas') || nameLower.includes('mangue')) {
    return 'FRUITS'
  }
  
  // Viandes
  if (nameLower.includes('poulet') || nameLower.includes('boeuf') ||
      nameLower.includes('porc') || nameLower.includes('agneau') ||
      nameLower.includes('saucisse') || nameLower.includes('jambon') ||
      nameLower.includes('steak') || nameLower.includes('viande')) {
    return 'VIANDES'
  }
  
  // Laitiers
  if (nameLower.includes('lait') || nameLower.includes('fromage') ||
      nameLower.includes('yaourt') || nameLower.includes('beurre') ||
      nameLower.includes('crème') || nameLower.includes('yogurt')) {
    return 'LAITIERS'
  }
  
}

function parseDate(dateStr) {
  // Essayer différents formats de date
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) {
    // Essayer d'autres formats
    const formats = [
      dateStr, // Format original
      dateStr.replace(/(\d{2})\/(\d{2})\/(\d{4})/, '$3-$2-$1'), // DD/MM/YYYY -> YYYY-MM-DD
      dateStr.replace(/(\d{2})-(\d{2})-(\d{4})/, '$3-$2-$1'), // DD-MM-YYYY -> YYYY-MM-DD
      dateStr.replace(/(\d{4})-(\d{2})-(\d{2})/, '$1-$2-$3'), // YYYY-MM-DD (déjà bon)
    ]
    
    for (const format of formats) {
      const testDate = new Date(format)
      if (!isNaN(testDate.getTime())) {
        return testDate.toISOString().split('T')[0]
      }
    }
    
    // Si aucune date valide, utiliser aujourd'hui + 7 jours
    const futureDate = new Date()
    futureDate.setDate(futureDate.getDate() + 7)
    return futureDate.toISOString().split('T')[0]
  }
  return date.toISOString().split('T')[0]
}

async function confirmImport() {
  importing.value = true
  try {
    let successCount = 0
    let errorCount = 0
    
    for (const product of importPreview.value) {
      try {
        await axios.post('http://localhost:8080/api/products', product)
        successCount++
      } catch (error) {
        errorCount++
        console.error('Erreur import produit:', product.name, error)
      }
    }
    
    if (successCount > 0) {
      addNotification(`${successCount} produits importés avec succès !`, 'success')
    }
    if (errorCount > 0) {
      addNotification(`${errorCount} produits n'ont pas pu être importés`, 'warning')
    }
    
    // Réinitialiser
    importPreview.value = []
    if (fileInput.value) {
      fileInput.value.value = ''
    }
    
  } catch (error) {
    addNotification('Erreur lors de l\'import', 'error')
    console.error(error)
  } finally {
    importing.value = false
  }
}

function cancelImport() {
  importPreview.value = []
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

function getCategoryLabel(category) {
  const labels = {
    'LEGUMES': 'Légumes',
    'FRUITS': 'Fruits',
    'VIANDES': 'Viandes',
    'LAITIERS': 'Laitiers'
  }
  return labels[category] || category
}

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('fr-FR')
}

function downloadFile(content, filename, mimeType) {
  const blob = new Blob([content], { type: mimeType })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
.export-panel {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
}

.export-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.export-card {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.export-card:hover {
  transform: translateY(-2px);
}

.export-card h3 {
  margin-top: 0;
  margin-bottom: 0.5rem;
  color: #333;
  font-size: 1.2rem;
}

.export-card p {
  color: #666;
  margin-bottom: 1.5rem;
  line-height: 1.5;
}

.export-btn {
  width: 100%;
  padding: 1rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.csv-btn {
  background: #28a745;
  color: white;
}

.csv-btn:hover {
  background: #218838;
}

.pdf-btn {
  background: #dc3545;
  color: white;
}

.pdf-btn:hover {
  background: #c82333;
}

.alerts-btn {
  background: #ffc107;
  color: #212529;
}

.alerts-btn:hover {
  background: #e0a800;
}

/* Section Import */
.import-section {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.import-section h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
  font-size: 1.2rem;
}

.import-card p {
  color: #666;
  margin-bottom: 1.5rem;
  line-height: 1.5;
}

.file-upload {
  display: flex;
  gap: 1rem;
  align-items: center;
  margin-bottom: 1rem;
}

.file-input {
  display: none;
}

.upload-btn {
  padding: 0.75rem 1.5rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: background-color 0.2s ease;
}

.upload-btn:hover {
  background: #0056b3;
}

.import-preview {
  margin-top: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.import-preview h4 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
}

.preview-table {
  overflow-x: auto;
}

.preview-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.preview-table th,
.preview-table td {
  padding: 0.5rem;
  text-align: left;
  border-bottom: 1px solid #e1e5e9;
}

.preview-table th {
  background: #e9ecef;
  font-weight: 600;
}

.preview-note {
  font-size: 0.8rem;
  color: #666;
  font-style: italic;
  margin-top: 0.5rem;
}

.import-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.confirm-btn {
  padding: 0.75rem 1.5rem;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: background-color 0.2s ease;
}

.confirm-btn:hover:not(:disabled) {
  background: #218838;
}

.confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.cancel-btn {
  padding: 0.75rem 1.5rem;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: background-color 0.2s ease;
}

.cancel-btn:hover {
  background: #5a6268;
}

.export-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin-top: 2rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 768px) {
  .export-options {
    grid-template-columns: 1fr;
  }
  
  .export-card {
    padding: 1rem;
  }
  
  .file-upload {
    flex-direction: column;
    align-items: stretch;
  }
  
  .import-actions {
    flex-direction: column;
  }
}
</style> 