package de.christian2003.petrolindex.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


/**
 * Class implements the database for the Petrol Index app.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
@Database(entities = [PetrolEntry::class], version = 2, exportSchema = false)
abstract class PetrolIndexDatabase: RoomDatabase() {

    /**
     * Attribute stores the DAO through which to access the table storing all
     * petrol entries.
     */
    abstract val petrolEntryDao: PetrolEntryDao


    companion object {

        /**
         * Attribute stores the singleton instance of the database.
         */
        @Volatile
        private var INSTANCE: PetrolIndexDatabase? = null

        /**
         * Attribute stores the migration used to migrate the database from version 1 to version 2.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE petrol_entries ADD COLUMN distanceTraveled INTEGER")
            }
        }


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
                        .build()
                }
                return instance
            }
        }

    }

}
