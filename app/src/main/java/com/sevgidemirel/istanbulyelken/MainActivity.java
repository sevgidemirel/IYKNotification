package com.sevgidemirel.istanbulyelken;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    TextView mTxtViewilkhaberBasligi,mTxtViewikincihaberBasligi,mTxtViewuchaberBasligi;
    ProgressDialog progressDialog;
    Button mbtnYenile;
    String ilkHaberBasligi,ikinciHaberBasligi,ucuncuHaberBasligi;
    String firstLink,secondLink,thirdLink;
    final static String filename ="dosya";
    SharedPreferences veri;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    //arka plan ile ana işlemciyi ayırıyor
    public class FetchTitle extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("İYK");
            progressDialog.setMessage("En son haberler listeleniyor..");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect("http://www.istanbulyelken.org.tr/").get();
                Elements element = doc.select("section.s_three_columns");
                Elements baslik = element.select("h4.mt16");
                Elements link = element.select("a[href]");


                int i = 0;
                String baslikDizisi[] = new String[baslik.size()];

                if (baslik != null) {
                    for (Element b : baslik) {
                        baslikDizisi[i++] = b.text();
                    }
                    ilkHaberBasligi = baslikDizisi[0];
                    ikinciHaberBasligi = baslikDizisi[1];
                    ucuncuHaberBasligi = baslikDizisi[2];
                }

                int j=0;
                String LinkDizisi[]= new String[link.size()];

                if(link!=null){
                    for(Element l:link) {

                        LinkDizisi[j++] = l.attr("abs:href");
                    }
                    firstLink = LinkDizisi[0];
                    secondLink = LinkDizisi[1];
                    thirdLink = LinkDizisi[2];

                }


                    } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if(internetKontrol()){
            super.onPostExecute(aVoid);
//burada mobil bildirimler görüntülenir...


            mTxtViewilkhaberBasligi.setText(ilkHaberBasligi);
            mTxtViewikincihaberBasligi.setText(ikinciHaberBasligi);
            mTxtViewuchaberBasligi.setText(ucuncuHaberBasligi);



            progressDialog.dismiss();}else{
                Toast.makeText(getApplicationContext(),"İnternet Bağlantınızı Kontrol Edin..", Toast.LENGTH_SHORT).show();

            }
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtViewilkhaberBasligi=(TextView)findViewById(R.id.ilkText);
        mTxtViewikincihaberBasligi=(TextView)findViewById(R.id.ikinciText);
        mTxtViewuchaberBasligi=(TextView)findViewById(R.id.ucuncuText);
        mbtnYenile=(Button)findViewById(R.id.btnYenile);

        mbtnYenile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(internetKontrol()){
                    new FetchTitle().execute();

            }else{
                    Toast.makeText(getApplicationContext(),"İnternet Bağlantınızı Kontrol Edin..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(internetKontrol()) {

            mTxtViewilkhaberBasligi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri adress = Uri.parse(firstLink);
                    Intent browser = new Intent(Intent.ACTION_VIEW, adress);
                    startActivity(browser);

                }
            });


            mTxtViewikincihaberBasligi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri adress = Uri.parse(secondLink);
                    Intent browser = new Intent(Intent.ACTION_VIEW, adress);
                    startActivity(browser);

                }
            });


            mTxtViewuchaberBasligi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri adress = Uri.parse(thirdLink);
                    Intent browser = new Intent(Intent.ACTION_VIEW, adress);
                    startActivity(browser);

                }
            });
        }


            if(internetKontrol()){
            new FetchTitle().execute();}



        final Button btnServis = (Button)findViewById(R.id.btnServis);


        btnServis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!internetKontrol()){
                    Toast.makeText(getApplicationContext(),"İnternet Bağlantınızı Kontrol Edin..", Toast.LENGTH_SHORT).show();
                    return;
                } else{

                if(servisCalisiyormu()){
                    Intent intent = new Intent(getApplicationContext(),NotificationService.class);
                    stopService(intent);//servisi durdurur
                    btnServis.setText(R.string.baslat);
                    Toast.makeText(getApplicationContext(),"Bildirim Özelliği Kapalı", Toast.LENGTH_SHORT).show();

                }else{
                    Intent intent = new Intent(getApplicationContext(),NotificationService.class);
                    startService(intent);//Servisi başlatır
                    btnServis.setText(R.string.durdur);
                    Toast.makeText(getApplicationContext(),"Bildirim Özelliği Aktif", Toast.LENGTH_SHORT).show();
                }}
            }
        });




        if(servisCalisiyormu()){
            btnServis.setText(getString(R.string.durdur));



        }else{
            btnServis.setText(getString(R.string.baslat));

        }

       // mBtnHaberBasligi = (Button) findViewById(R.id.btn_haber_basligi);

      //  mBtnHaberBasligi.setOnClickListener(new View.OnClickListener() {
     //       @Override
     //       public void onClick(View view) {
    }


    //Bu metod çağrıldığında eğer servisimiz çalışıyorsa geriye true döndürülecek. Eğer çalışmıyorsa false döndürülecek.

    // Bunun için bir ActivityManager nesnesi oluşturup sistemin etkinlik servisini alıyoruz.
    //
    // Java'nın for döngüsü yapısını kullanarak getRunningServices() metodu ile sistemde çalışan tüm servisleri
    // alıyoruz. Bu metodun döndürdüğü RunningServiceInfo nesnelerinden oluşan dizi içinde servis isimli değişken
    // ile dönüyoruz. Döngü sırasındaki servis nesnesinin paket adı, bizim uygulamamızın paket adına eşitse bizim
    // servisimiz çalışıyor demektir. Bu mantığı kullanarak tüm çalışan servisleri geziyoruz ve bizim servisimiz
    // çalışıyor mu kontrol ediyoruz.

    private boolean servisCalisiyormu(){

        ActivityManager servisYoneticisi = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo servis: servisYoneticisi.getRunningServices(Integer.MAX_VALUE)){
            if(getApplication().getPackageName().equals(servis.service.getPackageName())){
                return true;
            }
        }
        return false;
    }



    protected boolean internetKontrol() { //interneti kontrol eden method
        // TODO Auto-generated method stub
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }



}


