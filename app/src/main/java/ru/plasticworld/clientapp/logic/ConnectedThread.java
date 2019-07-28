package ru.plasticworld.clientapp.logic;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Отдельный поток для передачи данных
 */
public class ConnectedThread extends Thread{
    private final BluetoothSocket copyBtSocket;
    private final OutputStream OutStrem;
    private final InputStream InStrem;
    private final String logTag;
    private final MyHandler h;

    public ConnectedThread(BluetoothSocket socket, String logTag, MyHandler h){
        this.logTag = logTag;
        copyBtSocket = socket;
        OutputStream tmpOut = null;
        InputStream tmpIn = null;

        try{
            tmpOut = socket.getOutputStream();
            tmpIn = socket.getInputStream();
        } catch (IOException e){
            Log.d(logTag, e.toString());
        }

        OutStrem = tmpOut;
        InStrem = tmpIn;

        this.h = h;
    }

    public void run(){
        byte[] buffer = new byte[1024];
        int bytes;

        while(true){
            try{
                bytes = InStrem.read(buffer);
                h.obtainMessage(h.getARDUINO_DATA(), bytes, -1, buffer).sendToTarget();
            }catch(IOException e){
                Log.d(logTag, e.toString());
                break;
            }
        }
    }

    public void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        Log.d(logTag, "***Отправляем данные: " + message + "***"  );

        try{
            OutStrem.write(msgBuffer);
        } catch(IOException e) {
            Log.d(logTag, e.toString());
        }
    }

    public void cancel(){
        try {
            copyBtSocket.close();
        } catch(IOException e){
            Log.d(logTag, e.toString());
        }
    }

    public Object status_OutStrem(){
        if (OutStrem == null){return null;
        } else {return OutStrem;}
    }
}
