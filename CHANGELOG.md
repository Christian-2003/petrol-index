<img src="docs/img/icon.png" height="150" align="right">

# Changelog

## 1.0.3 (XXXX-XX-XX)
###### Release Highlights
Major refactoring of the codebase to increase maintainability.

###### Features
* Add custom app icon for debugging releases to better distinguish them from release builds.
* Change app icon to include a small shadow.
* Major refactoring of the entire codebase to include domain driven design and clean architecture.
* Petrol entries are now represented through the domain model `Consumption`.
* Add application layer use cases to facilitate CRUD actions.
* Move entire view to presentation package within the plugin layer.
* Visual changes to the entire view.
* Add help cards that can be dismissed.
* Database entities are now completely decoupled from the domain model and backup DTOs.
* Switch from Gson to Kotlin-Serialization library to facilitate JSON backups.
* Changes to JSON that is used for backups.
* Add unit tests for domain layer.
* Add unit tests for application layer.
* Add unit tests for plugin layer.
* Add instrumented tests for database migration.

<br/>

## 1.0.2 (2025-03-06)
###### Release Highlights
Bugfixes and visual enhancements to enhance usability.

<br/>

###### Features
* Added custom animated splash screen.
* Fixed bug which linked to incorrect GitHub repository.
* Fixed typo in German app language.
* Trailing icon indicating an error in the text inputs within `AddPetrolEntryView` no longer occupies space when no error occurs.
* Change appearance of settings to match the new style of Password Vault.
* Change colors of cards (in `MainView` and `AddPetrolEntryView`) to be less prominent.
* Change style of data display on `MainView` to indicate whether it is clickable or not.
* Dispay delete button with red color to better indicate this action.

<br/>

## 1.0.1 (2025-01-18)
###### Release Highlights
Emergency update to fix incorrect update detection.

###### Features
* Bugfix makes app detect updates correctly.

<br/>

## 1.0.0 (2025-01-18)
###### Release Highlights
The app can store entries documenting the petrol consumption within a local database. The entries can be exported and imported to / from JSON files. Some data of the petrol consumption is analyzes using diagrams and charts.

###### Features
* Created Git repository.
* Created database to store petrol entries.
* Petrol entries can be added.
* Petrol entries can be deleted.
* Petrol entries can be edited.
* Price per litre, cumulcated volume and cumulated price is calculated and displayed using charts.
* List of petrol entries can be exported to JSON file.
* Petrol entries can be restored from JSON file.
* App detects updates on GitHub and informs the user to download a new release.
