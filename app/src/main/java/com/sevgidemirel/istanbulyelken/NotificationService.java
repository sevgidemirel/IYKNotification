package com.sevgidemirel.istanbulyelken;

/**
 * Created by Dell on 15.4.17.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dell on 14.4.17.
 */


public class NotificationService extends Service {


    ProgressDialog progressDialog;

    String ilkHaberBasligi,ilkLink;
    final static long ZAMAN = 10000;
    Timer zamanlayici;
    Handler yardimci;
    SharedPreferences veri;
    final static String filename ="file";



    public class FetchTitle extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(NotificationService.this);
          //  progressDialog.setTitle("BAŞLIK");
            //progressDialog.setMessage("Başlık Çekiliyor...");
            progressDialog.setIndeterminate(false);
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect("http://www.istanbulyelken.org.tr/").get();
                Elements element = doc.getElementsByClass("s_three_columns");
                Elements link = element.select("a[href]");
                Elements baslik = element.select("h4.mt16");

                int i = 0;
                String baslikDizisi[] = new String[baslik.size()];

                if (baslik != null) {
                    for (Element b : baslik) {
                        baslikDizisi[i++] = b.text();
                    }
                    ilkHaberBasligi = baslikDizisi[0];
                }

                int j=0;
                String LinkDizisi[]= new String[link.size()];

                if(link!=null){
                    for(Element l:link){

                        LinkDizisi[j++]=l.attr("abs:href");

                    }
                ilkLink = LinkDizisi[0];
                }



            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            veri = getSharedPreferences(filename,0);

            super.onPostExecute(aVoid);
            SharedPreferences.Editor editor = veri.edit();

            String data = veri.getString("haber","deger yok");
            if(data.equals(ilkHaberBasligi)){

                return;

            }else{
                editor.remove("haber");
                editor.putString("haber",ilkHaberBasligi);
                editor.commit();
                String haber = veri.getString("haber","deger yok");

                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

                Notification.Builder builder = new Notification.Builder(NotificationService.this);
                builder.setContentTitle("İstanbul Yelken Klubü");
                builder.setContentText(ilkHaberBasligi).setStyle(new Notification.BigTextStyle());
               builder.setSmallIcon(R.drawable.ic_sailing);
                builder.setAutoCancel(true);

               // Intent intent = new Intent(NotificationService.this,MainActivity.class);
                //PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this,1,intent,0);
                //builder.setContentIntent(pendingIntent);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(ilkLink));
                PendingIntent pending = PendingIntent.getActivity(NotificationService.this,1,intent,0);

                builder.setContentIntent(pending);

                Notification notification = builder.getNotification();

                notification.flags |= Notification.FLAG_AUTO_CANCEL;//notificationa tıklanınca notificationın otomatik silinmesi için
                notification.defaults |= Notification.DEFAULT_SOUND;//notification geldiğinde bildirim sesi çalması için
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                manager.notify(1,notification);

            }
            progressDialog.dismiss();
        }


    }


    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        zamanlayici=new Timer();
        yardimci=new Handler(Looper.getMainLooper());

        zamanlayici.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                bilgiVer();
            }
        },0,ZAMAN );
    }

    @Override
    public void onDestroy(){
        zamanlayici.cancel();
        super.onDestroy();
        Intent broadcastIntent = new Intent(".ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);


    }

    private void bilgiVer() {


        yardimci.post(new Runnable() {
            @Override
            public void run() {

                new FetchTitle().execute();
            }
        });

    }
}


