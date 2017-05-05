/*
 * Copyright (C) 2016 hejunlin <hejunlin2013@gmail.com>
 * Github:https://github.com/hejunlin2013/TVSample
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
package me.jasonzhang.app.module.metro;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import me.jasonzhang.app.R;
import me.jasonzhang.app.widget.MetroViewBorderImpl;

/**
 * Metro Demo入口类
 */
public class MetroActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_metro);

        FrameLayout roundedFrameLayout = new FrameLayout(this);
        roundedFrameLayout.setClipChildren(false);

        final MetroViewBorderImpl metroViewBorderImpl = new MetroViewBorderImpl(roundedFrameLayout);
//        metroViewBorderImpl.setBackgroundResource(R.drawable.border_color);
        metroViewBorderImpl.setBackgroundResource(R.drawable.home_focus);
        metroViewBorderImpl.getViewBorder().setScale(1.2f);
        metroViewBorderImpl.getViewBorder().setMargin(40);
        ViewGroup list = (ViewGroup) findViewById(R.id.list);
        metroViewBorderImpl.attachTo(list);

    }


}
