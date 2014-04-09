/*  LS Vertretungsplan - Android-App für den Vertretungsplan der Lornsenschule Schleswig
    Copyright (C) 2014  Johan v. Forstner

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see [http://www.gnu.org/licenses/]. */

package com.johan.vertretungsplan.ui;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.ViewPager;

import android.os.Bundle;
import android.os.Parcelable;


import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.util.DisplayMetrics;

import android.view.ViewGroup;
import android.view.ViewParent;

import com.johan.vertretungsplan.R;
  
public abstract class TabSwipeActivity extends Activity {
  
    protected ViewPager mViewPager;
    public TabsAdapter adapter;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {   	  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	/*
         * Create the ViewPager and our custom adapter
         */
        mViewPager = (ViewPager) findViewById(R.id.pager);
        
        if (savedInstanceState == null) {
        	adapter = new TabsAdapter( this, mViewPager );
        } else {
        	Parcelable adapterState = savedInstanceState.getParcelable("TabsAdapter");
        	adapter = new TabsAdapter( this, mViewPager );
        	adapter.restoreState(adapterState, this.getClassLoader());
        }
        
        mViewPager.setAdapter( adapter );   
    }
    
    @Override
    protected void onSaveInstanceState(Bundle state) {
    	state.putParcelable("TabsAdapter", adapter.saveState());
    	super.onSaveInstanceState(state);
    }
  
    /**
     * Add a tab with a backing Fragment to the action bar
     * @param titleRes A string resource pointing to the title for the tab
     * @param fragmentClass The class of the Fragment to instantiate for this tab
     * @param args An optional Bundle to pass along to the Fragment (may be null)
     */
    protected void addTab(int titleRes, Class<? extends Fragment> fragmentClass, Bundle args ) {
        adapter.addTab( getString( titleRes ), fragmentClass, args );
    }
    /**
     * Add a tab with a backing Fragment to the action bar
     * @param titleRes A string to be used as the title for the tab
     * @param fragmentClass The class of the Fragment to instantiate for this tab
     * @param args An optional Bundle to pass along to the Fragment (may be null)
     */
    protected void addTab(CharSequence title, Class<? extends Fragment> fragmentClass, Bundle args ) {
        adapter.addTab( title, fragmentClass, args );
    }
  
  public class TabsAdapter extends FragmentPagerAdapter implements TabListener, ViewPager.OnPageChangeListener {
  
        private final Activity mActivity;
        private final ActionBar mActionBar;
        private final ViewPager mPager;
  
        /**
         * @param fm
         * @param fragments
         */
        public TabsAdapter(Activity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            this.mActivity = activity;
            this.mActionBar = activity.getSupportActionBar();
            this.mPager = pager;
  
        }
  
        private class TabInfo {
            public final Class<? extends Fragment> fragmentClass;
            public final Bundle args;
            public TabInfo(Class<? extends Fragment> fragmentClass,
                    Bundle args) {
                this.fragmentClass = fragmentClass;
                this.args = args;
            }
        }
  
        private List<TabInfo> mTabs = new ArrayList<TabInfo>();
        private List<CharSequence> mTitles = new ArrayList<CharSequence>();
  
        public void addTab( CharSequence title, Class<? extends Fragment> fragmentClass, Bundle args ) {
            final TabInfo tabInfo = new TabInfo( fragmentClass, args );
  
//            Tab tab = mActionBar.newTab();
//            tab.setText( title );
//            tab.setTag( tabInfo );
  
            mTabs.add( tabInfo );
            mTitles.add( title );
  
            //mActionBar.addTab( tab );
            
            notifyDataSetChanged();
        }
        
        @Override
        public float getPageWidth(int position){
        	DisplayMetrics metrics = new DisplayMetrics();
        	getWindowManager().getDefaultDisplay().getMetrics(metrics);
            if (metrics.widthPixels / (metrics.densityDpi / 160f) >= 800 && getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
                return 0.5f;
            } else {
                return super.getPageWidth(position);
            }
        }
  
        @Override
        public Fragment getItem(int position) {
            final TabInfo tabInfo = mTabs.get( position );
            return (Fragment) Fragment.instantiate( mActivity, tabInfo.fragmentClass.getName(), tabInfo.args );
        }
  
        @Override
        public int getCount() {
            return mTabs.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
        	return mTitles.get(position);
        }
        
  
        public void onPageScrollStateChanged(int arg0) {
        }
  
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
  
        @Override
        public void onPageSelected(int position)
        {
           ViewParent root = findViewById(android.R.id.content).getParent();
           findAndUpdateSpinner(root, position);
        }

        /**
         * Searches the view hierarchy excluding the content view 
         * for a possible Spinner in the ActionBar. 
         * 
         * @param root The parent of the content view
         * @param position The position that should be selected
         * @return if the spinner was found and adjusted
         */
        private boolean findAndUpdateSpinner(Object root, int position)
        {
           if (root instanceof android.widget.Spinner)
           {
              // Found the Spinner
              Spinner spinner = (Spinner) root;
              spinner.setSelection(position);
              return true;
           }
           else if (root instanceof ViewGroup)
           {
              ViewGroup group = (ViewGroup) root;
              if (group.getId() != android.R.id.content)
              {
                 // Found a container that isn't the container holding our screen layout
                 for (int i = 0; i < group.getChildCount(); i++)
                 {
                    if (findAndUpdateSpinner(group.getChildAt(i), position))
                    {
                       // Found and done searching the View tree
                       return true;
                    }
                 }
              }
           }
           // Nothing found
           return false;
        }
  
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
        	/*
        	 * Slide to selected fragment when user selected tab
        	 */
            TabInfo tabInfo = (TabInfo) tab.getTag();
            for ( int i = 0; i < mTabs.size(); i++ ) {
                if ( mTabs.get( i ) == tabInfo ) {
                    mPager.setCurrentItem( i );
                }
            }
        }
  
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }
  
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }
}
