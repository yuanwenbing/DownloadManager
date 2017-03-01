package com.yuan.downloadmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yuan 01/03/2017.
 */

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TestAdapter adapter = new TestAdapter(this, getMockData());
        mRecyclerView.setAdapter(adapter);

    }

    private List<TestEntity> getMockData() {
        List<TestEntity> list = new ArrayList<>();

        for (int i = 0; i < 18; i++) {
            TestEntity entity = new TestEntity();
            entity.setTitle(i + "");
            entity.setUrl("http://192.168.1.94:8888/test" + i + ".dmg");
            list.add(entity);
        }

        return list;

    }
}
