package com.yuan.downloadmanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yuan.library.dmanager.download.DownloadManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private List<TestEntity> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);;

        ButterKnife.bind(this);

        mDatas = getMockData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TestAdapter adapter = new TestAdapter(this, mDatas);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new OnItemTouchListener(mRecyclerView) {
            @Override
            public void onItemLongClick(final RecyclerView.ViewHolder vh) {

                new AlertDialog.Builder(MainActivity.this).setTitle("Prompt").setMessage("Are you sure want to cancel ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = String.valueOf(mDatas.get(vh.getAdapterPosition()).getUrl().hashCode());
                        DownloadManager.getInstance().cancelTask(DownloadManager.getInstance().getTask(id));
                    }
                }).setNegativeButton("NO", null).show();

            }
        });


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
