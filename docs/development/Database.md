<img src="../img/icon.png" height="150" align="right">

# Database
This document describes the database architecture for the app Petrol Index.


###### Table of Contents
1. [Database Schema](#database-schema)
2. [Database Migration](#database-migration)
3. [Additional Information](#additional-information)

<br/>

## Database Schema
The database schema can be described by the following UML diagram:

![](../img/development/database_scheme.drawio.svg)

###### `petrol_entries` table
THis table stores all consumptions. The name "petrolEntries" is kept to preserve backwards-compatibility to the previous naming conventions, in which a petrol consumption was called "Petrol entry".

&nbsp; | Data Type | Attribute | Remarks
--- | --- | --- | ---
Primary Key | `BLOB NOT NULL` | id | UUID for the consumption. This is the same ID used as domain entity ID.
&nbsp; | `INTEGER NOT NULL` | epochSecond | Date on which the petrol consumption was performed. This stores the number of **days** passed since the epoch day.
&nbsp; | `INTEGER NOT NULL` | volume | Stores the volume of the petrol consumption in 1/100-liters. This means that each increment in this value represents exactly one centiliter.
&nbsp; | `TEXT NOT NULL` | description | Optional description for the consumption. This contains anything that the user might want to remember as additional info about the consumtpion.
&nbsp; | `INTEGER NOT NULL` | totalPrice | Total price (in cents) that was paid for the consumption.
&nbsp; | `INTEGER` | distanceTraveled | Optional distance traveled in kilometers.

Additional information can be calculated on demand, such as the price per liter.

<br/>

## Database Migration
This section contains a brief description for each database migration:

###### Migration from 1 to 2
Migration steps:
* Add column "distanceTraveled" to table "petrol_entries"

###### Migration from 2 to 3
Migration steps:
* Change data type from column "id" from the table "petrol_entries" from `INTEGER NOT NULL` to `BLOB NOT NULL` to switch from an integer-based ID to a UUID.
* Change values of the column "epochSeconds" from the table "petrol_entries" from epoch seconds to epoch days.

<br/>

## Additional Information
This section contains additional information required to understand this documentation file.

###### Measurement Units
The file documents database values using units from German unit systems. The following conversions apply:

Unit | de-DE :de: _(Default)_ | en-US :us: | en-GB :gb:
--- | --- | --- | ---
Price | Euro | Dollars | Pound sterling
Price | Eurocents | Cents | Penny
Volume | Liters | Gallons | Gallons
Volume | Centliliters | 1/100 Gallons | 1/100 Gallons
Distance | Kilometers | Miles | Miles
Price per Volume | Euros per Liter | Dollars per Gallon | Pounds per Gallon

<br/>

***

2025-09-28  
&copy; Christian-2003
