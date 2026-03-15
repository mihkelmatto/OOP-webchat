# OOP rühmatöö plaan

Online chat rakendus (server + klient).

## Liikmed
Oskar Austa (github: oskarasd123) <br>
Mihkel Matto (github: mihkelmatto) <br>
Raimond Olle (github: RaimondOlle <br>
Mattias Volt (github: moosipea) <br>



## Põhifunktsionaalsused (peab kindlasti olema):
-   reaalajas sõnumivahetus
	-   kasutajalt kasutajale
	-   kasutajalt kanalisse
-   sõnumite salvestamine
	-   serveris andmebaas   
	-   kliendis andmebaas
	-   ühendades küsib klient serverilt sõnumeid, mis vahepeal saadetud on



## Kõrvalfunktsionaalsused (võiks olla ka):

**lihtne graafiline UI kliendile**

-   kasutajate autentimine, salasõnaga ühendamine
-   sõnumite otsimine, filtreerimine
-   sõnumite editimine ja kustutamine (editimisel vastav märge)
-   grupichatid
	-   kasutajate privileegid
	-   Kliendi ja serveri käsud (nagu /dm, /motd vms)
    
-   visuaalsed elemendid
	-   sõnumite formatting ja renderdamine (Markdown või selle laadne)
	-   kasutajate pingimine @kasutajanimi süntaksiga 
	-   kasutajatele on määratud värv (kasutajanime järgi)
	-   võimalik on näha teiste kasutajate online-staatust
    

**Turvalisus**

-   ideaalis tahaks, et ühendus oleks võimalikult turvaline
-   suhtlusprotokoll võiks olla selline, et sama hästi on kasutatav GUI klient ja command-line klient.
	-   projekti raames teeme ennekõike GUI kliendi
	-   kui aega jääb, võib ka command-line kliendi teha
