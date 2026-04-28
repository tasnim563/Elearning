INSERT INTO utilisateur (id, nom, email, mot_de_passe, role)
VALUES
    (1, 'Amina Admin', 'admin@elearning.local', '{noop}admin123', 'ADMIN'),
    (2, 'Moussa Manager', 'manager@elearning.local', '{noop}manager123', 'FORMATEUR'),
    (3, 'Ghada Learner', 'ghada@test.com', '{noop}learner123', 'APPRENANT');

INSERT INTO cours (id, titre, description, categorie, niveau, image_url, actif, date_creation, formateur_id)
VALUES
    (1, 'Java Spring Boot', 'Construire une application web complete avec Spring Boot, Thymeleaf et persistance JPA.', 'Programmation', 'DEBUTANT', '/images/courses/spring-boot.svg', true, CURRENT_TIMESTAMP, 2),
    (2, 'UX Writing pour interfaces', 'Structurer des parcours plus clairs, mieux nommes et plus faciles a comprendre pour les utilisateurs.', 'Design Produit', 'INTERMEDIAIRE', '/images/courses/ux-writing.svg', true, CURRENT_TIMESTAMP, 2),
    (3, 'SQL analytique', 'Lire des jeux de donnees, ecrire des requetes plus solides et transformer les resultats en decisions.', 'Data', 'AVANCE', '/images/courses/sql-analytics.svg', true, CURRENT_TIMESTAMP, 2);

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
    (101, 'Structure MVC',
     'Une interface propre commence par une separation nette des responsabilites.\n\nLe controller recoit la demande, le service contient la regle metier, et le repository parle a la base.\n\nPoints a retenir:\n- un controller ne calcule pas\n- un service ne dessine pas la vue\n- un repository ne decide pas du parcours\n\nQuand cette separation tient, le code devient lisible et les ecrans evoluent plus vite.',
     1, 18, 11),
    (102, 'Templates utiles',
     'Un bon template ne se contente pas d afficher des donnees. Il donne du rythme, des repères, et une architecture visuelle stable.\n\nOn garde un header clair, des cartes avec une hierarchie typographique nette, puis des actions visibles.\n\nChecklist:\n- un titre unique par ecran\n- un sous-texte utile, pas decoratif\n- une action principale bien marquee\n\nLe but n est pas de faire plus de contenu, mais de mieux le presenter.',
     2, 16, 11),
    (103, 'Entites et relations',
     'Les entites doivent refléter le produit. Ici, un cours contient des modules, un module contient des lecons, et une lecon peut porter un quiz.\n\nQuand les relations sont bien posees, le back-office devient simple a construire.\n\nA retenir:\n- `Cours` organise le parcours\n- `Module` regroupe les etapes\n- `Lecon` porte le contenu\n\nCette structure permet ensuite d ajouter un panneau admin sans re-ecrire toute l application.',
     1, 22, 12),
    (104, 'Seed data',
     'Une bonne demo a besoin de contenu credible. Des textes trop courts donnent une fausse impression de produit termine.\n\nPour tester l UX, il faut des titres, des explications, des transitions et des cas d usage concrets.\n\nLe vrai objectif du seed data est simple: permettre de valider les ecrans, la lisibilite et les parcours administratifs.',
     2, 14, 12),

    (201, 'Ton et verbes',
     'Les interfaces efficaces parlent comme des outils, pas comme des slogans.\n\nOn remplace les verbes vagues par des actions precises: commencer, enregistrer, publier, corriger.\n\nExemples:\n- mauvais: Continuer\n- mieux: Ouvrir la lecon suivante\n- meilleur: Marquer comme terminee\n\nCe niveau de precision reduit la charge mentale et augmente le taux de clic utile.',
     1, 12, 21),
    (202, 'Navigation laterale',
     'La sidebar doit expliquer ou l utilisateur se trouve et ce qu il peut faire ensuite.\n\nOn y met trois choses seulement: la navigation, le contexte et les raccourcis importants.\n\nUne bonne sidebar ne crie pas. Elle cadre.\n\nDans ce projet, elle sert aussi de point d entree vers les espaces admin et manager.',
     2, 15, 21),
    (203, 'Empty states',
     'Un empty state ne doit jamais ressembler a une fin de parcours.\n\nIl doit expliquer pourquoi rien ne s affiche et donner l action suivante.\n\nStructure utile:\n- contexte\n- explication courte\n- bouton d action\n\nC est souvent la difference entre une page morte et une page qui remet l utilisateur en mouvement.',
     1, 10, 22),
    (204, 'Messages systeme',
     'Un message systeme utile indique ce qui s est passe et ce qu il faut faire ensuite.\n\nErreur, succes, attente ou sauvegarde partielle: chaque etat doit aider l utilisateur a reprendre la main.\n\nExemple: au lieu de "Erreur", afficher "La lecon n a pas pu etre enregistree. Verifie le titre et recommence."',
     2, 11, 22),

    (301, 'Question metier',
     'Avant d ouvrir l editeur SQL, il faut formuler la question.\n\nExemple: quels cours ont le plus de lecons completes cette semaine ?\n\nUne requete sans question produit souvent un resultat techniquement correct mais inutilisable.\n\nLe SQL analytique commence toujours par le besoin, pas par la syntaxe.',
     1, 14, 31),
    (302, 'Dimensions',
     'Une analyse lisible repose sur des dimensions claires: temps, categorie, niveau, statut.\n\nOn choisit les axes qui racontent une histoire et on evite de tout croiser avec tout.\n\nMoins il y a de bruit, plus la lecture est forte.',
     2, 16, 31),
    (303, 'CTE',
     'Une CTE permet de decouper une requete en etapes lisibles.\n\nAu lieu d un bloc unique difficile a relire, on nomme les sous-resultats et on avance pas a pas.\n\nCela facilite le debug, la revue de code et la maintenance.',
     1, 18, 32),
    (304, 'Aggregations',
     'Grouper et compter semble simple, mais les erreurs arrivent vite.\n\nIl faut verifier la granularite, les filtres et les champs non agreges.\n\nRègle pratique: si le resultat a l air surprenant, relis d abord le niveau de regroupement avant de blamer les donnees.',
     2, 20, 32);

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
