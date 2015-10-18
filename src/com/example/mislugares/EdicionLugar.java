package com.example.mislugares;

import java.text.DateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class EdicionLugar extends Activity {
    private long id;
    private Lugar lugar;
    private EditText nombre;
    private Spinner  tipo;
    private EditText direccion;
    private EditText telefono;
    private EditText url;
    private EditText comentario;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicion_lugar);
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);
        lugar = Lugares.elemento((int) id);
        nombre = (EditText) findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());  
        tipo = (Spinner) findViewById(R.id.tipo);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adaptador);
        tipo.setSelection(lugar.getTipo().ordinal());       
    	direccion = (EditText) findViewById(R.id.direccion);
        direccion.setText(lugar.getDireccion());
        telefono = (EditText) findViewById(R.id.telefono);
        telefono.setText(Integer.toString(lugar.getTelefono()));
    	url = (EditText) findViewById(R.id.url);
        url.setText(lugar.getUrl());
    	comentario = (EditText) findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicion_lugar, menu);            
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.accion_cancelar:
                if(getIntent().getExtras().getBoolean("nuevo", false)) {
                    Lugares.borrar((int) id);
                }
                finish();
                return true;
             	 
            case R.id.accion_guardar:
  	        	lugar.setNombre(nombre.getText().toString());
  	            lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
  	        	lugar.setDireccion(direccion.getText().toString());  	        	  		        	  
  	        	if (telefono.getText().toString().equals("")) {
                    lugar.setTelefono(0);
  	        	} else { 
                    lugar.setTelefono(Integer.parseInt(telefono.getText().toString()));
  	        	}  		        	  
  	        	lugar.setUrl(url.getText().toString());
  	        	lugar.setComentario(comentario.getText().toString());   	            
    	        Lugares.actualizaLugar((int) id, lugar);   	      	  
  	        	finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}