<img src="docs/img/icon.png" height="150" align="right">

# Changelog

## 1.1.1 (XXXX-XX-XX)
###### Release Highlights
n./a.

###### Features
* Downgrade `minSdk` from 35 to 34 in order to support devices running Android 14.
* Add focus requester to `ConsumptionScreen` so that the user is able to switch between inputs using IME keyboards.

<br/>

## 1.1.0 (2025-09-29)
###### Release Highlights
Major refactoring of the codebase to increase maintainability as well as changes to the user interface.

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
* List of used software no longer shows license name and software version to increase maintainability.
* License text is now displayed in a fully-expanded modal bottom sheet instead of a partially expanded bottom sheet.
* Change the scrim color of the navigation bar to match the app surface color.
* List of consumptions collapses top app bar to increase space for scrolling.
* List of consumptions displays below navigation bar to increase space for scrolling.
* Add onboarding to the app to help the user get started with the app.
* Switch all app icons (except the info- and help-icons on cards) from filled to outlined versions. This puts less emphasis on these icons.
* Values (e.g. volume and price) are now entered according to user locale. For German, the values are entered using a comma instead of a dot to separate decimals (e.g. "1.234,56 €" instead of "1234.56 €").
* Date pickers show date in medium format (e.g. "27.09.2025") instead of default format (e.g. "27. Sept. 2025").

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
