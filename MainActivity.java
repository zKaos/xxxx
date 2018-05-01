package com.example.slymn.ilk_sql;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;



public class MainActivity extends ListActivity/*AppCompatActivity*/ {
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    boolean connectionstate = false;
    byte BtAdapterSayac = 0;
    Button btnOpen, btnFind;
    EditText MtxtVwState;
    EditText MtxtVwState1;
    TextView durum;
    String sGelenVeri;
    boolean bisChecked = false;


    //
    verikaynagi vk;
     String motor,panel;
    String tarih;


    List<deger> degerler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vk = new verikaynagi(this);
        vk.ac();
        List<deger> degerler = vk.listele();
        final ArrayAdapter<deger> adaptor = new ArrayAdapter<deger>(this, android.R.layout.simple_list_item_1, degerler);
        setListAdapter(adaptor);


        btnOpen = (Button) findViewById(R.id.btnOpen);/*Bluetooth açıp kapatmak buton tanımlıyoruz*/
        btnFind = (Button) findViewById(R.id.btnFind);/*Bluetooth açıldıktan sonra cihazımıza bağlanmak için buton tanımlıyoruz.*/
        // btnAyarlar = (Button) findViewById(R.id.btnSettings);/*Bluetooth ayarları sayfasına gitmek için buton tanımlıyoruz.*/
        //btnSendData = (Button) findViewById(R.id.btnSendData);/*Bluetooth ile veri göndermek için buton tanımlıyoruz.*/
        //edTxt = (EditText) findViewById(R.id.editText);/*Veri girişi almak için edittext tanımlıyoruz.*/
        MtxtVwState = (EditText) findViewById(R.id.MtxtVwState);
        MtxtVwState1 = (EditText) findViewById(R.id.MtxtVwState1);
        durum = (TextView) findViewById(R.id.durum);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bisChecked) {
                    try {
                        openBT();
                        findBT();
                        openBT();
                        findBT();
                        openBT();
                        findBT();
                        durum.setText("Bağlantı Açıldı");
                        bisChecked = true;
                        btnOpen.setText("Kapat");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        closeBT();
                        durum.setText("Bağlantı Kapandı");
                        bisChecked = false;
                        btnOpen.setText("Bt Aç");
                        MtxtVwState.setText("");
                        MtxtVwState1.setText("");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    openBT();
                    findBT();
                    openBT();
                    findBT();
                    openBT();
                    findBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        Button yaz = (Button) findViewById(R.id.button);
        yaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              //String s=(durum.getText().toString());

                Calendar c = Calendar.getInstance();
                SimpleDateFormat myDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                tarih = myDate.format(c.getTime());

            //motor=Integer.parseInt(MtxtVwState.getText().toString());
                deger d;

                d = new deger(motor,panel, tarih);

                vk.degerOlustur(d);
                adaptor.add(d);

                //motor = Integer.parseInt(MtxtVwState.getText().toString());
                //panel = Integer.parseInt(MtxtVwState1.getText().toString());

            }
        });
        Button hesapla = (Button) findViewById(R.id.button2);
        hesapla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s[];
                s= vk.sonuc();
                TextView t=(TextView)findViewById(R.id.durum);
                t.setText("karşılaştırma sonucu:\n"+s[0]+"\n"+s[1]);
            }
        });


    }







    void openBT() throws IOException {

        /*Bluetooth u açıyoruz.*/
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService I
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();/*Bluetooth üzerinden gelen verileri yakalamak için bir listener oluşturuyoruz.*/
        } catch (Exception ignored) {
        }

    }

    /**********************************************************************************************
     * onCreate End
     *********************************************************************************************/
    void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                durum.setText("Bluetooth adaptörü bulunamadı");
            }
            if (BtAdapterSayac == 0) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                    BtAdapterSayac = 1;
                }
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (("HC-06").equals(device.getName().toString())) {/*Eşleşmiş cihazlarda HC-05 adında cihaz varsa bağlantıyı aktişleştiriyoruz. Burada HC-05 yerine bağlanmasını istediğiniz Bluetooth adını yazabilirsiniz.*/
                        mmDevice = device;
                        durum.setText("Bağlantı Bulundu");
                        connectionstate = true;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
    void closeBT() throws IOException {
        try {
            /*Aktif olan bluetooth bağlantımızı kapatıyoruz.*/
            if (mBluetoothAdapter.isEnabled()) {
                stopWorker = true;
                mBluetoothAdapter.disable();
                mmOutputStream.close();
                mmInputStream.close();
                mmSocket.close();
            } else {
            }
        } catch (Exception ignored) {
        }
    }
    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            final byte delimiter = 10; //This is the ASCII code for a newline character




            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (final int[][][] i = {{{0}}}; i[0][0][0] < bytesAvailable; i[0][0][0]++) {
                                    byte b = packetBytes[i[0][0][0]];
                                    if (b == delimiter) {
                                        final byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(readBuffer, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {

                                                sGelenVeri = data.toString();

                                                //sGelenVeri1=data.toString();
                                                if(sGelenVeri.charAt(0)=='0') {
                                                    sGelenVeri = sGelenVeri.substring(1, 4);
                                                    motor=(sGelenVeri);
                                                    MtxtVwState.setText(sGelenVeri);
                                                    // int tut=Integer.parseInt(sGelenVeri);


                                                   // deger d=new deger(tut);








                                                }
                                                else if(sGelenVeri.charAt(0)=='1') {
                                                    sGelenVeri = sGelenVeri.substring(1, 4);
                                                    MtxtVwState1.setText(sGelenVeri);
                                                  // durum.setText(MtxtVwState1.getText().toString());



                                                    //
                                                }

                                                //handler.postDelayed(this,10000);


                                                handler.postDelayed(this,1000);

                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception ignored) {
        }
    }


    //

    protected  void onResume(){
        vk.ac();
        super.onResume();


    }
    protected void onPause(){

        vk.kapat();
        super.onPause();

    }



}
