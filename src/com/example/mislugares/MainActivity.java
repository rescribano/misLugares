package com.example.mislugares;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class MainActivity extends ListActivity implements LocationListener {

	public BaseAdapter adaptador;
	private LocationManager manejador;
	private Location mejorLocaliz;	
	private static final long DOS_MINUTOS = 2 * 60 * 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Lugares.indicializaBD(this);
		adaptador = new AdaptadorCursorLugares(this, Lugares.listado());
		setListAdapter(adaptador);
		manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
		    actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.GPS_PROVIDER));
		}
		if (manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
		    actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true; /** true -> el menu ya esta visible */
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
	        case R.id.acercaDe:
	            lanzarAcercaDe(null);
	            break;

	        case R.id.config:
		        lanzarPreferencias(null);
		        break;
	       
	        case R.id.menu_mapa:
		        Intent i = new Intent(this, Mapa.class);	       
		        startActivityForResult(i, 0);
		        break;
		     
	        case R.id.accion_nuevo:
	            int id = Lugares.nuevo();           	           
	            Intent intent = new Intent(this, EdicionLugar.class);	            
	            intent.putExtra("id", (long) id); 	            	                
	            startActivityForResult(intent, 0);
	            break;
		}
		return true; /** true -> consumimos el item, no se propaga*/
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}
	
	// Metodos asociados al onClick del Layout activity_main.xml
	public void lanzarAcercaDe(View view){
	    Intent i = new Intent(this, AcercaDe.class);
	    startActivity(i);
	}
	
	public void salir(View view){
		finish();
	}
	
	public void lanzarPreferencias(View view){
	    Intent i = new Intent(this, Preferencias.class);
	    startActivity(i);
	}	
	
	public void mostrarPreferencias(View view){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String s = "notificaciones: " + pref.getBoolean("notificaciones",true)
            + ", distancia m’nima: " + pref.getString("distancia","?");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	
	@Override 
	protected void onListItemClick(ListView listView, View vista, int posicion, long id) {
	    super.onListItemClick(listView, vista, posicion, id);
	    Intent intent= new Intent(this, VistaLugar.class);
	    intent.putExtra("id", id);	   
	    startActivityForResult(intent, 0);
	}
		
	@Override 
	protected void onResume() {
	    super.onResume();
	    activarProveedores();
	}

	private void activarProveedores() { 
	    if(manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  
	      	manejador.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20 * 1000, 5, this);
	    }

	    if(manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	      	manejador.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, 10, this);
	    }
	} 

	@Override 
	protected void onPause() {
	    super.onPause();
	    manejador.removeUpdates(this);
	}
	
	@Override 
	public void onLocationChanged(Location location) {
	    Log.d(Lugares.TAG, "Nueva localizaci—n: " + location);
	    actualizaMejorLocaliz(location);
	}	 

	@Override 
	public void onProviderDisabled(String proveedor) {
	    Log.d(Lugares.TAG, "Se deshabilita: " + proveedor);
	    activarProveedores();
	}

	@Override    
	public void onProviderEnabled(String proveedor) {
	    Log.d(Lugares.TAG, "Se habilita: " + proveedor);
	    activarProveedores();
	}

	@Override
	public void onStatusChanged(String proveedor, int estado, Bundle extras) {
	    Log.d(Lugares.TAG, "Cambia estado: " + proveedor);
	    activarProveedores();
	}
	
	private void actualizaMejorLocaliz(Location localiz) {
	    if (localiz != null) {
	        if (mejorLocaliz == null
	            || localiz.getAccuracy() < 2*mejorLocaliz.getAccuracy()
	            || localiz.getTime() - mejorLocaliz.getTime() > DOS_MINUTOS) {
	            Log.d(Lugares.TAG, "Nueva mejor localizacion");
	            mejorLocaliz = localiz;
	            Lugares.posicionActual.setLatitud(localiz.getLatitude());
	            Lugares.posicionActual.setLongitud(localiz.getLongitude());
	        }
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    ListView listView = (ListView) findViewById(android.R.id.list);
	    AdaptadorCursorLugares adaptador = (AdaptadorCursorLugares) listView.getAdapter();
	    adaptador.changeCursor(Lugares.listado());
	}		
}
