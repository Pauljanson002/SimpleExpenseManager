package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final SQLiteDatabase database;

    public PersistentAccountDAO(SQLiteDatabase db){
        this.database = db;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<>();
        String query = "select accountNo from account";
        Cursor cursor = database.rawQuery(query,null);
        try{
            if(cursor.moveToFirst()){
                do{
                    String accountNo = cursor.getString(cursor.getColumnIndex("accountno"));
                    accountNumbers.add(accountNo);
                }
                while (cursor.moveToNext());
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        ArrayList<Account> accounts = new ArrayList<>();
        String query = "select * from account";
        Cursor cursor =  database.rawQuery(query,null);
        try {
            while (cursor.moveToNext()){
                Account account = new Account(
                        cursor.getString(cursor.getColumnIndex("accountno")),
                        cursor.getString(cursor.getColumnIndex("bankname")),
                        cursor.getString(cursor.getColumnIndex("accountno")),
                        cursor.getDouble(cursor.getColumnIndex("balance"))
                );
                accounts.add(account);
            }
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        finally {
            if(cursor !=  null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
       String query = "select * from account where accountno = "+accountNo;
       Cursor cursor = database.rawQuery(query,null);
       if(cursor != null){
           cursor.moveToFirst();
           Account account = new Account(
                   cursor.getString(cursor.getColumnIndex("accountno")),
                   cursor.getString(cursor.getColumnIndex("bankname")),
                   cursor.getString(cursor.getColumnIndex("accountholdername")),
                   cursor.getDouble(cursor.getColumnIndex("balance"))
           );
           return account;
       }else {
           throw new InvalidAccountException("Account no "+accountNo+" is invalid");
       }
    }

    @Override
    public void addAccount(Account account) {
        String query = "insert into account (accountno,bankname,accountholdername,balance) " +
                "values(?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(query);
        statement.bindString(1,account.getAccountNo());
        statement.bindString(2,account.getBankName());
        statement.bindString(3,account.getAccountHolderName());
        statement.bindDouble(4,account.getBalance());
        statement.executeInsert();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String query = "delete from account where accountno = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.bindString(1,accountNo);
        if(statement.executeUpdateDelete() == 0){
           throw new InvalidAccountException("Account not found");
        }

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        String query = "update account set balance = balance + ? where accountno = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.bindString(2,accountNo);
        if(expenseType == ExpenseType.EXPENSE){
            statement.bindDouble(1,-amount);
        }else {
           statement.bindDouble(1,amount);
        }
        if(statement.executeUpdateDelete() ==0){
            throw new InvalidAccountException("Account not found");
        }

    }
}
