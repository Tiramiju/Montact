package com.example.montact;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ContactFragment contactFragment;
    private BusinessCardFragment businessCardFragment;
    private CalendarFragment calendarFragment;

    private MaterialToolbar materialToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        materialToolbar =findViewById(R.id.top_appbar);

        TabLayout tabLayout = findViewById(R.id.tabs_layout);
        tabLayout.addOnTabSelectedListener(onTabSelectedListener);

        fragmentManager = getSupportFragmentManager();
        contactFragment = new ContactFragment();
        businessCardFragment = new BusinessCardFragment();
        calendarFragment = new CalendarFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout,contactFragment).commitAllowingStateLoss();

        materialToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.calendar_view:
                    fragmentTransaction.replace(R.id.main_layout, calendarFragment).commitAllowingStateLoss();
            }
            return false;
        }
    };

    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            doReplace(tab);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            doReplace(tab);
        }
    };

    private void doReplace(TabLayout.Tab tab) {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (tab.getPosition()) {
            case 0:
                fragmentTransaction.replace(R.id.main_layout,contactFragment).commitAllowingStateLoss();
                break;
            case 1:
                fragmentTransaction.replace(R.id.main_layout,businessCardFragment).commitAllowingStateLoss();
                break;
        }
    }
}