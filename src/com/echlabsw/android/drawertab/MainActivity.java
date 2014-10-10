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

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
		ViewPager.OnPageChangeListener, OnItemClickListener,
		ExampleFragment.OnRefreshContentListener, LoaderCallbacks<JSONObject> {

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

	private Menu mOptionsMenu;

	private static final boolean TAB_SCROLL_ENABLED = true;

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
		Log.i(TAG, "onCreate");

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

		if (!TAB_SCROLL_ENABLED) {
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
		}

		mArrayNavigationDrawerAdapter = new ArrayAdapter<Tab>(this,
				android.R.layout.simple_list_item_1, mTabMap.values().toArray(
						new Tab[0]));
		mDrawerListView.setAdapter(mArrayNavigationDrawerAdapter);
		mDrawerListView.setOnItemClickListener(this);

		/* Create AsyncTaskLoader */
		getSupportLoaderManager().initLoader(0, null, this);
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

	@Override
	public void onAttachFragment(Fragment fragment) {
		Log.i(TAG, "onAttachFragment");
		super.onAttachFragment(fragment);
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();

	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
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
		mOptionsMenu = menu;
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

	@Override
	public void onRefreshFragmentContent(final SwipeRefreshLayout swipeLayout) {
		showProgressLoading();
		AsyncTaskLoader<JSONObject> loader = (AsyncTaskLoader<JSONObject>) getSupportLoaderManager()
				.<JSONObject> getLoader(0);
		if (loader != null) {
			loader.stopLoading();
			loader.forceLoad();
		} else {
			Log.e(TAG, "loader is null!");
		}
	}

	private void showProgressLoading() {
		if (mOptionsMenu != null) {
			final MenuItem item = mOptionsMenu.findItem(R.id.action_refresh);
			if (item != null) {
				item.setActionView(R.layout.actionbar_indeterminate_progress);
			}
		}
	}

	private void dismissProgressLoading() {
		SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		if (swipe != null) {
			swipe.setRefreshing(false);
		}
		if (mOptionsMenu != null) {
			MenuItem item = mOptionsMenu.findItem(R.id.action_refresh);
			if (item != null) {
				item.setActionView(null);
			}
		}
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		Log.i(TAG, "onRetainCustomNonConfigurationInstance");
		return super.onRetainCustomNonConfigurationInstance();
	}

	/**
	 * Instantiate and return a new Loader for the given ID.
	 */
	@Override
	public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
		Log.i(TAG, "onCreateLoader");
		return new AsyncTaskLoader<JSONObject>(this) {

			Thread loaderThread;

			@Override
			public JSONObject loadInBackground() {
				try {
					loaderThread = Thread.currentThread();
					for (int i = 0; i < 10; i++) {
						Log.i(TAG, "[thread: " + Thread.currentThread().getId()
								+ "] loadInBackground: " + i);
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					Log.i(TAG, "Thread " + Thread.currentThread().getId()
							+ " interrupted");
				}
				return null;
			}

			@Override
			public void onCanceled(JSONObject data) {
				Log.i(TAG, "onCanceled");
				if (loaderThread != null) {
					loaderThread.interrupt();
				}
				super.onCanceled(data);
			}

			/**
			 * Run in UI Thread
			 */
			@Override
			protected void onStopLoading() {
				Log.i(TAG, "onStopLoading");
				if (loaderThread != null) {
					loaderThread.interrupt();
				}
				super.onStopLoading();
			}

			@Override
			protected void onAbandon() {
				Log.i(TAG, "onAbandon");
				super.onAbandon();
			}

			@Override
			protected void onReset() {
				Log.i(TAG, "onReset");
				super.onReset();
			}

			@Override
			protected void onStartLoading() {
				super.onStartLoading();
				showProgressLoading();
			}

		};
	}

	/**
	 * Called when a previously created loader has finished its load.
	 */
	@Override
	public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
		Log.i(TAG, "onLoadFinished");
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				dismissProgressLoading();
			}
		});
	}

	/**
	 * Called when a previously created loader is being reset, and thus making
	 * its data unavailable.
	 */
	@Override
	public void onLoaderReset(Loader<JSONObject> loader) {
		Log.i(TAG, "onLoaderReset");
	}

}
