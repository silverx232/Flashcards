package com.barnes.flashcards.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.barnes.flashcards.dao.DeckDao;
import com.barnes.flashcards.dao.FlashcardDao;
import com.barnes.flashcards.model.Deck;
import com.barnes.flashcards.model.Flashcard;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that builds the SQLite database in the Room style.
 */
@Database(entities = {Deck.class, Flashcard.class},
        version = 1, exportSchema = false)
@TypeConverters({DatabaseConverter.class})
public abstract class FlashcardsRoomDatabase extends RoomDatabase {

    public abstract DeckDao deckDao();
    public abstract FlashcardDao flashcardDao();

    // The number of threads the database can operate on
    public static final int NUMBER_OF_THREADS = 4;

    //Holds the instance of the database. There should only be one instance of the database in the program
    private static volatile FlashcardsRoomDatabase INSTANCE;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //This callback creates test data in the database
    private static final RoomDatabase.Callback sRoomDatabaseCallback =
        new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // create test data
                DeckDao deckDao = INSTANCE.deckDao();

                LocalDateTime date = LocalDateTime.now();

                deckDao.insert(new Deck(1, 10, 0, 0, "Deck 1", date));
                deckDao.insert(new Deck(2, 10, 0, 0, "Deck 2", date));
                deckDao.insert(new Deck(3, 0, 0, 0, "Deck 3", date));
                deckDao.insert(new Deck(4, 0, 0, 0, "Deck 4", date));
                deckDao.insert(new Deck(5, 0, 0, 0, "Deck 5", date));

                FlashcardDao flashcardDao = INSTANCE.flashcardDao();

                flashcardDao.insert(new Flashcard(1, "deck 1 front 1", "deck 1 back 1",
                        Flashcard.Status.STILL_LEARNING, 1));
                flashcardDao.insert(new Flashcard(2, "deck 1 front 2", "deck 1 back 2",
                        Flashcard.Status.STILL_LEARNING, 1));
                flashcardDao.insert(new Flashcard(3, "deck 1 front 3", "deck 1 back 3",
                        Flashcard.Status.STILL_LEARNING, 1));
                flashcardDao.insert(new Flashcard(4, "deck 1 front 4", "deck 1 back 4",
                        Flashcard.Status.STILL_LEARNING, 1));
                flashcardDao.insert(new Flashcard(5, "deck 1 front 5", "deck 1 back 5",
                        Flashcard.Status.STILL_LEARNING, 1));
                flashcardDao.insert(new Flashcard(6, "deck 1 front 6", "deck 1 back 6",
                        Flashcard.Status.STILL_LEARNING, 1));
                flashcardDao.insert(new Flashcard(7, "deck 1 front 7", "deck 1 back 7",
                        Flashcard.Status.STILL_LEARNING, 1));
                flashcardDao.insert(new Flashcard(8, "deck 1 front 8", "deck 1 back 8",
                        Flashcard.Status.STILL_LEARNING, 1));
                flashcardDao.insert(new Flashcard(9, "deck 1 front 9", "deck 1 back 9",
                        Flashcard.Status.STILL_LEARNING, 1));
                flashcardDao.insert(new Flashcard(10, "deck 1 front 10", "deck 1 back 10",
                        Flashcard.Status.STILL_LEARNING, 1));

                flashcardDao.insert(new Flashcard(11, "deck 2 front 1", "deck 2 back 1",
                        Flashcard.Status.STILL_LEARNING, 2));
                flashcardDao.insert(new Flashcard(12, "deck 2 front 2", "deck 2 back 2",
                        Flashcard.Status.STILL_LEARNING, 2));
                flashcardDao.insert(new Flashcard(13, "deck 2 front 3", "deck 2 back 3",
                        Flashcard.Status.STILL_LEARNING, 2));
                flashcardDao.insert(new Flashcard(14, "deck 2 front 4", "deck 2 back 4",
                        Flashcard.Status.STILL_LEARNING, 2));
                flashcardDao.insert(new Flashcard(15, "deck 2 front 5", "deck 2 back 5",
                        Flashcard.Status.STILL_LEARNING, 2));
                flashcardDao.insert(new Flashcard(16, "deck 2 front 6", "deck 2 back 6",
                        Flashcard.Status.STILL_LEARNING, 2));
                flashcardDao.insert(new Flashcard(17, "deck 2 front 7", "deck 2 back 7",
                        Flashcard.Status.STILL_LEARNING, 2));
                flashcardDao.insert(new Flashcard(18, "deck 2 front 8", "deck 2 back 8",
                        Flashcard.Status.STILL_LEARNING, 2));
                flashcardDao.insert(new Flashcard(19, "deck 2 front 9", "deck 2 back 9",
                        Flashcard.Status.STILL_LEARNING, 2));
                flashcardDao.insert(new Flashcard(20, "deck 2 front 10", "deck 2 back 10",
                        Flashcard.Status.STILL_LEARNING, 2));

            });
            }
        };

    /**
     * Method that gets an instance of the database.
     *
     * <p> There should only be a single instance of the database in the program. This method returns
     * that instance, or creates one if it doesn't already exist. </p>
     * @param context The context calling the database
     * @return Returns an instance of the database
     */
    public static FlashcardsRoomDatabase getDatabase(final Context context) {
        // If the instance has not been created yet
        if (INSTANCE == null) {
            synchronized (FlashcardsRoomDatabase.class) {
                if (INSTANCE == null) {

                    //Create an instance of the database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FlashcardsRoomDatabase.class, "flashcards_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
