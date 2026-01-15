[🇫🇷 Lire en français](README.fr.md) | [🇬🇧 Read in English](README.md)

# mrisk – Microservice d’analyse du risque de diabète de type 2.

---
**Projet** : MicroDiab  
**Organisation** : MediLabo Solutions  
**Auteur** : michaudj  
**Date** : Janvier 2026  
**Version** : 1.0.0
---

## 📌 Rôle
Le microservice **mrisk** est un microservice back-end d’analyse médicale chargé d’évaluer le niveau de risque 
de diabète de type 2 pour un patient, pour l'application **MicroDiab**. Le microservice **mrisk** est un service 
**stateless** et ne dispose d’aucune base de données propre.

Il analyse :
* les données patient (âge, sexe) fournies par le microservice **mpatient**.
* les notes médicales fournies par le microservice **mnotes**.

Le résultat est un rapport de risque calculé selon des règles médicales définies.

**Fonctionnalités clés** :
- Calcul du **niveau de risque de diabète de type 2**.
- Communication inter-microservices via **Feign Client**.
- Documentation interactive via **Swagger UI** et **Javadoc**.
- Traçage distribué avec **Zipkin** et **Micrometer Tracing**.
- Containerisé avec **Docker** (compatible avec `docker-compose`).

---

## 📦 Dépendances
**Microservices Internes**

| Microservice      | Rôle                              | Port |
|-------------------|-----------------------------------|------|
| **eureka-server** | Découverte de services            | 9102 |
| **zipkin**        | Traçage distribué                 | 9411 |
| **mgateway**      | Passerelle API + Authentification | 9010 |
| **mpatient**      | Données patients                  | 9001 |
| **mnotes**        | Notes médicales                   | 9002 |

**Communication** : Feign (appels depuis `clientui` -- appels vers `mpatient` et `mnotes`)  
**Tests** : Rapport JaCoCo disponible dans `target/`  
**Réseau** : microdiab-network (partagé avec les autres microservices).

**Sécurité** : La gateway applique une authentification centralisée,
mais chaque microservice reste sécurisé indépendamment afin d’éviter
toute exposition accidentelle.
* **Sécurité externe** : Spring Security (authentification centralisée via `mgateway`)
* **Sécurité interne** : Spring Security (Basic Auth pour les communications inter-microservices)

---

## 📊 Monitoring et Documentation
| Outil      | URL                                                                  | Description        |
|------------|----------------------------------------------------------------------|--------------------|
| Actuator   | [http://localhost:9003/actuator](http://localhost:9003/actuator)     | Métriques et santé |
| Swagger UI | [http://localhost:9003/swagger-ui](http://localhost:9003/swagger-ui) | Documentation API  |
| Javadoc    | [http://localhost:9003/apidocs](http://localhost:9003/apidocs)       | Documentation Java |

---

## 🛠 Prérequis
- Java 21
- Spring Boot 3.5.8
- Spring Cloud 2025.0.0
- Maven
- Docker
- Variables d'environnement :
    - `EUREKA_SERVER_HOST=eureka-server`
    - `EUREKA_SERVER_PORT=9102`

---

## 🚀 Endpoints principaux
| Méthode | Endpoint        | Description                                       |
|---------|-----------------|---------------------------------------------------|
| GET     | `/risk/{patId}` | Délivre le rapport de niveau de risque de diabète |

**patId** correspond à l’identifiant unique du patient.

---

## 🧠 Analyse du risque de diabète
### 🎚️ Niveaux de risque possibles
Un patient peut se voir attribuer l’un des quatre niveaux de risque suivants :
* **None** : aucun risque  
* **Borderline** : risque limité  
* **In Danger** : danger  
* **Early onset** : apparition précoce

### 🧪 Termes déclencheurs analysés
Les termes suivants sont recherchés dans les notes médicales :
* Hémoglobine A1C
* Microalbumine
* Taille
* Poids
* Fumeur / Fumeuse
* Anormal
* Cholestérol
* Vertiges
* Rechute
* Réaction
* Anticorps

### 📏 Règles de calcul
* 🟢 **Aucun risque** (None) : 
  * Aucune note médicale ne contient de termes déclencheurs.

* 🟡 **Risque limité** (Borderline) :
  * Entre 2 et 5 termes déclencheurs.
  * Patient âgé de plus de 30 ans

* 🟠 **Danger** (In Danger) : 
  * Moins de 30 ans : Homme ≥ 3 termes // Femme ≥ 4 termes
  * Plus de 30 ans : 6 ou 7 termes

* 🔴 **Apparition précoce** (Early onset) :
  * Moins de 30 ans : Homme ≥ 5 termes // Femme ≥ 7 termes
  * Plus de 30 ans : ≥ 8 termes

---