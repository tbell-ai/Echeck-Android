package kr.co.tbell.echeck.views.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import kr.co.tbell.echeck.constant.ColumnContract.ColumnEntry;

public class AppDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "echeck.db";

    public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + ColumnEntry.USER_TABLE_NAME + " (" +
            ColumnEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ColumnEntry.COLUMN_NICKNAME + " TEXT," +
            ColumnEntry.COLUMN_BEFORE_ELECRT + " TEXT," +
            ColumnEntry.COLUMN_NOW_PERIOD + " TEXT," +
            ColumnEntry.COLUMN_USE + " TEXT," +
            ColumnEntry.COLUMN_HOUSE + " TEXT," +
            ColumnEntry.COLUMN_HOUSE_COUNT + " TEXT," +
            ColumnEntry.COLUMN_USER_CREATED_AT + " TEXT" + ")";

    public static final String CREATE_HOUSE_TABLE = "CREATE TABLE IF NOT EXISTS " + ColumnEntry.HOUSE_DISCOUNT_TABLE_NAME + " (" +
            ColumnEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN + " TEXT," +
            ColumnEntry.COLUMN_HOUSE_DISCOUNT1 + " TEXT," +
            ColumnEntry.COLUMN_HOUSE_DISCOUNT2 + " TEXT," +
            ColumnEntry.COLUMN_USER_ID + " TEXT," +
            " FOREIGN KEY (" + ColumnEntry.COLUMN_USER_ID + ") REFERENCES " + ColumnEntry.USER_TABLE_NAME + "(" + ColumnEntry._ID + "));";

    public static final String CREATE_AGREE_TABLE = "CREATE TABLE IF NOT EXISTS " + ColumnEntry.AGREE_TABLE_NAME + " (" +
            ColumnEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ColumnEntry.COLUMN_AGREE_NAME + " TEXT," +
            ColumnEntry.COLUMN_AGREE_TYPE + " TEXT," +
            ColumnEntry.COLUMN_AGREE_YN + " TEXT," +
            ColumnEntry.COLUMN_AGREE_CREATED_AT + " TEXT" + ")";

    public static final String CREATE_ELECT_TABLE = "CREATE TABLE IF NOT EXISTS " + ColumnEntry.ELECT_TABLE_NAME + " (" +
            ColumnEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ColumnEntry.COLUMN_ELECT + " TEXT," +
            ColumnEntry.COLUMN_CHARGE + " TEXT," +
            ColumnEntry.COLUMN_MEASURE + " TEXT," +
            ColumnEntry.COLUMN_ELECT_CREATED_AT + " TEXT" + ")";

    public static final String CREATE_PRODUCT_TABLE = "CREATE TABLE IF NOT EXISTS " + ColumnEntry.PRODUCT_TABLE_NAME + " (" +
            ColumnEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ColumnEntry.COLUMN_NAME + " TEXT," +
            ColumnEntry.COLUMN_PATTERN + " TEXT," +
            ColumnEntry.COLUMN_DAY_HOUR + " TEXT," +
            ColumnEntry.COLUMN_PRODUCT_CREATED_AT + " TEXT" + ")";

    public static final String DELETE_USER_TABLE = "DROP TABLE IF EXISTS " + ColumnEntry.USER_TABLE_NAME;
    public static final String DELETE_AGREE_TABLE = "DROP TABLE IF EXISTS " + ColumnEntry.AGREE_TABLE_NAME;
    public static final String DELETE_ELECT_TABLE = "DROP TABLE IF EXISTS " + ColumnEntry.ELECT_TABLE_NAME;
    public static final String DELETE_PRODUCT_TABLE = "DROP TABLE IF EXISTS " + ColumnEntry.PRODUCT_TABLE_NAME;
    public static final String DELETE_HOUSE_TABLE = "DROP TABLE IF EXISTS " + ColumnEntry.HOUSE_DISCOUNT_TABLE_NAME;

    private static AppDBHelper instance;

    public AppDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized AppDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppDBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_AGREE_TABLE);
        db.execSQL(CREATE_ELECT_TABLE);
        db.execSQL(CREATE_HOUSE_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(DELETE_USER_TABLE);
//        db.execSQL(DELETE_AGREE_TABLE);
//        db.execSQL(DELETE_ELECT_TABLE);
//        db.execSQL(DELETE_HOUSE_TABLE);
//        db.execSQL(DELETE_PRODUCT_TABLE);

        if(newVersion > oldVersion) {
            db.execSQL(DELETE_PRODUCT_TABLE);
            db.execSQL(DELETE_ELECT_TABLE);

            db.execSQL(CREATE_PRODUCT_TABLE);
            db.execSQL(CREATE_ELECT_TABLE);
        }

        onCreate(db);
    }
}
