package de.christian2003.petrolindex.plugin.infrastructure.db

import androidx.core.database.getIntOrNull
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import okio.IOException
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.jvm.Throws
import kotlin.uuid.Uuid


@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration_test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        assetsFolder = PetrolIndexDatabase::class.java.canonicalName!!,
        openFactory = FrameworkSQLiteOpenHelperFactory()
    )


    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        //Create database on version 1:
        var db = helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO `petrol_entries` (`id`, `epochSecond`, `volume`, `totalPrice`, `description`) VALUES(1, 123456, 1500, 3000, \"Consumption 1\")")
            execSQL("INSERT INTO `petrol_entries` (`id`, `epochSecond`, `volume`, `totalPrice`, `description`) VALUES(2, 123457, 2000, 4000, \"Consumption 2\")")
            execSQL("INSERT INTO `petrol_entries` (`id`, `epochSecond`, `volume`, `totalPrice`, `description`) VALUES(3, 123458, 2500, 5000, \"Consumption 3\")")
            close()
        }

        //Migrate to version 2:
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        //Validate data after migration:
        val cursor = db.query("SELECT * FROM `petrol_entries` ORDER BY `id` ASC")

        //Row 1:
        Assert.assertTrue(cursor.moveToFirst())
        Assert.assertEquals(1, cursor.getInt(cursor.getColumnIndex("id")))
        Assert.assertEquals(123456, cursor.getInt(cursor.getColumnIndex("epochSecond")))
        Assert.assertEquals(1500, cursor.getInt(cursor.getColumnIndex("volume")))
        Assert.assertEquals(3000, cursor.getInt(cursor.getColumnIndex("totalPrice")))
        Assert.assertEquals("Consumption 1", cursor.getString(cursor.getColumnIndex("description")))
        Assert.assertNull(cursor.getIntOrNull(cursor.getColumnIndex("distanceTraveled")))

        //Row 2:
        Assert.assertTrue(cursor.moveToNext())
        Assert.assertEquals(2, cursor.getInt(cursor.getColumnIndex("id")))
        Assert.assertEquals(123457, cursor.getInt(cursor.getColumnIndex("epochSecond")))
        Assert.assertEquals(2000, cursor.getInt(cursor.getColumnIndex("volume")))
        Assert.assertEquals(4000, cursor.getInt(cursor.getColumnIndex("totalPrice")))
        Assert.assertEquals("Consumption 2", cursor.getString(cursor.getColumnIndex("description")))
        Assert.assertNull(cursor.getIntOrNull(cursor.getColumnIndex("distanceTraveled")))

        //Row 3:
        Assert.assertTrue(cursor.moveToNext())
        Assert.assertEquals(3, cursor.getInt(cursor.getColumnIndex("id")))
        Assert.assertEquals(123458, cursor.getInt(cursor.getColumnIndex("epochSecond")))
        Assert.assertEquals(2500, cursor.getInt(cursor.getColumnIndex("volume")))
        Assert.assertEquals(5000, cursor.getInt(cursor.getColumnIndex("totalPrice")))
        Assert.assertEquals("Consumption 3", cursor.getString(cursor.getColumnIndex("description")))
        Assert.assertNull(cursor.getIntOrNull(cursor.getColumnIndex("distanceTraveled")))

        cursor.close()
    }


    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        //Create database on version 2:
        var db = helper.createDatabase(TEST_DB, 2).apply {
            execSQL("INSERT INTO `petrol_entries` (`id`, `epochSecond`, `volume`, `totalPrice`, `description`, `distanceTraveled`) VALUES(1, 518400, 1500, 3000, \"Consumption 1\", NULL)")
            execSQL("INSERT INTO `petrol_entries` (`id`, `epochSecond`, `volume`, `totalPrice`, `description`, `distanceTraveled`) VALUES(2, 604800, 2000, 4000, \"Consumption 2\", NULL)")
            execSQL("INSERT INTO `petrol_entries` (`id`, `epochSecond`, `volume`, `totalPrice`, `description`, `distanceTraveled`) VALUES(3, 691200, 2500, 5000, \"Consumption 3\", 350)")
            close()
        }

        //Migrate to version 3:
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)

        //Validate data after migration:
        val cursor = db.query("SELECT * FROM `petrol_entries` ORDER BY `epochSecond` ASC")

        //Row 1:
        Assert.assertTrue(cursor.moveToFirst())
        Uuid.fromByteArray(cursor.getBlob(cursor.getColumnIndex("id")))
        Assert.assertEquals(6, cursor.getInt(cursor.getColumnIndex("epochSecond")))
        Assert.assertEquals(1500, cursor.getInt(cursor.getColumnIndex("volume")))
        Assert.assertEquals(3000, cursor.getInt(cursor.getColumnIndex("totalPrice")))
        Assert.assertEquals("Consumption 1", cursor.getString(cursor.getColumnIndex("description")))
        Assert.assertNull(cursor.getIntOrNull(cursor.getColumnIndex("distanceTraveled")))

        //Row 2:
        Assert.assertTrue(cursor.moveToNext())
        Uuid.fromByteArray(cursor.getBlob(cursor.getColumnIndex("id")))
        Assert.assertEquals(7, cursor.getInt(cursor.getColumnIndex("epochSecond")))
        Assert.assertEquals(2000, cursor.getInt(cursor.getColumnIndex("volume")))
        Assert.assertEquals(4000, cursor.getInt(cursor.getColumnIndex("totalPrice")))
        Assert.assertEquals("Consumption 2", cursor.getString(cursor.getColumnIndex("description")))
        Assert.assertNull(cursor.getIntOrNull(cursor.getColumnIndex("distanceTraveled")))

        //Row 3:
        Assert.assertTrue(cursor.moveToNext())
        Uuid.fromByteArray(cursor.getBlob(cursor.getColumnIndex("id")))
        Assert.assertEquals(8, cursor.getInt(cursor.getColumnIndex("epochSecond")))
        Assert.assertEquals(2500, cursor.getInt(cursor.getColumnIndex("volume")))
        Assert.assertEquals(5000, cursor.getInt(cursor.getColumnIndex("totalPrice")))
        Assert.assertEquals("Consumption 3", cursor.getString(cursor.getColumnIndex("description")))
        Assert.assertEquals(350, cursor.getIntOrNull(cursor.getColumnIndex("distanceTraveled")))

        cursor.close()
    }


    @Test
    @Throws(IOException::class)
    fun migrate1ToLatest() {
        //Create database on version 1:
        var db = helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO `petrol_entries` (`id`, `epochSecond`, `volume`, `totalPrice`, `description`) VALUES(1, 518400, 1500, 3000, \"Consumption 1\")")
            execSQL("INSERT INTO `petrol_entries` (`id`, `epochSecond`, `volume`, `totalPrice`, `description`) VALUES(2, 604800, 2000, 4000, \"Consumption 2\")")
            execSQL("INSERT INTO `petrol_entries` (`id`, `epochSecond`, `volume`, `totalPrice`, `description`) VALUES(3, 691200, 2500, 5000, \"Consumption 3\")")
            close()
        }

        //Migrate to latest version:
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)

        //Validate data after migration:
        val cursor = db.query("SELECT * FROM `petrol_entries` ORDER BY `epochSecond` ASC")

        //Row 1:
        Assert.assertTrue(cursor.moveToFirst())
        Uuid.fromByteArray(cursor.getBlob(cursor.getColumnIndex("id")))
        Assert.assertEquals(6, cursor.getInt(cursor.getColumnIndex("epochSecond")))
        Assert.assertEquals(1500, cursor.getInt(cursor.getColumnIndex("volume")))
        Assert.assertEquals(3000, cursor.getInt(cursor.getColumnIndex("totalPrice")))
        Assert.assertEquals("Consumption 1", cursor.getString(cursor.getColumnIndex("description")))
        Assert.assertNull(cursor.getIntOrNull(cursor.getColumnIndex("distanceTraveled")))

        //Row 2:
        Assert.assertTrue(cursor.moveToNext())
        Uuid.fromByteArray(cursor.getBlob(cursor.getColumnIndex("id")))
        Assert.assertEquals(7, cursor.getInt(cursor.getColumnIndex("epochSecond")))
        Assert.assertEquals(2000, cursor.getInt(cursor.getColumnIndex("volume")))
        Assert.assertEquals(4000, cursor.getInt(cursor.getColumnIndex("totalPrice")))
        Assert.assertEquals("Consumption 2", cursor.getString(cursor.getColumnIndex("description")))
        Assert.assertNull(cursor.getIntOrNull(cursor.getColumnIndex("distanceTraveled")))

        //Row 3:
        Assert.assertTrue(cursor.moveToNext())
        Uuid.fromByteArray(cursor.getBlob(cursor.getColumnIndex("id")))
        Assert.assertEquals(8, cursor.getInt(cursor.getColumnIndex("epochSecond")))
        Assert.assertEquals(2500, cursor.getInt(cursor.getColumnIndex("volume")))
        Assert.assertEquals(5000, cursor.getInt(cursor.getColumnIndex("totalPrice")))
        Assert.assertEquals("Consumption 3", cursor.getString(cursor.getColumnIndex("description")))
        Assert.assertNull(cursor.getIntOrNull(cursor.getColumnIndex("distanceTraveled")))

        cursor.close()
    }

}
