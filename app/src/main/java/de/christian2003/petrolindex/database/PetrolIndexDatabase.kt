package de.christian2003.petrolindex.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * Class implements the database for the Petrol Index app.
 *
 * @author  Christian-2003
 * @since   1.0.0
 */
@Database(entities = [PetrolEntry::class], version = 1, exportSchema = false)
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
                    ).fallbackToDestructiveMigration().build()
                }
                return instance
            }
        }

    }

}
