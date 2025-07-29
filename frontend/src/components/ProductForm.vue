<template>
    <div class="product-form">
      <h2>➕ Ajouter un produit</h2>
      <form @submit.prevent="submitForm">
        <label>
          Nom :
          <input v-model="form.name" required />
        </label>
  
        <label>
          Quantité :
          <input type="number" v-model="form.quantity" required min="1" />
        </label>
  
        <label>
          Date de péremption :
          <input type="date" v-model="form.expirationDate" required />
        </label>
  
        <label>
          Catégorie :
          <select v-model="form.category" required>
            <option value="">Sélectionner une catégorie</option>
            <option value="LEGUMES">Légumes</option>
            <option value="FRUITS">Fruits</option>
            <option value="VIANDES">Viandes</option>
            <option value="LAITIERS">Laitiers</option>
          </select>
        </label>
  
        <button type="submit">Ajouter</button>
      </form>
  
      <div v-if="successMessage" class="success">{{ successMessage }}</div>
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import axios from 'axios'
  
  const form = ref({
    name: '',
    quantity: 1,
    expirationDate: '',
    category: ''
  })
  
  const successMessage = ref('')
  const errorMessage = ref('')
  
  async function submitForm() {
    try {
      await axios.post('http://localhost:8080/api/products', form.value)
      successMessage.value = 'Produit ajouté avec succès !'
      errorMessage.value = ''
      form.value = { name: '', quantity: 1, expirationDate: '', category: '' }
    } catch (err) {
      errorMessage.value = 'Erreur lors de l\'ajout du produit.'
      successMessage.value = ''
      console.error(err)
    }
  }
  </script>
  
  <style scoped>
  .product-form {
    max-width: 600px;
    margin: 1rem auto;
    padding: 1rem;
    border: 1px solid #ddd;
    border-radius: 8px;
  }
  
  label {
    display: block;
    margin-bottom: 0.5rem;
  }
  
  input, select {
    width: 100%;
    padding: 0.5rem;
    margin-top: 0.2rem;
    margin-bottom: 1rem;
    box-sizing: border-box;
  }
  
  button {
    padding: 0.5rem 1rem;
    background-color: #2ecc71;
    border: none;
    border-radius: 4px;
    color: white;
    cursor: pointer;
  }
  
  .success {
    color: green;
    margin-top: 1rem;
  }
  .error {
    color: red;
    margin-top: 1rem;
  }
  </style>
  