package com.crushtech.newslify.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.crushtech.newslify.models.Article

@Database(
    entities = [Article::class],
    version = 12
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao
    //singleton

    companion object {
        //@Volatile indicates
        // that writes to this field are immediately made visible to other threads.
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        //this function gets called whenever we create an instance of this class
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article database.db"
            ).fallbackToDestructiveMigration().build()
        /** why fallbackToDestructiveMigration?
         *  it Allows Room to destructively recreate database tables
         *  if {@link Migration}s that would
         * migrate old database schemas to the latest schema version are not found.
         **/
    }
}