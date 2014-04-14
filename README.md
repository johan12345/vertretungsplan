vertretungsplan
==================

Änderungen an der App ls-vertretungsplan zur Anpassung auf mehrere Schulen.

*Original-Beschreibung:*

Dies ist eine Android-App für den Vertretungsplan der Lornsenschule Schleswig. Weitere Informationen auf der [Internetseite](http://johan12345.github.io/ls-vertretungsplan/)

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

Bei den letzten drei Projekten ist darauf zu achten, dass sie alle standardmäßig "library" heißen und darum nur eines zur Zeit in Eclipse importiert werden kann und danach umbenannt werden muss. Alternativ kann man die Projekte auch vorher in der ".project"-Datei umbenennen.

Achte darauf, dass in Eclipse (zumindest beim Projekt "Vertretungsplan") in den Eigenschaften das Encoding auf `UTF-8` (und nicht etwa `Cp1252`) gestellt ist.

Füge die beiden .jar-Dateien aus dem Ordner "libs" der HoloEverywhere Library zum Build Path dieser Bibliothek hinzu (Rechtsklick -> Build Path -> Add to Build Path).

Kopiere die Datei "support-v4-19.1.0.jar" aus dem Ordner "libs" der HoloEverywhere Library in den gleichen Ordner der Bibliotheken Crouton und PagerSlidingTabStrip (dort die android-support-v4.jar entfernen) und füge sie jeweils zum Build Path hinzu.

Wenn bei Crouton noch Fehler angezeigt werden, setze in der project.properties-Datei `target` auf `android-19`.
