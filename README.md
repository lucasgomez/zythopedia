# Zythopedia, le README
Effort minimum de doc, parce que j'oublie d'année en année...

## Général

### Application
L'appli est en java/spring boot pour le backend et a besoin d'une DB (nornalement PostgreSQL) pour les datas. Le front est en Angular. Elle est sensée pouvoir être buildée avec MVN sans autre difficulté. Si c'est pas le cas, essaye de supprimer target et refaire un clean install. Sinon prie, ca aide pas, mais ca fait passer le temps.

#### Troubleshoot
Sur une nouvelle install de Intellij, il faut activer le *Annotation processing* pour permettre à l'IDE de gérer les classes lombokisées ainsi que MapStruct. Au cas ou l'application build, mais échoue au démarrage Spring car il ne trouve pas un bean lié à MapStruct, il faut faire un *mvn clean install* pour le forcer à tout recréer. Pourquoi? Je sais pas.

### Database
L'application tourne normalement sur une DB Postgres. J'ai pas essayé le reste, ptête que ca marche, je vois pas de raison que ca ne soit pas ok.

#### Création
Pour créer la nouvelle DB, une fois postgres installé :
```
sudo su - postgres
createuser zythopedia
createdb zythopediadb
psql
```
A partir de là, on est dans le postgres shell :
```
alter user zythopedia with encrypted password 'zythopedia';
grant all privileges on database zythopediadb to zythopedia;
```
Ce qui doit normalement correspondre à la configuration suivante dans application.properties :
```
spring.datasource.url=jdbc:postgresql://localhost:5432/zythopediadb
spring.datasource.username=zythopedia
spring.datasource.password=zythopediapwd
```

*Note :* j'ai un souci avec le user 'zythopedia', il n'a pas accès au schéma 'public' et le GRANT ne semble pas changer ca, pour le moment c'est donc le user 'postgres' qui est utilisé

#### Restore
Pour créer les structures à partir de 0, il suffit de démarrer l'application et liquibase se charge de créer les structures.
Pour restaurer un backup, il faut utiliser *pg_restore* en utilisant l'utilisateur *postgres*:
```
sudo -u postgres pg_restore -d zythopediadb --clean /home/shared/zytho.backup
```
Le *sudo -u postgres* pour le faire avec le user *postgres* et l'option *--clean* pour faire des DROP avant le réimport.

#### Backup pour pg_restore
```
sudo -u postgres pg_dump -d zythopediadb -F tar -c -f /home/shared/zytho_21-05-25.dump
```

#### Backup à destination d'un import plain text
```
sudo -u postgres pg_dump -d zythopediadb -F plain -c -f /home/shared/zytho_21-05-25.dump
```

#### Restore d'un plain backup
D'abord vider la DB si ne contiens pas de DROP dans le scipt. Puis :
```sudo -u postgres psql zythopediadb < /home/shared/zytho_25-05-25.dump``

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

## Queries utiles 

### Query de base par brasserie
```sql
SELECT p.name, d."name", s.name, d.abv, bd.service_method, bd.buying_price
from producer p
inner join drink d on d.producer_fk = p.id
inner join bought_drink bd on bd.drink_fk = d.id
inner join "style" s on s.id = d.style_fk  
left outer join edition e on e.id = bd.edition_fk 
where LOWER(p.name) = 'galio';
```

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
```

## Release
mvn release:prepare
mvn release:perform
npm run build:prod

### Problèmes
#### Le front n'arrive pas à contacter le back
Il faut s'assurer que le script de redirect est présent et bien configuré. On peut le trouver à la racine de "sites/soif.fetedelabiere.ch" dans un répertoire "redirect". Dedans se trouve l'adresse IP du serveur backend. Ce répertoire n'est pas pris dans le npm build

#### On ne peut pas accéder à autre chose que la page "root", les autres font un Objet non trouvé
Il faut copier le fichier .httaccess depuis une ancienne version pour que ca marche.
