package ru.plasticworld.clientapp;

import android.app.Application;

import ru.plasticworld.clientapp.data.remote.MeasurementServer;

public class App extends Application {

    public static App instance;
    private MeasurementServer measurementServer;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        measurementServer = new MeasurementServer();
    }

    public static App getInstance() {
        return instance;
    }

    public MeasurementServer getMeasurementServer() {
        return measurementServer;
    }
}
