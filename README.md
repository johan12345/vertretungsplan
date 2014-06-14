vertretungsplan
==================

Dies ist eine Android-App für Vertretungspläne von Schulen. Sie basiert auf meiner [App für die Lornsenschule Schleswig](https://github.com/johan12345/ls-vertretungsplan), ist aber leicht auf andere Schulen erweiterbar. Weitere Informationen dazu im [Wiki](https://github.com/johan12345/vertretungsplan/wiki/Schulen-hinzuf%C3%BCgen).

**English:** This is an Android app for substitution schedules of schools. It is based on the [app](https://github.com/johan12345/ls-vertretungsplan) I first created for Lornsenschule, Schleswig, Germany. Currently, it only supports some schools in Germany, but feel free to fork the code and add other schools. You can find information about this in the [Wiki](https://github.com/johan12345/vertretungsplan/wiki/Schulen-hinzuf%C3%BCgen) (in German). If you need any help or an English translation of the Wiki, please contact me (johan.forstner@gmail.com).

Anleitung zum Importieren in Eclipse
------------------------------------

Klone das git-Repository mit allen Submodules. 
Importiere dann in Eclipse (ADT) folgende Projekte:

* Vertretungsplan
* HoloEverywhere Library
* HoloEverywhere Addon Preferences
* Crouton Library
* Inscription Library
* PagerSlidingTabStrip Library
* Google Play Services

Bei den letzten drei Projekten ist darauf zu achten, dass sie alle standardmäßig "library" heißen und darum nur eines zur Zeit in Eclipse importiert werden kann und danach umbenannt werden muss. Alternativ kann man die Projekte auch vorher in der ".project"-Datei umbenennen.

Achte darauf, dass in Eclipse (zumindest beim Projekt "Vertretungsplan") in den Eigenschaften das Encoding auf `UTF-8` (und nicht etwa `Cp1252`) gestellt ist.

Füge die beiden .jar-Dateien aus dem Ordner "libs" der HoloEverywhere Library zum Build Path dieser Bibliothek hinzu (Rechtsklick -> Build Path -> Add to Build Path).

Kopiere die Datei "support-v4-19.1.0.jar" aus dem Ordner "libs" der HoloEverywhere Library in den gleichen Ordner der Bibliotheken Crouton und PagerSlidingTabStrip (dort die android-support-v4.jar entfernen) und füge sie jeweils zum Build Path hinzu.

Wenn bei Crouton noch Fehler angezeigt werden, setze in der project.properties-Datei `target` auf `android-19`.
