package com.example.mislugares;

public class GeoPunto {  
    private int longitud, latitud;   

    public GeoPunto(double longitud, double latitud){
        setLongitud(longitud);
        setLatitud(latitud);          
    }

    public GeoPunto(int  longitud, int latitud){
        setLongitud(longitud);
        setLatitud(latitud);        
    }
    
    public double getLongitud() {
        return (double) (longitud / 1E6);
    }

    public void setLongitud(double longitud) {
        this.longitud = (int) (longitud * 1E6);
    }

    public double getLatitud() {
        return (double) (latitud / 1E6);      
    }

    public void setLatitud(double latitud) {    
        this.latitud = (int) (latitud * 1E6);
    }

    public String toString(){
        return "(" + this.getLongitud() + "," + this.getLatitud() + ")";
    }
    
    // Metodo para aproximar la distancia en metros entre dos coordenadas   
    public double distancia(GeoPunto punto){
        final double RADIO_TIERRA = 6371000; // En metros
        double dLat = Math.toRadians(latitud - punto.latitud) / 1E6;
        double dLon = Math.toRadians(longitud - punto.longitud) / 1E6;
        double lat1 = Math.toRadians(punto.latitud) / 1E6;
        double lat2 = Math.toRadians(latitud) / 1E6;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.sin(dLon / 2) * Math.sin(dLon / 2) * 
            Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * RADIO_TIERRA;
    }
}
