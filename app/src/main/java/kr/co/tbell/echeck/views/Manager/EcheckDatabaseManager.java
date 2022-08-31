package kr.co.tbell.echeck.views.Manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import kr.co.tbell.echeck.model.Agree;
import kr.co.tbell.echeck.model.Elect;
import kr.co.tbell.echeck.model.House;
import kr.co.tbell.echeck.model.User;
import kr.co.tbell.echeck.model.dto.HomeProduct;
import kr.co.tbell.echeck.views.helper.AppDBHelper;
import kr.co.tbell.echeck.constant.ColumnContract.ColumnEntry;

public class EcheckDatabaseManager {

    private AtomicInteger openCounter;
    private static EcheckDatabaseManager instance;
    private AppDBHelper dbHelper;
    private SQLiteDatabase db;

    private EcheckDatabaseManager(Context context) {
        dbHelper = AppDBHelper.getInstance(context);
        openCounter = new AtomicInteger();
    }

    public static synchronized EcheckDatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new EcheckDatabaseManager(context);
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDataBase() {
        if(openCounter.incrementAndGet() == 1) {
            db = dbHelper.getWritableDatabase();
        }

        return db;
    }

    public synchronized void closeDatabase() {
        if(openCounter.decrementAndGet() == 0) {
            db.close();
        }
    }

    public long putData(String tableName, ContentValues values) {
        long newRowId = 0;

        try {

            db.beginTransaction();
            newRowId = db.insert(tableName, null, values);
            db.setTransactionSuccessful();

        } catch (SQLException e){
            Log.d("db_error", "insert error");
        } finally {
            db.endTransaction();
        }

        return newRowId;
    }

    public boolean checkJoin(String tableName) {

        boolean result = false;

        String[] projection = {
                ColumnEntry._ID
        };

        Cursor cursor = db.query(
                tableName,            // The table to query
                projection,              // The array of columns to return (pass null to get all)
                null,           // The columns for the WHERE clause
                null,        // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // The sort order
        );

        while(cursor.moveToNext()) {
            if((cursor.getLong(cursor.getColumnIndexOrThrow(ColumnEntry._ID))) > 0) {
                result = true;
            }
        }
        cursor.close();

        return result;
    }

    public Agree getAgree() {

        Agree agree = new Agree();

        String[] projection = {
                ColumnEntry._ID,
                ColumnEntry.COLUMN_AGREE_NAME,
                ColumnEntry.COLUMN_AGREE_TYPE,
                ColumnEntry.COLUMN_AGREE_YN,
                ColumnEntry.COLUMN_AGREE_CREATED_AT
        };

        Cursor cursor = db.query(
                ColumnEntry.AGREE_TABLE_NAME,            // The table to query
                projection,              // The array of columns to return (pass null to get all)
                null,           // The columns for the WHERE clause
                null,        // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // The sort order
        );

        List<Agree> agrees = new ArrayList<Agree>();
        while(cursor.moveToNext()) {
            agree.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ColumnEntry._ID)));
            agree.setAgreeName(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_AGREE_NAME)));
            agree.setAgreeType(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_AGREE_TYPE)));
            agree.setAgreeYn(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_AGREE_YN)));
            agree.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_AGREE_CREATED_AT)));
            agrees.add(agree);
        }
        cursor.close();

        return agrees.get(0);
    }

    public User getUser() {

        User user = new User();

        String[] projection = {
                ColumnEntry._ID,
                ColumnEntry.COLUMN_NICKNAME,
                ColumnEntry.COLUMN_BEFORE_ELECRT,
                ColumnEntry.COLUMN_NOW_PERIOD,
                ColumnEntry.COLUMN_USE,
                ColumnEntry.COLUMN_HOUSE,
                ColumnEntry.COLUMN_HOUSE_COUNT,
                ColumnEntry.COLUMN_USER_CREATED_AT
        };

        Cursor cursor = db.query(
                ColumnEntry.USER_TABLE_NAME,            // The table to query
                projection,              // The array of columns to return (pass null to get all)
                null,           // The columns for the WHERE clause
                null,        // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // The sort order
        );

        List<User> users = new ArrayList<User>();
        while(cursor.moveToNext()) {
            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ColumnEntry._ID)));
            user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_NICKNAME)));
            user.setElectBefore(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_BEFORE_ELECRT)));
            user.setElectPeriod(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_NOW_PERIOD)));
            user.setElectUse(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_USE)));
            user.setElectHouse(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_HOUSE)));
            user.setHouseCount(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_HOUSE_COUNT)));
            user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_USER_CREATED_AT)));
            users.add(user);
        }
        cursor.close();

        return users.get(0);
    }

    public List<House> getHouse() {

        String[] projection = {
                ColumnEntry._ID,
                ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN,
                ColumnEntry.COLUMN_HOUSE_DISCOUNT1,
                ColumnEntry.COLUMN_HOUSE_DISCOUNT2,
                ColumnEntry.COLUMN_USER_ID
        };

        Cursor cursor = db.query(
                ColumnEntry.HOUSE_DISCOUNT_TABLE_NAME,            // The table to query
                projection,              // The array of columns to return (pass null to get all)
                null,           // The columns for the WHERE clause
                null,        // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // The sort order
        );

        List<House> houses = new ArrayList<House>();
        while(cursor.moveToNext()) {
            House house = new House();
            house.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ColumnEntry._ID)));
            house.setHouseDiscountYn(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN)));
            house.setHouseDiscount1(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_HOUSE_DISCOUNT1)));
            house.setHouseDiscount2(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_HOUSE_DISCOUNT2)));
            house.setUserHouseId(cursor.getLong(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_USER_ID)));
            houses.add(house);
        }
        cursor.close();

        return houses;
    }

    public List<Elect> getElect() {

        String[] projection = {
                ColumnEntry._ID,
                ColumnEntry.COLUMN_ELECT,
                ColumnEntry.COLUMN_CHARGE,
                ColumnEntry.COLUMN_MEASURE,
                ColumnEntry.COLUMN_ELECT_CREATED_AT
        };

        Cursor cursor = db.query(
                ColumnEntry.ELECT_TABLE_NAME,            // The table to query
                projection,              // The array of columns to return (pass null to get all)
                null,           // The columns for the WHERE clause
                null,        // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // The sort order
        );

        List<Elect> elects = new ArrayList<Elect>();
        while(cursor.moveToNext()) {
            Elect elect = new Elect();
            elect.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ColumnEntry._ID)));
            elect.setElectAmount(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_ELECT)));
            elect.setElectCharge(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_CHARGE)));
            elect.setElectMeasure(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_MEASURE)));
            elect.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_ELECT_CREATED_AT)));
            elects.add(elect);
        }
        cursor.close();

        return elects;
    }

    public House getOneHouse(Long houseId) {

        House house = new House();
        String selection = ColumnEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(houseId)};

        String[] projection = {
                ColumnEntry._ID,
                ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN,
                ColumnEntry.COLUMN_HOUSE_DISCOUNT1,
                ColumnEntry.COLUMN_HOUSE_DISCOUNT2,
                ColumnEntry.COLUMN_USER_ID
        };

        Cursor cursor = db.query(
                ColumnEntry.HOUSE_DISCOUNT_TABLE_NAME,            // The table to query
                projection,              // The array of columns to return (pass null to get all)
                selection,           // The columns for the WHERE clause
                selectionArgs,        // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // The sort order
        );

        while(cursor.moveToNext()) {
            house.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ColumnEntry._ID)));
            house.setHouseDiscountYn(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_HOUSE_DISCOUNT_YN)));
            house.setHouseDiscount1(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_HOUSE_DISCOUNT1)));
            house.setHouseDiscount2(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_HOUSE_DISCOUNT2)));
            house.setUserHouseId(cursor.getLong(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_USER_ID)));
        }
        cursor.close();

        return house;

    }

    public List<HomeProduct> getProduct() {

        String[] projection = {
                ColumnEntry._ID,
                ColumnEntry.COLUMN_NAME,
                ColumnEntry.COLUMN_PATTERN,
                ColumnEntry.COLUMN_DAY_HOUR,
                ColumnEntry.COLUMN_ELECT_CREATED_AT
        };

        Cursor cursor = db.query(
                ColumnEntry.PRODUCT_TABLE_NAME,            // The table to query
                projection,              // The array of columns to return (pass null to get all)
                null,           // The columns for the WHERE clause
                null,        // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // The sort order
        );

        List<HomeProduct> products = new ArrayList<>();
        while(cursor.moveToNext()) {
            HomeProduct product = new HomeProduct();

            product.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ColumnEntry._ID)));
            product.setProduct(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_NAME)));
            product.setUsagePattern(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_PATTERN)));
            product.setDayHour(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_DAY_HOUR)));
            product.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_PRODUCT_CREATED_AT)));

            products.add(product);
        }
        cursor.close();

        return products;
    }

    public HomeProduct getOneProduct(Long productId) {

        HomeProduct product = new HomeProduct();
        String selection = ColumnEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(productId)};

        String[] projection = {
                ColumnEntry._ID,
                ColumnEntry.COLUMN_NAME,
                ColumnEntry.COLUMN_PATTERN,
                ColumnEntry.COLUMN_DAY_HOUR,
                ColumnEntry.COLUMN_ELECT_CREATED_AT
        };

        Cursor cursor = db.query(
                ColumnEntry.PRODUCT_TABLE_NAME,            // The table to query
                projection,              // The array of columns to return (pass null to get all)
                selection,           // The columns for the WHERE clause
                selectionArgs,        // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // The sort order
        );

        while(cursor.moveToNext()) {
            product.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ColumnEntry._ID)));
            product.setProduct(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_NAME)));
            product.setUsagePattern(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_PATTERN)));
            product.setDayHour(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_DAY_HOUR)));
            product.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(ColumnEntry.COLUMN_PRODUCT_CREATED_AT)));
        }
        cursor.close();

        return product;

    }

    public int updateData(String tableName, ContentValues values, String whereClause, String[] whereArgs) {

        int result = 0;

        try {

            db.beginTransaction();
            result = db.update(tableName, values, whereClause, whereArgs);
            db.setTransactionSuccessful();

        } catch (SQLException e) {
            Log.d("db_error", "update error");
        } finally {
            db.endTransaction();
        }

        return result;
    }

    public int deleteData(String tableName, String whereClause, String[] whereArgs) {

        int result = 0;

        try {

            db.beginTransaction();
            result = db.delete(tableName, whereClause, whereArgs);
            db.setTransactionSuccessful();

        } catch (SQLException e) {
            Log.d("db_error", "delete error");
        } finally {
            db.endTransaction();
        }

        return result;
    }

    public void removeData() {

        db.execSQL(ColumnEntry.DELETE_AGREE_DATA);
        db.execSQL(ColumnEntry.DELETE_HOUSE_DATA);
        db.execSQL(ColumnEntry.DELETE_USER_DATA);
        db.execSQL(ColumnEntry.DELETE_ELECT_DATA);
        db.execSQL(ColumnEntry.DELETE_PRODUCT_DATA);

    }

    public void initInfo() {

        db.execSQL(ColumnEntry.DELETE_HOUSE_DATA);
        db.execSQL(ColumnEntry.DELETE_USER_DATA);

    }

}
