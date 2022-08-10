package com.example.drivers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

//ORTIZ SOQUI IRAK DAREK - INGENIERO EN SISTEMAS DE INFORMACIÓN

public class MainActivity extends AppCompatActivity {

    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lv);
        String jsonS = null;

        try{
            InputStream input = getAssets().open("data.json");
            int size = input.available();
            byte[] b = new byte[size];
            input.read(b);
            input.close();
            jsonS = new String(b, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        String result="";

        //I did create 4 arraylist:
        //The first one is for get the shipments
        //The second one is for get the drivers
        //The third one is for the shipments too because in the first array i am going to remove the spaces, dots, and numbers
        //The last one is for add every driver's name with every shipment with a "," between that too

        ArrayList<String> a1 = new ArrayList();
        ArrayList<String> a2 = new ArrayList();
        ArrayList<String> a1R = new ArrayList();
        ArrayList<String> fin = new ArrayList();
        fin.add("Defa,ult");

        //Here i am getting the values of the json file in that arrays
        if(jsonS != null){
            try{
                JSONObject json = new JSONObject(jsonS);
                JSONArray arr = json.getJSONArray("shipments");
                JSONArray arr2 = json.getJSONArray("drivers");
                String x;
                for(int i=0;i<arr.length();i++){
                    a1R.add(arr.getString(i));
                    x=arr.getString(i).replaceAll("\\d","");
                    x = x.replaceAll(" ","");
                    x = x.replaceAll("\\.","");
                    a1.add(x);
                    //Log.d("COMPROBAR",a1.get(i));
                    //result = result + arr.getString(i)+"\n";
                }
                for(int i=0;i<arr2.length();i++){
                    a2.add(arr2.getString(i));
                    //result = result + arr2.getString(i)+"\n";
                }



            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        //This is the part of the code that recieves the event of touch an item of the list

        lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,a2));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int p = lv.getPositionForView(view);

                //What the next line does is compare an item from fin when the items of a1 (shipments)
                //this because the code does not going to work with shipments that the user has touched
                doNotDuplicateShip(a1,fin);


                String choice=String.valueOf(a2.get(p));

                //In the next line i did a method "cleanText" that is for delete spaces, and numbers
                choice = cleanText(choice);

                String[] c,parts,ps;

                char letter,decision=' ';
                int letters=0,position2=0;
                double ss,valueM=0,max=0;
                boolean b=false;
                String word="";

                int position=0;
                //Here i am compairing if some element of the shipments exists in the array fin
                //if it is, i save its position in position2 and after is going to be in a condition that
                //is going to show the existing record. if doesnt exists, is going to register it with the complete
                //process.
                for(int x=0;x<fin.size();x++){
                    word = fin.get(x);
                    word = cleanText(word);
                    parts = word.split(",");
                    if(choice.equals(parts[1])){
                        b=true;
                        position2 = x;
                    }
                }
                if(b){
                    Log.d("COMPROBAR",fin.get(position2)+" YA SE TENÍA REGISTRO DEL DRIVER");
                    ps = fin.get(position2).split(",");
                    //Toast.makeText(MainActivity.this, ps[0],Toast.LENGTH_SHORT).show();
                    //This method is for show by a toast the text.
                    showFinal(ps[0],a1R);
                    //Log.d("COMPROBAR","ELEMENTO ENCONTRADO");
                }else{
                    for(int m=0;m<a1.size();m++){
                        letters=0;
                        if(a1.get(m).length()%2==0){ //If the length of the shipment is even
                            decision='e'; //do this
                        }else{
                            decision='o'; //if it is no even, is odd and do this
                        }
                        switch (decision){
                            //In the case 'e', valueM is 1.5 and is going to be multiplied by the amount of vowels
                            case 'e':
                                valueM = 1.5;
                                for(int j=0;j<choice.length();j++){
                                    letter = choice.charAt(j);
                                    if((letter=='a')||(letter=='e')||(letter=='i')||(letter=='o')||(letter=='u')){
                                        letters++;
                                    }
                                }
                                break;
                            case 'o':
                                //In case 'o', valueM is 1 and is going to be multiplied by the amount of consontants
                                valueM=1;
                                for(int j=0;j<choice.length();j++){
                                    letter = choice.charAt(j);
                                    if((letter=='a')||(letter=='e')||(letter=='i')||(letter=='o')||(letter=='u')){

                                    }else{
                                        letters++;
                                    }
                                }
                                break;
                        }
                        //Im calculating SS
                        ss=calculateSS(a1.get(m).length(),choice.length(),letters,valueM);
                        //ss=letters*valueM;
//                      if(ss>max){
                        //Here im getting the biggest ss value and saving his position, the position is for
                        //know what is the shipment that is the owner of that SS
                        if(ss>max){
                            for(int x=0;x<fin.size();x++){
                                c = fin.get(x).split(",");
                                if(!((c[0].equals(a1.get(position).toLowerCase(Locale.ROOT)))&&(c[1].equals(a2.get(p).toLowerCase(Locale.ROOT))))){
                                    max=ss;
                                    position=m;
                                }
                            }

                        }
                        ss=0;
                    }

                    fin.add(a1.get(position).toLowerCase(Locale.ROOT)+","+a2.get(p));
                    Log.d("COMPROBAR",a1.get(position)+","+a2.get(p));
                    //Toast.makeText(MainActivity.this, a1.get(position),Toast.LENGTH_SHORT).show();
                    showFinal(a1.get(position).toLowerCase(Locale.ROOT),a1R);
                }
            }
        });
    }

    //This is the method to calculate SS using the shipment length, drivername length, amount of letters
    //(vowels or consonants) and the value to multiplicate
    public double calculateSS(int shipLength, int driverLength, int letters, double valueM){
        int vmay=0;
        double ss=0;
        //double ss=letters*valueM;
        if(shipLength>driverLength){
            vmay=shipLength;
        }else{
            vmay=driverLength;
        }
        for(int i=2;i<=vmay;i++){
            ss=letters*valueM;
            if((shipLength % i == 0)&&(driverLength % i == 0)){
                ss = ss + 1.5;
            }
        }
        return ss;
    }

    public String cleanText(String choice){
        choice =choice.replaceAll("\\d","");
        choice = choice.replaceAll(" ","");
        choice = choice.toLowerCase(Locale.ROOT);
        return choice;
    }

    public void doNotDuplicateShip(ArrayList<String> a1, ArrayList<String> fin){
        String pal;
        String[] partx;
        for(int j=0;j<a1.size();j++){
            pal = a1.get(j).toLowerCase(Locale.ROOT);

            for(int k=0;k<fin.size();k++){
                partx = fin.get(k).toLowerCase(Locale.ROOT).split(",");
                if(partx[0].equals(pal)){
                    a1.remove(j);
                }
            }
        }
    }
    public void showFinal(String shipment, ArrayList<String> shipmentsArray2){
        String word="";
        for(int j=0;j<shipmentsArray2.size();j++){
            word = shipmentsArray2.get(j);
            word = cleanText(word);
            word = word.replaceAll("\\.","");
            if(shipment.equals(word)){
                Toast.makeText(MainActivity.this,shipmentsArray2.get(j),Toast.LENGTH_SHORT).show();
            }
        }
    }
}