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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	private int totalTabs;

	public TabsPagerAdapter(FragmentManager fm, int totalTabs) {
		super(fm);
		this.totalTabs = totalTabs;
	}

	@Override
	public Fragment getItem(int index) {
		ExampleFragment frag = new ExampleFragment();
		Bundle args = new Bundle();
		args.putInt("index", index);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public int getCount() {
		return totalTabs;
	}
}
