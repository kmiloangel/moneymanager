package co.camaleon.moneymanager.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.ArrayList;

import co.camaleon.moneymanager.R;
import co.camaleon.moneymanager.adapters.CuentaAdapter;
import co.camaleon.moneymanager.db.CuentaDao;
import co.camaleon.moneymanager.db.DBOperations;
import co.camaleon.moneymanager.models.Cuenta;
import co.camaleon.moneymanager.utils.SwipeableRecyclerViewTouchListener;

/**
 * Created by Usuario on 30/03/2015.
 */
public class CuentaFragment extends Fragment {
    private static final String TAG = CuentaFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private ImageButton newItemIcon;

    private DBOperations dbOperations;

    private ArrayList<Cuenta> cuentas;

    private View fragment;

    public CuentaFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_cuenta, container, false);

        newItemIcon = (ImageButton) fragment.findViewById(R.id.new_icon);
        newItemIconOnClickListener();

        dbOperations = new DBOperations(getActivity());

        return fragment;
    }

    private void newItemIconOnClickListener(){
        newItemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new fragment and transaction
                Fragment newFragment = new NewCuentaFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.frame_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_cuenta);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //LLAMO OBJETOS DE BD
        cuentas = dbOperations.getCuentasList(true);

        if(cuentas.size() == 0){
            FrameLayout fm = (FrameLayout) fragment.findViewById(R.id.fl_no_regs);
            fm.setVisibility(View.VISIBLE);
        }

        //ON ITEM TOUCH LISTENER
        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
                //Toast.makeText(getActivity(), "Tapped " + movimientos.get(position), Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putInt("cuenta_id", cuentas.get(position).getId());

                Fragment newFragment = new NewCuentaFragment();
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.frame_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        };

        // specify an adapter
        mAdapter = new CuentaAdapter(cuentas, getActivity(), itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(swipeToDelete());
    }

    //FUNCIONALIDAD SWIPE TO DELETE
    private RecyclerView.OnItemTouchListener swipeToDelete(){
        //OBJETO PARA REALIZAR DELETE
        final CuentaDao cuentaDao = new CuentaDao();

        SwipeableRecyclerViewTouchListener swipeTouchListener = new SwipeableRecyclerViewTouchListener(mRecyclerView,
                new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipe(int position) {
                        return true;
                    }

                    @Override
                    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            //ELIMINO DE BD
                            Cuenta cuenta = cuentas.get(position);
                            dbOperations.delete(cuentaDao, cuenta.getId());

                            //ELIMINO DE ARRAY LIST
                            cuentas.remove(position);

                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            //ELIMINO DE BD
                            Cuenta cuenta = cuentas.get(position);
                            dbOperations.delete(cuentaDao, cuenta.getId());

                            //ELIMINO DE ARRAY LIST
                            cuentas.remove(position);

                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
        );

        return swipeTouchListener;
    }

    public interface OnItemTouchListener {
        public void onCardViewTap(View view, int position);
    }
}
