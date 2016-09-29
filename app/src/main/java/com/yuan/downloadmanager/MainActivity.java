package com.yuan.downloadmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DownloadListAdapter adapter = new DownloadListAdapter(this, getMockData());
        mRecyclerView.setAdapter(adapter);


    }

    private List<MockEntity> getMockData() {
        List<MockEntity> list = new ArrayList<>();

        for(int i = 'a'; i<'o'; i++) {
            MockEntity entity = new MockEntity();
            char c = (char) i;
            String s = String.valueOf(c);
            entity.setTitle(s.toUpperCase());
            entity.setUrl("http://192.168.1.193:8888/test." + s + s + s);
            list.add(entity);
        }

        return list;

    }
}
