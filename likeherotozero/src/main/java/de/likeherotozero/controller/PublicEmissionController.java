/*Aufgabe:
•	Öffentliche Suche bzw. Auswahl eines Landes.
•	Anzeige des aktuellsten CO2-Wertes des gewählten Landes.
Mögliche Endpunkte:
•	GET /emissions/search – Formular mit Länderauswahl.
•	POST /emissions/search – Formular absenden.
•	GET /emissions/latest/{countryId} – Ergebnisansicht.
Wichtige Methoden:
•	showSearchForm() – lädt die Liste aller Länder.
•	submitSearch() – verarbeitet die Auswahl und leitet zur Ergebnisansicht weiter.
•	showLatestEmission(Long countryId) – ruft den aktuellen Datensatz über den Service ab.
Abhängigkeiten:
•	Nutzt CountryService und Co2EmissionService.
*/