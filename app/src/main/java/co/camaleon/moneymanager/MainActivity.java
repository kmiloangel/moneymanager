package co.camaleon.moneymanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


//import com.loopj.android.http.JsonHttpResponseHandler;
//import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import co.camaleon.moneymanager.adapters.NavDrawerListAdapter;
import co.camaleon.moneymanager.db.DBOperations;
import co.camaleon.moneymanager.db.MovimientoDao;
import co.camaleon.moneymanager.fragments.CategoriaFragment;
import co.camaleon.moneymanager.fragments.CuentaFragment;
import co.camaleon.moneymanager.fragments.HomeFragment;
import co.camaleon.moneymanager.fragments.MovimientoFragment;
import co.camaleon.moneymanager.models.Categoria;
import co.camaleon.moneymanager.models.Cuenta;
import co.camaleon.moneymanager.utils.ConstantsUtils;
//import co.camaleon.moneymanager.utils.HttpUtils;
import co.camaleon.moneymanager.utils.NavDrawerItem;
//import cz.msebera.android.httpclient.entity.mime.Header;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    //nav drawer
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuItems;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuItems    = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuTitles   = getResources().getStringArray(R.array.nav_drawer_titles);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList();

        // agregar un nuevo item al menu deslizante
        //home
        navDrawerItems.add(new NavDrawerItem(navMenuItems[0], navMenuIcons.getResourceId(0, -1)));
        // movimientos
        navDrawerItems.add(new NavDrawerItem(navMenuItems[1], navMenuIcons.getResourceId(1, -1)));
        // categorias
        navDrawerItems.add(new NavDrawerItem(navMenuItems[2], navMenuIcons.getResourceId(2, -1)));
        // cuentas
        navDrawerItems.add(new NavDrawerItem(navMenuItems[3], navMenuIcons.getResourceId(3, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                null, //toolbar
                R.mipmap.ic_drawer, // nav drawer open - description for accessibility
                R.mipmap.ic_drawer // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
            //displayView(1);//MOVIMIENTO
        }

        /*TEST*/
        backupdDatabase();
        importDatabase();

        //REST TEST
        //restTest();
    }
    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    //RIGHT ACTION BAR MENU->OVERFLOW MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //RIGHT ACTION BAR MENU->OVERFLOW MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar actions click
        /*
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
        return super.onOptionsItemSelected(item);
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new MovimientoFragment();
                break;
            case 2:
                fragment = new CategoriaFragment();
                break;
            case 3:
                fragment = new CuentaFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e(TAG, "Error cuando se creo el fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private void backupdDatabase() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "co.camaleon.moneymanager"
                        + "//databases//" + "moneymanager.db";
                String backupDBPath = "/moneymanager/moneymanager-backup.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getApplicationContext(), "Backup Successful!",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Backup Failed! " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();

        }
    }


    public void importDatabase(){
        /*ContentValues values = new ContentValues();
        DBOperations dbOperations = new DBOperations(this);

        //Categoria categoria = null;
        //int categoria_id = DBOperations.saveNewCategoria(dbOperations, "Categoría", "", categoria);
        int categoria_id = 2;

        //Cuenta cuenta = null;
        //int cuenta_id = DBOperations.saveNewCuenta(dbOperations, "Cuenta", "", cuenta);
        int cuenta_id = 2;

        for(int i = 0; i < 10; i++) {
            values.clear();
            values.put("cuenta_id", cuenta_id);
            values.put("categoria_id", categoria_id);
            values.put("egreso", 1);
            values.put("fecha", "2017-09-22");
            values.put("valor", "40000");
            values.put("descripcion", "Prueba 2");

            dbOperations.insertOrIgnore(values, new MovimientoDao());
        }*/


        /*try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "co.camaleon.moneymanager"
                        + "//databases//" + "moneymanager.db";
                String backupDBPath  = "/moneymanager.db";
                File  backupDB= new File(data, currentDBPath);
                File currentDB  = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }*/
    }

    /*public void restTest(){
        // make HTTP request to retrieve the weather
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                weatherWebserviceURL, null, new Response.Listener() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Parsing json object response
                    // response will be a json object


                    jsonObj = (JSONObject) response.getJSONArray("weather").get(0);
                    // display weather description into the "description textview"
                    description.setText(jsonObj.getString("description"));
                    // display the temperature
                    temperature.setText(response.getJSONObject("main").getString("temp") + " °C");

                    String backgroundImage = "";

                    //choose the image to set as background according to weather condition
                    if (jsonObj.getString("main").equals("Clouds")) {
                        backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/clouds-wallpaper2.jpg";
                    } else if (jsonObj.getString("main").equals("Rain")) {
                        backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/rainy-wallpaper1.jpg";
                    } else if (jsonObj.getString("main").equals("Snow")) {
                        backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/snow-wallpaper1.jpg";
                    }

                    // load image from link and display it on background
                    // We'll use the Glide library
                    Glide
                            .with(getApplicationContext())
                            .load(backgroundImage)
                            .centerCrop()
                            .crossFade()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target target, boolean isFirstResource) {
                                    System.out.println(e.toString());
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(weatherBackground);

                    // hide the loading Dialog
                    pDialog.dismiss();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error , try again ! ", Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("tag", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error while loading ... ", Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                pDialog.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance(this).addToRequestQueue(jsonObjReq);
    }*/


}
