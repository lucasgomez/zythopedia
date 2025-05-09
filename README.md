# Zythopedia, le README
Effort minimum de doc, parce que j'oublie d'année en année...

## Général
L'appli est en java/spring boot pour le backend et a besoin d'une DB (nornalement PostgreSQL) pour les datas. Le front est en Angular. Elle est sensée pouvoir être buildée avec MVN sans autre difficulté. Si c'est pas le cas, essaye de supprimer target et refaire un clean install. Sinon prie, ca aide pas, mais ca fait passer le temps. 

## Nouvelle édition
Une fois que l'appli boot, il est possible de faire une nouvelle édition.

### Générer la nouvelle édition
Aller dans 'application.properties' et modifier le paramètre 'edition.current.name'. Désormais, plus aucune boisson n'apparait, car pour le moment il n'en existe aucune pour la nouvelle édition.

### Générer le fichier de commande vide
GET sur http://localhost:8080/api/export/order pour obtenir le fichier vide pour saisir la nouvelle commande. Il contient plusieurs onglets :
 - Le 1er sera l'onglet à remplir. Il contient 2 entrées d'exemples qui peuvent être supprimées.
 - Le 2ème (Current edition order) est celui qui contient les boissons pour l'édition courante. Il est vide si aucune boisson n'a encore été saisie pour cette édition.
 - Le 3ème contient les boissons des éditions précédentes (pour celles qui ont été re-commandées)
 - Les suivants permettent de retrouver les IDs des styles, couleurs, produceurs et pays déjà dans la DB

### Replir le fichier de commande
Pour le remplissage du 1er onglet, s'il s'agit d'une bière jamais référencée, elle sera créée à partir des informations présentes (nom, nom brasserie, nom style, taux d'alcool, etc). Pour celles déjà existantes (identifiées par le 'code amstein' ou par la combinaison bière/brasserie), seul les valeurs liées au BoughtDrink sont récupérées, soit le prix d'achat, volume (bouteilles seulement) et le type de service.

Pour une personne qui ne serait pas moi qui doit faire se travail, faut copier/coller le contenu du fichier de commande Amstein dans une app de texte simple, utiliser la sélection verticale pour nettoyer le contenu et garder le code Amstein, le nom de la bière, son prix au L pour les TAP, le volume et le prix unitaire pour les BOTTLE. De la à mettre dans les bonnes colonnes du fichier d'import précédement généré.

### Import du fichier de commande
Pour terminer, il faut injecter ce fichier. Pour ce faire, requête POST sur http://localhost:8080/api/import/order avec en paramètre file le fichiers XLS. De plus, il faut ajouter les credentials (username/password) pour la simple auth. Tout devrait bien se passer, rien de compliqué. Sinon t'es dans le merde.

### Exporter fichiers data et price calculator
Générer les fichiers pour partager le travail avec des GET sur :
 - Calculateur de prix : http://localhost:8080/api/export/calculator
 - Edition des boissons : http://localhost:8080/api/edition/2025/export/data

### Query de ouf pour localiser les drink créés en doublon à l'import :
```sql
SELECT bd.id, bd.code, d.name, d.id AS drinkId,
		'SELECT ''' || REPLACE(d.name, '''', '''''') || ''' as orig, ' ||
		'      d.id, d.name, d.description, d.abv, ' ||
		'      ''UPDATE bought_drink set drink_fk = '' || d.id || '' WHERE drink_fk =' || d.id || ''' AS updQry ' ||
		'FROM drink d ' ||
		'WHERE d.id <> ' || d.id ||
		' AND (POSITION(''' || REPLACE(UPPER(REPLACE(d.name, '''', '''''')), ' ', ''' IN UPPER(d.name))>0 OR POSITION(''') || ''' IN UPPER(d.name))>0); '
FROM   bought_drink bd
INNER JOIN drink d ON bd.drink_fk = d.id
WHERE  bd.edition_fk = 2794
AND    d.description IS NULL;