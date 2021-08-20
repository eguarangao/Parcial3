package com.example.ml_kit_1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.*;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageView imageView;
    Button enviar;
    TextView MultiLineText;
    TextView editText;
    RequestQueue rq;
    String nombrePais;
    PolylineOptions optionsPoly;
    GoogleMap mapa;
    LatLng puntosLatLng;
    int con;
    String lugares;
    private String URL="https://restcountries.eu/rest/v2/name/"+nombrePais+"?fullText=true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView2);

///informacion pais
        MultiLineText = findViewById(R.id.MultiLine);
        MultiLineText.setMovementMethod(new ScrollingMovementMethod());
///informacion pais
        rq = Volley.newRequestQueue(this);
        //mlkit
        imageView = findViewById(R.id.imagen);
        //llena el img en el txt
        editText = findViewById(R.id.txtPais);
        enviar = findViewById(R.id.enviar);
        System.out.println("hola ebert que haces"+ editText.getText() );
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(
                i,"Seleccionar Imagen"),121);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==121){
            imageView.setImageURI(data.getData());
            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();

                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {
                                // Task completed successfully
                                // ...
                                editText.setText(result.getText());
                                System.out.println(editText.getText());
                                nombrePais = editText.getText().toString();
                                System.out.println(nombrePais);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ///informacion pais
    private void getVoleyPais(){
        JsonArrayRequest dataVolley = new JsonArrayRequest(
                Request.Method.GET,"https://restcountries.eu/rest/v2/name/"+nombrePais+"?fullText=true",null, response -> {
            int longitud = response.length();
            for(int i = 0; i < longitud; i++)
            {
                try {
                    JSONObject data = new JSONObject(response.get(i).toString());
                    SpannableString myTextNnme = new SpannableString("name: " + data.get("name")+"\n");
                    SpannableString myTextCapital = new SpannableString("topLevelDomain: " + data.get("topLevelDomain")+"\n");
                    SpannableString myTextGeoRectangle = new SpannableString("alpha3Code: " + data.get("alpha3Code") + "\n");
                    SpannableString myTextSeqId = new SpannableString("capital: " + data.get("capital") + "\n");
                    SpannableString myTextDateGeoId = new SpannableString("region: " + data.get("region") + "\n");
                    SpannableString myTextCountryCodes = new SpannableString("latlng: " + data.get("latlng") + "\n");
                    SpannableString myTextCountryInfo = new SpannableString("flag: " + data.get("flag") + "\n");


                    MultiLineText.append( myTextNnme);
                    MultiLineText.append( myTextCapital);
                    MultiLineText.append( myTextGeoRectangle);
                    MultiLineText.append(  myTextSeqId);
                    MultiLineText.append( myTextDateGeoId);
                    MultiLineText.append( myTextCountryCodes);
                    MultiLineText.append( myTextCountryInfo);
                      latEbert = myTextCountryCodes.toString();

                    System.out.println("I"+latEbert+"I");
                    System.out.println(latEbert);
                }
                catch (JSONException e) {
                    String msj = "Mensaje de error: " + e.getMessage();
                    MultiLineText.setText(msj);
                }
            }
        },
                error -> {
                    String msj = "Mensaje de error por Voley: " + error.getMessage();
                    MultiLineText.setText(msj);
                }
        );
        rq.add(dataVolley);
    }
    private  String latEbert;

    public void btnAceptar_Click(View view)
    {
            Toast.makeText(this, "Cargando.....", Toast.LENGTH_SHORT).show();
            getVoleyPais();


    }


    @Override
    public void onMapReady( GoogleMap googleMap) {
        this.optionsPoly = new PolylineOptions();
        this.mapa = googleMap;
        this.con = 1;

        this.puntosLatLng = new LatLng(2, 2);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                marcarPunto(latLng);
            }

        });


    }
    public void marcarPunto(LatLng punto){
        this.mapa.addMarker(new MarkerOptions().position(punto).title("Punto " + this.con++));
        this.optionsPoly.add(punto);
    }


    public void Eliminar(View view){
        this.con = 0;
        this.mapa.clear();
        this.optionsPoly = new PolylineOptions();
    }

}