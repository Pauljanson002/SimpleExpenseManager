package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    SQLiteDatabase database;

    public PersistentTransactionDAO(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        String query = "insert into account_transaction (accountno,expensetype,amount,date) values (?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(query);
        statement.bindString(1,accountNo);
        if(expenseType == ExpenseType.EXPENSE){
            statement.bindLong(2,0);
        }else{
            statement.bindLong(2,1);
        }
        statement.bindDouble(3,amount);
        statement.bindLong(4,date.getTime());
        statement.executeInsert();
    }


    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "select * from account_transaction";
        Cursor cursor = database.rawQuery(query,null);
        try {
            while (cursor.moveToNext()){
                Transaction transaction = new Transaction(
                        new Date(cursor.getLong(cursor.getColumnIndex("data"))),
                        cursor.getString(cursor.getColumnIndex("accountno")),
                        (cursor.getInt(cursor.getColumnIndex("expensetype")) == 0)? ExpenseType.EXPENSE:ExpenseType.INCOME ,
                        cursor.getDouble(cursor.getColumnIndex("amount"))
                );
                transactions.add(transaction);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "select * from account_transaction limit " + limit;
        Cursor cursor = database.rawQuery(query,null);
        try{
            while(cursor.moveToNext()){
                Transaction transaction = new Transaction(
                        new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                        cursor.getString(cursor.getColumnIndex("accountno")),
                        (cursor.getInt(cursor.getColumnIndex("expensetype")) == 0)? ExpenseType.EXPENSE:ExpenseType.INCOME,
                        cursor.getDouble(cursor.getColumnIndex("amount"))
                );
                transactions.add(transaction);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(cursor!= null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return transactions;
    }
}
