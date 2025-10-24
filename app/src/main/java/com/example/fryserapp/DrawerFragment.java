package com.example.fryserapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fryserapp.data.FreezerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView;
import java.util.Collections;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

public class DrawerFragment extends Fragment {
    private RecyclerView recycler;
    private TextView emptyText;
    private FreezerAdapter adapter;
    private final List<FreezerItem> allItems = new ArrayList<>();

    private Spinner spinnerSort;
    private ImageButton btnSortDirection;
    private boolean ascending = true;
    private String sortBy = "Skuffe";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drawer, container, false);
        recycler = v.findViewById(R.id.recycler);
        emptyText = v.findViewById(R.id.emptyText);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter = new FreezerAdapter(item -> showEditDeleteDialog(item)));

//        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false; // Vi flytter ikke elementer, kun swipe
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                // Swipe afsluttet → vis skraldespandsikon til bekræftelse
//                int position = viewHolder.getAdapterPosition();
//                viewHolder.itemView.setTranslationX(-200); // flyt lidt for visuel feedback
//            }
//
//            @Override
//            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
//                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
//
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//
//                // Tegn rød baggrund med skraldespandsikon
//                View itemView = viewHolder.itemView;
//                Paint paint = new Paint();
//                paint.setColor(Color.RED);
//
//                if (dX < 0) { // Swiper til venstre
//                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
//                            (float) itemView.getRight(), (float) itemView.getBottom(), paint);
//
//                    Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete);
//                    if (icon != null) {
//                        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
//                        int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
//                        int iconRight = itemView.getRight() - iconMargin;
//                        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
//                        int iconBottom = iconTop + icon.getIntrinsicHeight();
//                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//                        icon.draw(c);
//                    }
//                }
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(recycler);


        loadItems();

        spinnerSort = v.findViewById(R.id.spinnerSort);
        btnSortDirection = v.findViewById(R.id.btnSortDirection);

        // Håndtér dropdown
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = parent.getItemAtPosition(position).toString();
                sortItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Håndtér pil (skift retning)
        btnSortDirection.setOnClickListener(view -> {
            ascending = !ascending;
            btnSortDirection.setImageResource(ascending ?
                    R.drawable.ic_arrow_downward : R.drawable.ic_arrow_upward);
            sortItems();
        });

        recycler.setOnTouchListener((y, event) -> {
            // Brugeren trykker et andet sted → fortryd swipe
            for (int i = 0; i < recycler.getChildCount(); i++) {
                recycler.getChildAt(i).setTranslationX(0);
            }
            return false;
        });

        return v;
    }

    private void loadItems() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<FreezerItem> items = com.example.fryserapp.App.db.freezerDao().getAll();
            synchronized (allItems) {
                allItems.clear();
                allItems.addAll(items);
            }
            if (getActivity()==null) return;
            getActivity().runOnUiThread(() -> {
                adapter.submitList(new ArrayList<>(allItems));
                emptyText.setVisibility(allItems.isEmpty()? View.VISIBLE : View.GONE);
            });
        });
    }

    public void showAddDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_item, null);
        EditText name = view.findViewById(R.id.inputName);
        EditText drawer = view.findViewById(R.id.inputDrawer);
        EditText qty = view.findViewById(R.id.inputQty);

        new AlertDialog.Builder(getContext())
                .setTitle("Opret vare")
                .setView(view)
                .setPositiveButton("Opret", (d, which) -> {
                    String n = name.getText().toString().trim();
                    int dr = safeInt(drawer.getText().toString().trim(), 1);
                    if (dr<1) dr=1; if (dr>6) dr=6;
                    String q = qty.getText().toString().trim();
                    FreezerItem item = new FreezerItem(0, n, q, System.currentTimeMillis(), null, dr);
                    Executors.newSingleThreadExecutor().execute(() -> {
                        com.example.fryserapp.App.db.freezerDao().insert(item);
                        loadItems();
                    });
                })
                .setNegativeButton("Annuller", null)
                .show();
    }

    private void showEditDeleteDialog(FreezerItem item) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_item, null);
        EditText name = view.findViewById(R.id.inputName);
        EditText drawer = view.findViewById(R.id.inputDrawer);
        EditText qty = view.findViewById(R.id.inputQty);
        name.setText(item.name);
        drawer.setText(String.valueOf(item.drawer));
        qty.setText(item.quantity==null?"":item.quantity);

        new AlertDialog.Builder(getContext())
                .setTitle("Rediger / Slet")
                .setView(view)
                .setPositiveButton("Gem", (d, w) -> {
                    item.name = name.getText().toString().trim();
                    int dr = safeInt(drawer.getText().toString().trim(), item.drawer);
                    if (dr<1) dr=1; if (dr>6) dr=6;
                    item.drawer = dr;
                    item.quantity = qty.getText().toString().trim();
                    Executors.newSingleThreadExecutor().execute(() -> {
                        com.example.fryserapp.App.db.freezerDao().update(item);
                    });
                    loadItems();

                })
                .setNeutralButton("Slet", (d, w) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        com.example.fryserapp.App.db.freezerDao().delete(item);
                        loadItems();
                    });
                })
                .setNegativeButton("Annuller", null)
                .show();
    }

    private void sortItems() {
        if (allItems.isEmpty()) return;

        Collections.sort(allItems, (a, b) -> {
            int cmp;
            if (sortBy.equals("Navn")) {
                cmp = a.name.compareToIgnoreCase(b.name);
            } else {
                cmp = Integer.compare(a.drawer, b.drawer);
            }
            return ascending ? cmp : -cmp;
        });

        adapter.submitList(new ArrayList<>(allItems));
    }

    private int safeInt(String s, int def) { try { return Integer.parseInt(s); } catch (Exception e) { return def; } }
}
