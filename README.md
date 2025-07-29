# 🥬 Gestionnaire de Stock - Produits Frais

## 📋 Présentation / Overview

Ce projet est une application web complète de gestion de stock pour les produits frais, permettant de suivre les entrées, sorties, dates de péremption et catégories de produits. L'application offre une interface moderne et intuitive avec des fonctionnalités avancées de gestion et d'analyse.

**This project is a complete web application for managing fresh product stock, allowing you to track entries, exits, expiration dates, and product categories. The application offers a modern and intuitive interface with advanced management and analysis features.**

---

## ✨ Fonctionnalités principales / Main Features

### 🛠️ Gestion des produits / Product Management
- ✅ **Ajouter, modifier, supprimer des produits** / Add, edit, delete products
- ✅ **Validation des données** côté frontend et backend / Data validation on frontend and backend
- ✅ **Gestion des catégories** (Légumes, Fruits, Viandes, Laitiers) / Category management
- ✅ **Suivi des dates de péremption** / Expiration date tracking

### 🔍 Recherche et filtrage / Search and Filtering
- ✅ **Recherche par nom** / Search by name
- ✅ **Filtrage par catégorie** / Filter by category
- ✅ **Tri par nom, date, quantité, catégorie** / Sort by name, date, quantity, category
- ✅ **Pagination** de la liste des produits / Product list pagination

### 📊 Tableau de bord / Dashboard
- ✅ **Statistiques en temps réel** / Real-time statistics
- ✅ **Répartition par catégorie** / Category breakdown
- ✅ **Produits critiques** (périment bientôt) / Critical products
- ✅ **Alertes de péremption** / Expiration alerts

### 🎨 Interface utilisateur / User Interface
- ✅ **Thème clair/sombre** / Light/dark theme
- ✅ **Interface responsive** / Responsive interface
- ✅ **Notifications en temps réel** / Real-time notifications
- ✅ **Design moderne et intuitif** / Modern and intuitive design

### �� Export et import / Export and Import
- ✅ **Export CSV** des produits / CSV export of products
- ✅ **Export des alertes** / Export alerts
- ✅ **Génération de rapports** / Report generation
- ✅ **Import CSV** de produits / CSV import of products

---

## 🛠️ Technologies utilisées / Technologies Used

### Frontend
- **Vue.js 3** - Framework JavaScript progressif
- **Axios** - Client HTTP pour les requêtes API
- **CSS3** - Styles avec variables CSS et thèmes
- **Vite** - Build tool et dev server

### Backend
- **Java 21** - Langage de programmation
- **Spring Boot 3.5.4** - Framework Java
- **Spring Data JPA** - Persistance des données
- **Spring Security** - Sécurité de l'application
- **Hibernate** - ORM
- **H2 Database** - Base de données en mémoire
- **Maven** - Gestionnaire de dépendances

### Autres
- **REST API** - Architecture API
- **Validation Jakarta** - Validation des données
- **CORS** - Cross-Origin Resource Sharing
- **SQL** - Requêtes de base de données

---

## 🚀 Installation et lancement / Installation and Run

### Prérequis / Prerequisites

- **Node.js** (>= 16)
- **Java 21** (JDK)
- **Maven** (>= 3.6)

### 📦 Installation / Installation

1. **Cloner le repository** / Clone the repository
```bash
git clone <repository-url>
cd gestionnaire-stock-produits-frais
```

2. **Installer les dépendances frontend** / Install frontend dependencies
```bash
cd frontend
npm install
```

### 🏃‍♂️ Lancement / Running

#### Backend (Port 8080)
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend (Port 5173)
```bash
cd frontend
npm run dev
```

#### Accès à l'application / Access the application
- **Frontend** : http://localhost:5173
- **Backend API** : http://localhost:8080
- **Console H2** : http://localhost:8080/h2-console

---

## 📁 Structure du projet / Project Structure

```
gestionnaire-stock-produits-frais/
├── backend/                          # Backend Spring Boot
│   ├── src/main/java/com/stock/
│   │   ├── config/                   # Configuration
│   │   ├── manager/
│   │   │   ├── controller/           # Contrôleurs REST
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # Entités JPA
│   │   │   ├── exception/           # Gestion d'erreurs
│   │   │   ├── repository/          # Repositories
│   │   │   ├── service/             # Services métier
│   │   │   └── StockManagerApplication.java
│   │   └── config/                  # Configuration CORS
│   └── src/main/resources/
│       ├── application.yml           # Configuration Spring Boot
│       └── data.sql                 # Données de test
├── frontend/                        # Frontend Vue.js
│   ├── src/
│   │   ├── components/              # Composants Vue
│   │   │   ├── AlertPanel.vue       # Panel d'alertes
│   │   │   ├── ExportPanel.vue      # Export/Import
│   │   │   ├── ProductForm.vue      # Formulaire produit
│   │   │   ├── ProductList.vue      # Liste des produits
│   │   │   ├── StatisticsPanel.vue  # Statistiques
│   │   │   └── ThemeToggle.vue      # Basculement thème
│   │   ├── App.vue                  # Composant principal
│   │   ├── main.js                  # Point d'entrée
│   │   └── style.css                # Styles globaux
│   ├── package.json                 # Dépendances frontend
│   └── vite.config.js               # Configuration Vite
└── README.md                        # Documentation
```

---

## 🔧 Configuration / Configuration

### Backend Configuration (`application.yml`)
```yaml
server:
  port: 8080

spring:
  h2:
    console:
      enabled: true
      path: /h2-console
  datasource:
    url: jdbc:h2:mem:stockdb
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

### Frontend Configuration
- **Port de développement** : 5173
- **API Backend** : http://localhost:8080
- **CORS** : Configuré pour les ports 3000, 8080, 5173

---

## �� API Endpoints

### Produits / Products
- `GET /api/products` - Liste tous les produits
- `GET /api/products/{id}` - Récupère un produit
- `POST /api/products` - Crée un produit
- `PUT /api/products/{id}` - Met à jour un produit
- `DELETE /api/products/{id}` - Supprime un produit
- `PATCH /api/products/{id}/quantity` - Met à jour la quantité
- `GET /api/products/expiring` - Produits périment bientôt

### Catégories / Categories
- **LEGUMES** - Légumes
- **FRUITS** - Fruits  
- **VIANDES** - Viandes
- **LAITIERS** - Laitiers

---

## �� Fonctionnalités détaillées / Detailed Features

### 📦 Gestion des produits
- **Validation complète** des données (nom, quantité, date, catégorie)
- **Interface intuitive** avec formulaires et modales
- **Actions rapides** (modifier, supprimer) directement dans la liste
- **Gestion des erreurs** avec notifications utilisateur

### 🔍 Recherche et filtrage
- **Recherche en temps réel** par nom de produit
- **Filtrage par catégorie** avec sélecteur déroulant
- **Tri multiple** : nom, date de péremption, quantité, catégorie
- **Pagination** avec 6 produits par page

### �� Statistiques avancées
- **Métriques en temps réel** : total produits, unités, produits critiques
- **Graphiques de répartition** par catégorie avec pourcentages
- **Liste des produits critiques** avec jours restants
- **Calcul automatique** des moyennes et statistiques

### ⚠️ Système d'alertes
- **Détection automatique** des produits périment dans les 3 jours
- **Interface dédiée** pour visualiser les alertes
- **Export des alertes** au format CSV
- **Notifications visuelles** avec codes couleur

### 🎨 Interface utilisateur
- **Thème clair/sombre** avec persistance locale
- **Design responsive** adapté mobile/desktop
- **Animations fluides** et transitions
- **Notifications toast** pour le feedback utilisateur

### �� Export/Import
- **Export CSV** complet avec en-têtes français
- **Génération de rapports** textuels détaillés
- **Import CSV** avec validation et aperçu
- **Gestion des erreurs** d'import avec feedback

---

## �� Tests et données / Testing and Data

### Données de test incluses
Le projet inclut des données de test dans `backend/src/main/resources/data.sql` :
- Tomates, Pommes, Lait, Poulet, Carottes, Fromage, Bananes, Steak

### Test de l'API
```bash
# Tester l'API avec curl
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/expiring
```

---

## 🚀 Déploiement / Deployment

### Production Build
```bash
# Backend
cd backend
./mvnw clean package
java -jar target/manager-0.0.1-SNAPSHOT.jar

# Frontend
cd frontend
npm run build
```

### Variables d'environnement
- `SPRING_PROFILES_ACTIVE=prod` pour la production
- `DATABASE_URL` pour la base de données de production

---

## 🤝 Contribution / Contributing

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request



