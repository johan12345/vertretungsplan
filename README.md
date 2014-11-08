vertretungsplan
==================

Dies ist eine Android-App für Vertretungspläne von Schulen. Sie basiert auf meiner [App für die Lornsenschule Schleswig](https://github.com/johan12345/ls-vertretungsplan), ist aber leicht auf andere Schulen erweiterbar. Weitere Informationen dazu im (leicht veralteten) [Wiki](https://github.com/johan12345/vertretungsplan/wiki/Schulen-hinzuf%C3%BCgen). Der Code des dazugehörigen [Servers](https://github.com/johan12345/vertretungsplan-server) und die [Schul-Konfigurationsdateien](https://github.com/johan12345/vertretungsplan-config-files) sind auch Open Source.

**English:** This is an Android app for substitution schedules of schools. It is based on the [app](https://github.com/johan12345/ls-vertretungsplan) I first created for Lornsenschule, Schleswig, Germany. Currently, it only supports some schools in Germany, but feel free to fork the [server code](https://github.com/johan12345/vertretungsplan-server) and add other schools.

Anleitung zum Importieren in Eclipse
------------------------------------

Klone das git-Repository mit allen Submodules. 
Importiere dann in Eclipse (ADT) folgende Projekte:

* Vertretungsplan
* android-support-v7-appcompat (aus dem Android 5.0 SDK)
* support-preferencefragment
* Google Play Services
* Crouton Library
* PagerSlidingTabStrip Library

Bei den letzten beiden Projekten ist darauf zu achten, dass sie alle standardmäßig "library" heißen und darum nur eines zur Zeit in Eclipse importiert werden kann und danach umbenannt werden muss. Alternativ kann man die Projekte auch vorher in der ".project"-Datei umbenennen.

Achte darauf, dass in Eclipse (zumindest beim Projekt "Vertretungsplan") in den Eigenschaften das Encoding auf `UTF-8` (und nicht etwa `Cp1252`) gestellt ist.

Kopiere die Datei "android-support-v4.jar" aus dem Ordner "libs" vom Hauptprojekt in den gleichen Ordner der Bibliotheken Crouton, support-preferencefragment und PagerSlidingTabStrip (dort die android-support-v4.jar entfernen) und füge sie jeweils zum Build Path hinzu.

Wenn bei Crouton noch Fehler angezeigt werden, setze in der project.properties-Datei `target` auf `android-19`.
