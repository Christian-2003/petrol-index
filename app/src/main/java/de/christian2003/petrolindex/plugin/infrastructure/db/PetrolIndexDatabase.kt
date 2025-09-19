package de.christian2003.petrolindex.plugin.infrastructure.db

import android.content.Context
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.christian2003.petrolindex.plugin.infrastructure.db.converter.LocalDateConverter
import de.christian2003.petrolindex.plugin.infrastructure.db.converter.UuidConverter
import de.christian2003.petrolindex.plugin.infrastructure.db.dao.ConsumptionDao
import de.christian2003.petrolindex.plugin.infrastructure.db.entities.ConsumptionEntity
import kotlin.uuid.Uuid


/**
 * Class implements the database for the Petrol Index app.
 */
@Database(
    entities = [ConsumptionEntity::class],
    version = 3
)
@TypeConverters(
    UuidConverter::class,
    LocalDateConverter::class
)
abstract class PetrolIndexDatabase: RoomDatabase() {

    /**
     * Attribute stores the DAO through which to access the table storing all
     * petrol entries.
     */
    abstract val petrolEntryDao: ConsumptionDao


    companion object {

        /**
         * Attribute stores the singleton instance of the database.
         */
        @Volatile
        private var INSTANCE: PetrolIndexDatabase? = null


        /**
         * Method returns the singleton instance of the database.
         */
        fun getInstance(context: Context): PetrolIndexDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context = context.applicationContext,
                        klass = PetrolIndexDatabase::class.java,
                        name = "petrol_index_database"
                    )
                        .fallbackToDestructiveMigration()
                        .addMigrations(MIGRATION_1_2)
                        .addMigrations(MIGRATION_2_3)
                        .build()
                }
                return instance
            }
        }

    }

}


/**
 * Attribute stores the migration used to migrate the database from version 1 to version 2.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE petrol_entries ADD COLUMN distanceTraveled INTEGER")
    }
}

/**
 * Stores the migration used to migrate the database table "petrol_entries" from an integer-based
 * primary key to a UUID-based primary key. Furthermore, this changes the attribute "epochSeconds",
 * which stores epoch seconds, to epoch days.
 * Since SQLite does not support changing the datatype of a column, a new table is created
 * temporarily to store the data with the new UUIDs.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            //Create new table with changed type for primary key 'id':
            db.execSQL("""CREATE TABLE `petrol_entries_new` (
                        `id` BLOB PRIMARY KEY NOT NULL,
                        `epochSecond` INTEGER NOT NULL,
                        `volume` INTEGER NOT NULL,
                        `totalPrice` INTEGER NOT NULL,
                        `description` TEXT NOT NULL,
                        `distanceTraveled` INTEGER
                    )""")

            //Copy data from old table into new table:
            val cursor = db.query("SELECT * FROM petrol_entries")
            val uuidConverter = UuidConverter()
            while (cursor.moveToNext()) {
                val oldId: Int = cursor.getInt(0)
                val oldEpochSecond: Long = cursor.getLong(1)
                val volume: Int = cursor.getInt(2)
                val totalPrice: Int = cursor.getInt(3)
                val description: String = cursor.getString(4)
                val distanceTraveled: Int? = cursor.getIntOrNull(5)

                db.execSQL(
                    sql = "INSERT INTO `petrol_entries_new` (`id`, `epochSecond`, `volume`, `totalPrice`, `description`, `distanceTraveled`) VALUES(?, ?, ?, ?, ?, ?)",
                    bindArgs = arrayOf(
                        uuidConverter.fromUuid(Uuid.random()),
                        oldEpochSecond / 86400, //Changes epoch seconds to days (there are 86,400 seconds / day)
                        volume,
                        totalPrice,
                        description,
                        distanceTraveled
                    )
                )
            }
            cursor.close()

            //Drop the old table:
            db.execSQL("DROP TABLE `petrol_entries`")

            //Rename new table:
            db.execSQL("ALTER TABLE `petrol_entries_new` RENAME TO `petrol_entries`")
        }
        catch (e: Exception) {
            Log.e("DB Migration", "A fatal error occurred while migrating to database version 3: ${e.message}")
            throw e
        }
    }
}

