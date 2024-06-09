# HolztransportVerwaltung

## Beschreibung:
Die HolztransportVerwaltung verwaltet die Aufträge, den Holzlagerbestand, die Maschinen, die Kunden, die Angestellten, den Urlaub von Angestellten und die Finanzen vom Unternehmen **Schennet Holz**. Man kann einen Auftrag für einen Kunden anlegen.

Das Postgre Datenbanksystem wird verwendet. 

## Tabellen:
* Maschinen - Die Maschinen vom Unternehmen werden in dieser Tabelle abgebildet
* Kunden - Die Kunden vom Unternehmen werden abgebildet
* Angestellte - Die Angestellten vom Unternehmen werden abgebildet
* Aufträge - Kundenauftrage werden mit benötigtem Holz, Personal und Auftragart abgebildet
* Holzagerbestand - Holzlagerbestand wird in dieser Tabelle abgebildet
* Konten - Gewinn, Erlös und Bilanz vom Unternehmen werden dort abgebildet

## Ziele:
* Die Auftrag Tabelle beinhaltet Kunden, benötigte Maschinen, Dauer, Angestellte und die Auftragskosten. Nach dem der Auftrag ausgeführt wurde (wenn das Datum vom Auftrag vorbei ist) werden die Kosten und Gewinne in die Finanzmanagement-Tabelle übernommen. (nicht erfüllt)
* Kunden, Maschinen, Holzlagerbestand und Angstellte beinhalten einfach gesagt die Daten (erfüllt)
* Holzlagerbestand beinhaltet die Menge an Holz die gerade im Lager sind. Wenn man Holz bestellt, werden die Kosten davon in die Finanzmanagement-Tabelle hineingeschrieben (teilweise erfüllt)
* Finanzmanagement Tabelle hat ID, Beschreibung, Betrag, Kategorie als Felder und man kann mit Funktionen/storedProcedures den Kontostand/Bilanz ausgeben. (nicht erfüllt)

## Funktionalitätsbeschreibung von Java-Programm
1. Kunde kann Auftrag ansehen
2. Import von JSON-Files wird am Programmstart ausgeführt, wenn die Tabellen nicht gefüllt sind
3. Export von JSON-Files wird am Ende vom Programm abgefragt, wenn Benutzer es will wird JSON-File abgespeichert
