# Car Rental Agency Management

Projet semestriel du module Advanced Web Development (JEE), UIR, 4e année, 2026–2027.
Groupe : Hiba, Salma, Fatima Zahra.

Application web de location de voitures : flotte, réservations, cycle de location (pick-up, prolongation, retour, inspection), facturation, trois rôles (administrateur, agent, client) et un composant de Machine Learning intégré à la couche métier.

## Pile technique

JDK 17 · Maven (WAR) · Apache Tomcat 9 · `javax.*` · Struts 2 (6.x) · Hibernate 5.6 · MySQL / MariaDB (XAMPP) · BCrypt · Weka

## Architecture

```text
Action Struts (web) → IXxxMetier (metier) → IXxxDao (dao) → Hibernate → MySQL
```

| Package | Contenu |
|---|---|
| `ma.uir.awd.model` | entités Hibernate et énumérations |
| `ma.uir.awd.dao` | interfaces `IXxxDao` et implémentations `XxxDaoImpl` |
| `ma.uir.awd.metier` | interfaces `IXxxMetier` et implémentations `XxxMetierImpl` |
| `ma.uir.awd.metier.ml` | le composant ML (prédiction de prolongation) |
| `ma.uir.awd.web` | `BaseAction` et les Actions Struts |
| `ma.uir.awd.util` | `HibernateUtil` |

Les vues sont dans `src/main/webapp/WEB-INF/views/` (sans scriptlet), les messages dans `src/main/resources/ma/uir/awd/web/package.properties`.

## Installation

1. Démarrer MySQL dans XAMPP.
2. Importer, dans l'ordre : `database/schema.sql`, `database/seed.sql`, puis `database/demo_accounts.sql` (à venir).
3. Vérifier avec `database/validation.sql` : chaque requête de détection doit renvoyer 0 ligne.
4. `mvn clean package`, puis déployer `target/car-rental.war` sur Tomcat 9.

## Comptes de démo

À venir avec `database/demo_accounts.sql` (un administrateur, un agent, un client).

## Règles de travail

- `main` doit toujours compiler et se déployer.
- Une branche par fonctionnalité : `hiba/...`, `salma/...`, `fatima/...`.
- Chaque branche est fusionnée par une pull request, relue par une autre membre (Hiba relit Salma, Salma relit Fatima Zahra, Fatima Zahra relit Hiba).
- `struts.xml`, `hibernate.cfg.xml`, `package.properties` et `pom.xml` : un bloc commenté par domaine, des modifications petites et annoncées au groupe.
- Les signatures des interfaces DAO et métier ne changent qu'après accord des trois.
- Ne jamais pousser `target/`, `.idea/` ni de mot de passe.
