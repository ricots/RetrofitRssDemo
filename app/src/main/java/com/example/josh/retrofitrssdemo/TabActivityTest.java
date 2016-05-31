package com.example.josh.retrofitrssdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Josh on 5/20/2016.
 */
public class TabActivityTest extends AppCompatActivity {

    PagerAdapter pagerAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // change back to setAdapter(pagerAdapter) if fail
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));


        tabLayout.setupWithViewPager(viewPager);

        //viewPager.getAdapter().notifyDataSetChanged();

        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Should I be using notifyItemChanged? notifyItemRangeChanged?
                viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class PagerAdapter extends FragmentStatePagerAdapter {
        Fragment fragment = null;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FirstFragment();
                case 1:
                    return new SecondFragment();
                default:
                    return new FirstFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Recent Activity";
            } else if (position == 1){
                return "Saved Bills";
            }
            return null;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }
}