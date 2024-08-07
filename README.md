# PicReport

# Application Mobile en Java

## Aperçu

Cette application mobile permet à deux types d'utilisateurs : **Admin** et **Utilisateur**. Les utilisateurs peuvent uploader leurs images sur Firestore et générer un rapport sous forme de document PDF contenant les informations suivantes :

- Nom et description des images téléchargées
- ID utilisateur
- Photos téléchargées

Les administrateurs peuvent voir une liste des utilisateurs et, en cliquant sur un utilisateur, consulter leurs rapports.

## Fonctionnalités

### Utilisateur
- Uploader des images sur Firestore
- Générer des rapports PDF contenant :
    - Nom et description des images
    - ID utilisateur
    - Photos téléchargées

### Administrateur
- Voir la liste des utilisateurs
- Voir et consulter les rapports générés par les utilisateurs

## Technologies Utilisées
- Java
- Firebase Firestore
- Firebase Storage
- iText (pour la génération de PDF)

## Installation

### Prérequis
- Android Studio
- Compte Firebase

### Étapes
1. **Cloner le dépôt**
   ```sh
   git clone https://github.com/merouane-bn/PicReport.git
   ```

2. **Ouvrir le projet dans Android Studio**
    - Lancer Android Studio et sélectionner "Ouvrir un projet existant".
    - Naviguer jusqu'au dépôt cloné et l'ouvrir.

3. **Configurer Firebase**
    - Aller sur la Console Firebase et créer un nouveau projet.
    - Ajouter une application Android à votre projet Firebase.
    - Suivre les instructions pour télécharger le fichier `google-services.json`.
    - Placer le fichier `google-services.json` dans le répertoire `app` de votre projet Android.

4. **Ajouter les dépendances**
    - S'assurer que les dépendances suivantes sont ajoutées dans votre fichier `build.gradle` :
      ```groovy
      dependencies {
          implementation(libs.appcompat)
          implementation(libs.material)
          implementation(libs.activity)
          implementation(libs.constraintlayout)
          implementation(libs.firebase.auth)
          implementation(libs.firebase.storage)
          implementation(libs.firebase.firestore)
          implementation(libs.firebase.database)
          testImplementation(libs.junit)
          androidTestImplementation(libs.ext.junit)
          androidTestImplementation(libs.espresso.core)
          implementation("com.itextpdf:itext7-core:7.1.15")
      }
      ```

5. **Lancer l'application**
    - Connecter votre appareil Android ou démarrer un émulateur Android.
    - Cliquer sur le bouton "Run" dans Android Studio pour construire et exécuter l'application.

## Utilisation

### Utilisateur
1. **Connexion/Inscription**
    - Les utilisateurs doivent se connecter ou s'inscrire pour utiliser l'application.
2. **Uploader des images**
    - Cliquer sur le bouton "Upload" pour sélectionner et uploader des images sur Firestore.
3. **Générer un rapport**
    - Après avoir uploadé des images, cliquer sur le bouton "Générer un rapport".
    - L'application créera un rapport PDF contenant les images, leurs noms et descriptions, ainsi que l'ID utilisateur.

### Administrateur
1. **Connexion**
    - Les administrateurs doivent se connecter avec leurs identifiants.
2. **Voir les utilisateurs**
    - Les administrateurs peuvent voir une liste de tous les utilisateurs.
3. **Consulter les rapports des utilisateurs**
    - Cliquer sur un utilisateur pour voir ses rapports et les consulter.


## Contribuer

Les contributions sont les bienvenues ! Veuillez forker le dépôt et créer une pull request avec vos modifications.

