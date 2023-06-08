# Virusland
![](https://www.rae.es/sites/default/files/styles/noticia_grande/public/2020-05/virus-1812092_1920.jpg?h=036f3151&itok=FKyri05t)
## Sobre el projecte
Virusland consisteix en un *software* que simula la propagació d'un o més virus en un territori conformat per diverses regions, tenint en compte les relacions de veïnatge, confinaments, vacunacions, mutacions per error i coincidència, mobilitats internes i externes, contagiositat, etc.
> Per Aniol Juanola, Jordi Badia i Guillem Vidal, alumnes d'Enginyeria Informàtica de la UdG

## Instruccions d'execució
### Prerequisits
- Java 17
- Linux requereix el paquet **openjfx** i dependències d'aquest
### Execució
- Windows &rarr; `.\run.bat`
- Linux &rarr; `./run.bash`

## Utilització
### Fitxers d'entrada
El primer que fa el programa és demanar la ruta del directori on es troben els fitxers que determinen les regions, virus, estat inicial d'aquests i vacunes aplicables que han de portar per nom:
- `regions.txt`
- `virus.txt`
- `vacunes.txt`
- `estatInicial.txt`

Es pot trobar un exemple del format de cadascun d'aquests a qualsevol dels jocs de proves del directori [test](test) d'aquest repositori.

### Finestres
El programa treballa amb dues finestres síncrones:
- **Consola**: des d'on l'usuari pot interactuar amb el programa i visualitzar les dades numèriques generades per la simulació.

- **Interfície gràfica**: des d'on l'usuari pot veure un resum visual de la situació de la simulació:
	- **Graf**: mapa del territori format per
		-  **Regions**: verd &rarr; sense confinament; vermell &rarr; amb confinament.
		- **Relacions de veïnatge**: blau &rarr; sense tancament, vermell &rarr; amb tancament.
	- **Gràfic de formatges central**: mostra les proporcions de contagiats, no contagiats, immunes i morts respecte a les afectacions resultants d'aplicar els filtres determinats per l'usuari.
	- **Gràfic de formatges esquerre**: mostra les proporcions de gent incubant, malalta i no malalta respecte a les afectacions resultants d'aplicar els filtres.
	- **Gràfic de formatges dret**: mostra les proporcions de gent en latència, contagiosa i no contagiosa respecte a les afectacions resultants d'aplicar els filtres.

### Menú principal
1. **Establir filtres** &rarr; obre el menú que permet opcions de filtratge.
2. **Generar tick següent** &rarr; genera el tick que segueix a l'actual i en mostra les dades.
3. **Generar més d'un tick** &rarr; l'usuari pot escollir quants ticks vol que es generin de cop.
4. **Aplicar vacuna** &rarr; l'usuari pot aplicar una vacuna a una proporció de població d'una regió.
5. **Actualitzar un confinament** &rarr; obre el menú que permet tractar amb confinaments i tancaments.
6. **Veure log/resum** &rarr; alterna entre mostrar les dades **conjuntades** més **rellevants** de l'execució que compleixin amb els filtres establerts (resum) o mostrar **totes** les dades **separades** de l'execució que compleixin amb els filtres establerts (log).
7. **Consultar llistes** &rarr; obre el menú que permet visualitzar les llistes d'elements presents en la simulació.
8. **Sortir** &rarr; tanca el programa.
> Nota: en qualsevol moment de la execució es pot tornar al menú anterior mitjançant la opció 0.

### Establir filtres
Les opcions 1 i 2 permeten seleccionar un virus i una regió com a filtres respectivament. En cas d'haver-n'hi un, el treu. L'opció 3 esborra els filtres (n'hi hagi o no).

### Actualitzar un confinament
L'opció 1 permet aplicar un confinament amb el conseqüent tancament de les fronteres, i la 2 permet treure'l.
L'opció 3 permet tancar una frontera entre dues regions, i la 4 permet obrir-la.

### Consultar llistes
1. Llistar regions
2. Llistar virus
3. Llistar famílies
4. Llistar vacunes

## Carpetes del repositori
- [doc](doc): documentació del projecte.
- [out/artifacts](out/artifacts): artefactes necessaris per a l'execució del programa.
- [src](src): codi font
- [test](test): joc de proves.

**TODO: suprimir .idea, inputFiles i demos**

## Agraïments
- [SmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph): utilitzat en el desenvolupament del graf de regions de la interfície gràfica.
- [PieCharts amb JavaFX](https://docs.oracle.com/javafx/2/charts/pie-chart.htm): utilitzat en el desenvolupament dels gràfics de formatges de la interfície gràfica.
