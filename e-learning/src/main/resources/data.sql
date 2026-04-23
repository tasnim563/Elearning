INSERT INTO utilisateur (id, nom, email, mot_de_passe, role)
VALUES (1, 'Ghada', 'ghada@test.com', '1234', 'APPRENANT');

INSERT INTO cours (id, titre, description, categorie, niveau, image_url, actif, date_creation, formateur_id)
VALUES
    (1, 'Java Spring Boot', 'Construire une application web complete avec Spring Boot, Thymeleaf et persistance JPA.', 'Programmation', 'DEBUTANT', '/images/courses/spring-boot.svg', true, CURRENT_TIMESTAMP, 1),
    (2, 'UX Writing pour interfaces', 'Structurer des parcours plus clairs, mieux nommes et plus faciles a comprendre pour les utilisateurs.', 'Design Produit', 'INTERMEDIAIRE', '/images/courses/ux-writing.svg', true, CURRENT_TIMESTAMP, 1),
    (3, 'SQL analytique', 'Lire des jeux de donnees, ecrire des requetes plus solides et transformer les resultats en decisions.', 'Data', 'AVANCE', '/images/courses/sql-analytics.svg', true, CURRENT_TIMESTAMP, 1);

INSERT INTO module (id, titre, description, ordre, cours_id)
VALUES
    (11, 'Demarrage', 'Installer les bases et la structure', 1, 1),
    (12, 'Persistence', 'JPA, relations et seed data', 2, 1),
    (21, 'Fondations', 'Nommer, guider, clarifier', 1, 2),
    (22, 'Etats', 'Empty states et messages utiles', 2, 2),
    (31, 'Questions', 'Formuler une analyse', 1, 3),
    (32, 'Requetes', 'CTE et aggregations', 2, 3);

INSERT INTO lecon (id, titre, contenu, ordre, duree_min, module_id)
VALUES
    (101, 'Structure MVC', 'Une lecon courte et claire sur controller/service/repository et le flux jusqu aux vues.', 1, 18, 11),
    (102, 'Templates utiles', 'Donner une forme lisible aux pages avec un layout commun et une navigation stable.', 2, 16, 11),
    (103, 'Entites et relations', 'Representer cours, modules, lecons et quiz avec des relations cohérentes.', 1, 22, 12),
    (104, 'Seed data', 'Preparer des donnees de demo riches pour tester l UX et les parcours.', 2, 14, 12),

    (201, 'Ton et verbes', 'Un bon bouton porte une action explicite. On retire le texte vague.', 1, 12, 21),
    (202, 'Navigation laterale', 'Construire une sidebar qui donne du contexte et des raccourcis.', 2, 15, 21),
    (203, 'Empty states', 'Expliquer ce qui manque et proposer la suite.', 1, 10, 22),
    (204, 'Messages systeme', 'Erreur, succes, attente: les rendre actionnables.', 2, 11, 22),

    (301, 'Question metier', 'Avant le SQL, on ecrit une question. Sans ca, la requete flotte.', 1, 14, 31),
    (302, 'Dimensions', 'Choisir des axes (temps, categorie, niveau) pour rendre l analyse lisible.', 2, 16, 31),
    (303, 'CTE', 'Decouper une requete en etapes compréhensibles.', 1, 18, 32),
    (304, 'Aggregations', 'Grouper et compter sans casser les resultats.', 2, 20, 32);

INSERT INTO quiz (id, titre, lecon_id)
VALUES
    (1001, 'Quiz: MVC', 101),
    (1002, 'Quiz: Templates', 102),
    (2001, 'Quiz: UX Writing', 201),
    (3001, 'Quiz: SQL', 301);

INSERT INTO question (id, intitule, bonne_reponse, explication, quiz_id)
VALUES
    (5001, 'Ou mettre la logique metier ?', 1, 'La logique va dans un service, pas dans la vue ni le controller.', 1001),
    (5002, 'Pourquoi un layout commun ?', 2, 'Pour stabiliser l experience: navigation + structure coherente.', 1001),
    (5003, 'Quel role pour le controller ?', 0, 'Recevoir la requete et deleguer au service.', 1001),

    (5101, 'Thymeleaf sert a...', 1, 'Rendre des vues HTML cote serveur avec des variables de modele.', 1002),
    (5102, 'Un template doit eviter...', 2, 'Le HTML brut sans styles ni structure.', 1002),

    (5201, 'Un bon bouton primaire doit...', 0, 'Porter l action principale avec un verbe clair.', 2001),
    (5202, 'Un empty state utile...', 2, 'Explique et propose une prochaine action.', 2001),

    (5301, 'Avant d ecrire du SQL, on...', 1, 'Formule la question metier.', 3001),
    (5302, 'Une CTE sert a...', 0, 'Structurer une requete en etapes.', 3001);

INSERT INTO question_choices (question_id, choice)
VALUES
    (5001, 'Dans le controller'),
    (5001, 'Dans un service'),
    (5001, 'Dans le template'),
    (5001, 'Dans l entity'),

    (5002, 'Pour ajouter du CSS'),
    (5002, 'Pour changer la base'),
    (5002, 'Pour une structure stable'),
    (5002, 'Pour supprimer la navigation'),

    (5003, 'Recevoir + deleguer'),
    (5003, 'Stocker les donnees'),
    (5003, 'Dessiner l UI'),
    (5003, 'Executer des migrations'),

    (5101, 'Ecrire du SQL'),
    (5101, 'Rendre des vues HTML'),
    (5101, 'Compiler du Java'),
    (5101, 'Ajouter des routes'),

    (5102, 'Les titres'),
    (5102, 'Les listes'),
    (5102, 'Le rendu brut sans structure'),
    (5102, 'Les boutons'),

    (5201, 'Porter l action principale'),
    (5201, 'Etre un paragraphe'),
    (5201, 'Dupliquer la sidebar'),
    (5201, 'Remplacer le contenu'),

    (5202, 'Blame l utilisateur'),
    (5202, 'Ne dit rien'),
    (5202, 'Explique et guide'),
    (5202, 'Cache le bouton'),

    (5301, 'Choisit une couleur'),
    (5301, 'Formule la question metier'),
    (5301, 'Supprime les tables'),
    (5301, 'Cache les resultats'),

    (5302, 'Structurer en etapes'),
    (5302, 'Crypter la base'),
    (5302, 'Ajouter du CSS'),
    (5302, 'Remplacer une table');
