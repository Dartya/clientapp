package ru.plasticworld.clientapp.logic;

import android.os.Handler;

import java.util.Calendar;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import ru.plasticworld.clientapp.App;
import ru.plasticworld.clientapp.activities.Values;
import ru.plasticworld.clientapp.data.remote.Measurement;
import ru.plasticworld.clientapp.data.remote.MeasurementServer;

/**
 * Handler — это механизм, который позволяет работать с очередью сообщений. Он привязан к
 * конкретному потоку (thread) и работает с его очередью. Handler умеет помещать сообщения в
 * очередь.
 * При этом он ставит самого себя в качестве получателя этого сообщения. И когда приходит время,
 * система достает сообщение из очереди и отправляет его адресату (т.е. в Handler) на обработку.
 */
public class MyHandler extends Handler {

    private final int ARDUINO_DATA = 1;
    private String mytext;
    private StringBuilder sb = new StringBuilder();
    private MeasurementServer measurementServer;

    public MyHandler(String mytext, MeasurementServer measurementServer) {
        this.mytext = mytext;
        this.measurementServer = measurementServer;
    }

    /**
     * Обрабатывает сообщения handleMessage. Мы извлекаем из сообщения атрибут what, obj и аргументы
     * типа int. Преобразуем полученное сообщение в строку и выводим его в текстовое поле главного
     * activity: mytext.setText(«Данные от Arduino: » + strIncom)
     */
    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case ARDUINO_DATA:
                byte[] readBuf = (byte[]) msg.obj;
                String strIncom = new String(readBuf, 0, msg.arg1);
                sb.append(strIncom);                                            // формируем строку
                int endOfLineIndex = sb.indexOf("\r\n");                        // определяем символы конца строки
                if (endOfLineIndex > 0) {                                       // если встречаем конец строки,
                    String sbprint = sb.substring(0, endOfLineIndex);           // то извлекаем строку
                    sb.delete(0, sb.length());                                  // и очищаем sb
                    mytext = "" + sbprint;                                      // обновляем TextView
                    Values.temp = sbprint;

                    measurementServer.sendMessage(new Measurement(1L, Calendar.getInstance().getTime().getSeconds(), Double.parseDouble(sbprint)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Response<Void>>() {
                                           @Override
                                           public void accept(Response<Void> sendRes) throws Exception {
                                               System.out.println("ok");
                                           }
                                       },
                                    new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable e) throws Exception {
                                            System.out.println("error");
                                        }
                                    });

                }
                break;
        }
    }

    public int getARDUINO_DATA() {
        return ARDUINO_DATA;
    }
}
