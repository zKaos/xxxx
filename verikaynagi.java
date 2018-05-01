package com.example.slymn.ilk_sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Slymn on 19.04.2018.
 */

public class verikaynagi {
    SQLiteDatabase db;
    sql_katmani bdb;
 public verikaynagi(Context c){

     bdb=new sql_katmani(c);

 }
    public void ac()
    {
        db=bdb.getWritableDatabase();


    }
    public void kapat()
    {

     bdb.close();

    }
    public void degerOlustur(deger d){
        //int motor=5;
      //  deger d=new deger();
       // d.setMotor(motor);

        ContentValues val=new ContentValues();
        val.put("motor",d.getMotor());
        val.put("panel",d.getPanel());
        val.put("tarih",d.getTarih());
        db.insert("deger","",val);
        //ContentValues val1=new ContentValues();
        //val1.put("panel",d.getPanel());

        //db.insert("deger","panel",val);

    }

    public List<deger> listele() {

        String kolonlar[]={"motor","panel","tarih"};
        List<deger> liste=new ArrayList<deger>();
        Cursor c=db.query("deger",kolonlar,null,null,null,null,null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            String m=c.getString(0);
            String p=c.getString(1);
            String t=c.getString(2);
            //String eleman=""+m+""+p;
            deger d=new deger(m,p,t);


            liste.add(d);
            c.moveToNext();
        }
        c.close();
        return liste;

    }
    public String[] sonuc(){
         String sonuc[]=new String[2];
        String kolonlar[]={"motor","panel","tarih"};
        List<deger> liste=new ArrayList<deger>();
        Cursor c=db.query("deger",kolonlar,null,null,null,null,null);
        c.moveToLast();
        int []m=new int[2];
        int[]p=new int[2];
        String t="";

            for(int i=0;i<2;i++) {
                 m[i] = c.getInt(0);
                 p[i] = c.getInt(1);
                 t = c.getString(2);
                c.moveToPrevious();
            }
                if (m[0] > m[1]) {
                    sonuc[0]="motor verimli çalışıyor";


                }
                else if(m[1]>m[0]) {
                    sonuc[0]= "motor verimsiz çalışıyor";
                }
                 if (p[0] > p[1]) {
                sonuc[1]= "panel verimli çalışıyor";


                }
                if(p[1]>p[0]) {
                sonuc[1]= "panel verimsiz çalışıyor";
                    }




       c.close();
       return sonuc;
    }
}
