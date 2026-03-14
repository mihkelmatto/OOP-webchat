# OOP rühmatöö plaan

## Liikmed

- Oskar Austa (github: oskarasd123)
- Mihkel Matto (github: mihkelmatto)
- Raimond Olle (github: RaimondOlle)
- Mattias Volt (github: moosipea)

## Funktsioonide prioriteedid:

1. Reaalajas sõnumivahetus (localhost)
Localhost >> üle võrgu: https://websocket.org/guides/languages/java/
- inimeselt inimesele (serveri kaudu)

2. Vestluste salvestamine
https://github.com/xerial/sqlite-jdbc
Sõnumite editimine?

3. Lisakasutajad, paroolid, krüpteerimine

4. Grupid, privileegid
5. UI
6. Sõnumite formatting



## Märkmed

- IRC stiilis juturuum
- una meid on neli tükki, siis nii klient kui server javas
- _ei ühildu ametliku IRC protokolliga_ (sest seda oleks ilgelt tüütu teha)
- Websocket. Protokollid selle peal.
- autentimine, session tokenid, (loodetavasti) turvaline suhtlus. 
- korrektne krüpto on hästi keeruline, selle projekti raames me sajaprotsendilise turvalisuse poole ei püüdle. siiski sooviks vältida lihtsamaid asju, näiteks salasõnade hoiustamist tekstikujul.
- privaatsed sõnumid konkreetsete kasutajate vahel
- erinevad kanalid/grupi chatid
- käsud (näiteks /dm, /motd, jne)
- javafx gui vms. ei pea üleliia keeruline olema.
- kliendi poolel markdown renderdamine. Ei ole vaja markdowni täisfunktsionaalsust. näiteks pilte ilmselt ei hakka panema (kuna tegemist on ennekõike tekstisuhtlusega). samuti ei toeta htmli ja selliseid asju. Põhimõtteliselt lihtsalt tavaline formattimine nagu bold, kaldkiri, lingid, muu selline.
- klient peaks tuvastama, kui kasutajat on pingitud @kasutajanimi süntaksiga
- kasutajatel rollid, privileegid
- klient peaks ühendades saama kõik vahepeal saadetud sõnumid, st mitte ainult reaalajasuhtlus. niisiis serveri poole peal andmebaas
- igal kasutajal on nime järgi genereeritud värv
- võimalik on näha kasutaja online-staatust.
