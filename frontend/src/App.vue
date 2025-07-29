<script setup>
import ProductForm from './components/ProductForm.vue'
import ProductList from './components/ProductList.vue'
import AlertPanel from './components/AlertPanel.vue'
</script>

<template>
  <div>
    
  </div>
  <ProductForm />
  <ProductList />
  <AlertPanel />
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
