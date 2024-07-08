package com.example.mygrub.user.home.welfare;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.mygrub.R;
import com.example.mygrub.user.home.food.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrgList extends AppCompatActivity {

    private RecyclerView orgView;
    private ArrayList<OrgItem> itemList;
    private OrgAdapter adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welfare_org_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        orgView = findViewById(R.id.orgView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.recommend_org_label);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        orgView.setLayoutManager(layoutManager);

        itemList = getIntent().getParcelableArrayListExtra("itemList");

        adapter = new OrgAdapter(OrgList.this, itemList);
        orgView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OrgAdapter.OnItemClickListener() {
            @Override
            public void onTitleClick(OrgItem item) {
                copyText(item.getKey(), "Organization name");
            }

            @Override
            public void onAddressClick(OrgItem item) {
                copyText(item.getAddress(), "Address");
            }

            @Override
            public void onContactClick(OrgItem item) {
                copyText(item.getContact(), "Contact");
            }

            @Override
            public void onLinkClick(OrgItem item) {
                copyText(item.getLink(), "Link");
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(item.getLink())));
            }
        });
    }

    private void copyText(String info, String label) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, info);
        clipboard.setPrimaryClip(clip);
        makeToast(label + " has been copied!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_back_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.back_icon) {
            startActivity(new Intent(OrgList.this, OrgMain.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}