package com.example.tarrito.notasapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.tarrito.notasapp.R;
import com.example.tarrito.notasapp.adapters.MyAdapter;
import com.example.tarrito.notasapp.models.Nota;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Nota>>{

    private MyAdapter adapter;
    private GridView gridView;
    final Context context = this;
    String notas, notas2, auxtext;
    int posicion, nuevapos, auxcolor;

    private Realm realm;
    private RealmResults<Nota> nota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        gridView = (GridView) findViewById(R.id.gridView);

        nota = getAllNota();
        nota.addChangeListener(this);

        adapter = new MyAdapter(nota, R.layout.grid_item, this);

        gridView.setAdapter(adapter);
        registerForContextMenu(gridView);
    }

    @Override
    public void onChange(RealmResults<Nota> element) {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        realm.removeAllChangeListeners();
        realm.close();
        super.onDestroy();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(this.nota.get(info.position).getid() + "");
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.delete_item:
                realm.beginTransaction();
                nota.deleteFromRealm(info.position); // App crash
                realm.commitTransaction();
                return true;
            case R.id.change_item: {
                // get prompts.xml view
                posicion = info.position;
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.popup, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        notas2 = String.valueOf(userInput.getText());

                                        realm.beginTransaction();
                                        nota.get(posicion).setNota(notas2);
                                        realm.commitTransaction();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


                return true;
            }case R.id.change_color:



                realm.beginTransaction();
                //int i = nota.get(info.position).getid();
                int i;
                for (i = 0; i <nota.size(); i++)
                {
                    nota.get(i).setColor(0);
                }
                nota.get(info.position).setColor(1);
                realm.commitTransaction();

                return true;

            case R.id.change_position:

                posicion = info.position;
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.popup_position, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editText);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Cambiar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        // nuevapos = Integer.parseInt(userInput.getText().toString());
                                        nuevapos = Integer.valueOf(userInput.getText().toString());
                                        int tamaño = nota.size();

                                        if(nuevapos > tamaño){
                                            Toast.makeText(MainActivity.this, "Error de posición", Toast.LENGTH_SHORT).show();
                                        }else{
                                            if (nuevapos >= 1) {


                                                realm.beginTransaction();
                                                auxcolor = nota.get(nuevapos-1).getColor();
                                                auxtext = nota.get(nuevapos-1).getNota();
                                                nota.get(nuevapos-1).setColor(nota.get(posicion).getColor());
                                                nota.get(nuevapos-1).setNota(nota.get(posicion).getNota());
                                                nota.get(posicion).setColor(auxcolor);
                                                nota.get(posicion).setNota(auxtext);
                                                realm.commitTransaction();
                                            }
                                            else{
                                                Toast.makeText(MainActivity.this, "Error de posición", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
                //aqui ocurre la magia!

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_add:
                addNota();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private RealmResults<Nota> getAllNota() {
        return realm.where(Nota.class).findAll();
    }


    private void addNota() {
        // get prompts.xml view

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                notas = String.valueOf(userInput.getText());

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {


                                        Nota n1 = new Nota(notas, 0);


                                        realm.copyToRealmOrUpdate(n1);


                                        nota = getAllNota();
                                    }
                                });
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

}
