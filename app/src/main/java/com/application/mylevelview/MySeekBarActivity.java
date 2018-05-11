/*
 * Copyright (C) 2018 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.application.mylevelview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglu on 2018/5/10.
 */

public class MySeekBarActivity extends Activity {

    private MyLevelView mylevlview;
    private EditText edittext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myseekbar_activity);
        mylevlview = (MyLevelView) findViewById(R.id.mylevlview);
        edittext = (EditText) findViewById(R.id.edittext);
        initData();
    }

    private void initData() {
        List<MyLevelView.LevelTextBean> mDataList = new ArrayList<>();
        mDataList.add(new MyLevelView.LevelTextBean("大众会员",0,10));
        mDataList.add(new MyLevelView.LevelTextBean("白银会员",20,20));
        mDataList.add(new MyLevelView.LevelTextBean("黄金会员",40,40));
        mDataList.add(new MyLevelView.LevelTextBean("铂金会员",80,80));
        mDataList.add(new MyLevelView.LevelTextBean("钻石会员",100));
        mylevlview.setData(mDataList);
    }

    public void onProgress(View view) {
        String trim = edittext.getText().toString().trim();
        mylevlview.setCurrentProgress(Integer.parseInt(trim));
    }
}
