/*
 * Copyright (C) 2014 ech0s7r 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.echlabsw.android.drawertab;

import java.util.HashMap;
import java.util.LinkedHashMap;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

public class MainActivity extends FragmentActivity implements
		TabContentFactory, TabHost.OnTabChangeListener,
		ViewPager.OnPageChangeListener, OnItemClickListener {

	public static final String TAG = "DrawerTab";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private ActionBarDrawerToggle mDrawerToggle;

	private ViewPager mViewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar mActionBar;
	private TabHost mTabHost;

	private HorizontalScrollView mHorizontalTabScrollView;

	private HashMap<String, Tab> mTabMap;

	private int mScrollToX;

	private ListAdapter mArrayNavigationDrawerAdapter;

	public static class Tab {
		int index;
		String title;

		Tab(int index, String title) {
			this.index = index;
			this.title = title;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		createStubTab();

		mActionBar = getActionBar();

		mHorizontalTabScrollView = (HorizontalScrollView) findViewById(R.id.hscroll_tab_host);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerListView = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);

		mDrawerToggle = createDrawerToggleListener();

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		for (String tag : mTabMap.keySet()) {
			Tab tab = mTabMap.get(tag);
			TabHost.TabSpec spec = mTabHost.newTabSpec(tag);
			spec.setContent(this);
			spec.setIndicator(tab.title);
			mTabHost.addTab(spec);
		}

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager(),
				mTabMap.size());
		mViewPager.setAdapter(mAdapter);

		mTabHost.setOnTabChangedListener(this);
		mViewPager.setOnPageChangeListener(this);

		mHorizontalTabScrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
					v.performClick();
					break;
				}
				return true;
			}
		});

		mArrayNavigationDrawerAdapter = new ArrayAdapter<Tab>(this,
				android.R.layout.simple_list_item_1, mTabMap.values().toArray(
						new Tab[0]));
		mDrawerListView.setAdapter(mArrayNavigationDrawerAdapter);
		mDrawerListView.setOnItemClickListener(this);
	}

	private void createStubTab() {
		mTabMap = new LinkedHashMap<String, Tab>();
		mTabMap.put("0", new Tab(0, "Tab 0"));
		mTabMap.put("1", new Tab(1, "Tab 1"));
		mTabMap.put("2", new Tab(2, "Tab 2"));
		mTabMap.put("3", new Tab(3, "Tab 3"));
		mTabMap.put("4", new Tab(4, "Tab 4"));
		mTabMap.put("5", new Tab(5, "Tab 5"));
	}

	private ActionBarDrawerToggle createDrawerToggleListener() {
		return new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				mActionBar.setTitle(getString(R.string.app_name));
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				// Set the title on the action when drawer open
				mActionBar.setTitle("Title");
				super.onDrawerOpened(drawerView);
			}
		};
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(mDrawerListView)) {
				mDrawerLayout.closeDrawer(mDrawerListView);
			} else {
				mDrawerLayout.openDrawer(mDrawerListView);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public View createTabContent(String tag) {
		// Stub empty View
		return new View(this);
	}

	@Override
	public void onTabChanged(String tabId) {
		Tab tab = mTabMap.get(tabId);
		selectTab(tab);
	}

	@Override
	public void onPageSelected(int position) {
		mTabHost.setCurrentTab(position);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@TargetApi(13)
	private void centerTabHorizontalScroll() {
		final View currentTabView = mTabHost.getCurrentTabView();
		// int currentTabIndex = mTabHost.getCurrentTab();
		int widthX = getDisplayPxWidth();

		if ((currentTabView.getLeft() + currentTabView.getWidth()) > widthX) {
			mScrollToX = currentTabView.getLeft();

		} else if (currentTabView.getLeft() < (currentTabView.getWidth() + 10)) {
			// 10 maybe margin
			mScrollToX = 0;
		}
		if (mHorizontalTabScrollView != null) {
			mHorizontalTabScrollView.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mHorizontalTabScrollView != null) {
						mHorizontalTabScrollView.scrollTo(mScrollToX, 0);
					}
				}
			}, 100);
		}

	}

	@TargetApi(13)
	@SuppressWarnings("deprecation")
	private int getDisplayPxWidth() {
		Display display = getWindowManager().getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			return size.x;
		} else {
			return display.getWidth();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Tab tab = (Tab) mArrayNavigationDrawerAdapter.getItem(position);
		selectTab(tab);
	}

	private void selectTab(Tab tab) {
		mViewPager.setCurrentItem(tab.index);
		if (mDrawerLayout.isDrawerOpen(mDrawerListView)) {
			mDrawerLayout.closeDrawer(mDrawerListView);
		}

		centerTabHorizontalScroll();
	}

}
