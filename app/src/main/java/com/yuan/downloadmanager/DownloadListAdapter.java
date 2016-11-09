package com.yuan.downloadmanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yuan.library.db.download.DownloadManager;
import com.yuan.library.db.download.DownloadTask;
import com.yuan.library.db.download.DownloadTaskListener;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.yuan.library.db.download.DownloadStatus.DOWNLOAD_ERROR_FILE_NOT_FOUND;
import static com.yuan.library.db.download.DownloadStatus.DOWNLOAD_STATUS_CANCEL;
import static com.yuan.library.db.download.DownloadStatus.DOWNLOAD_STATUS_CONNECTING;
import static com.yuan.library.db.download.DownloadStatus.DOWNLOAD_STATUS_ERROR;
import static com.yuan.library.db.download.DownloadStatus.DOWNLOAD_STATUS_FINISH;
import static com.yuan.library.db.download.DownloadStatus.DOWNLOAD_STATUS_INIT;
import static com.yuan.library.db.download.DownloadStatus.DOWNLOAD_STATUS_PAUSE;
import static com.yuan.library.db.download.DownloadStatus.DOWNLOAD_STATUS_START;

/**
 * Created by Yuan on 9/19/16:2:31 PM.
 * <p/>
 * Description:com.yuan.downloadmanager.ListAdapter
 */

class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.CViewHolder> {

    private Context mContext;

    private List<MockEntity> mListData;

    private DownloadManager mDownloadManager;


    DownloadListAdapter(Context context, List<MockEntity> list) {
        mContext = context;
        mListData = list;
        mDownloadManager = DownloadManager.getInstance();
    }

    @Override
    public CViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new CViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CViewHolder holder, final int position) {

        final MockEntity entity = mListData.get(holder.getAdapterPosition());
        holder.titleView.setText(entity.getTitle());
        holder.itemView.setTag(mListData.get(holder.getAdapterPosition()).getUrl());
        DownloadTask itemTask = mDownloadManager.getTask(String.valueOf(mListData.get(holder.getAdapterPosition()).getUrl().hashCode()));

        if (itemTask == null) {
            holder.downloadButton.setText(R.string.start);
            holder.progressView.setText("0");
            holder.progressBar.setProgress(0);
        } else {
            int status = itemTask.getDownloadStatus();
            String progress = getDownLoadPercent(itemTask.getCompletedSize(), itemTask.getTotalSize());
            switch (status) {
                case DOWNLOAD_STATUS_INIT:
                    int state = itemTask.isFinish() ? R.string.start : R.string.resume;
                    holder.downloadButton.setText(state);
                    holder.progressBar.setProgress(Integer.parseInt(progress));
                    holder.progressView.setText(progress);
                    break;
                case DOWNLOAD_STATUS_CONNECTING:
                    holder.downloadButton.setText(R.string.connecting);
                    holder.progressBar.setProgress(Integer.parseInt(progress));
                    holder.progressView.setText(progress);
                    break;
                case DOWNLOAD_STATUS_START:
                    holder.downloadButton.setText(R.string.pause);
                    holder.progressBar.setProgress(Integer.parseInt(progress));
                    holder.progressView.setText(progress);
                    break;
                case DOWNLOAD_STATUS_PAUSE:
                    holder.downloadButton.setText(R.string.resume);
                    holder.progressBar.setProgress(Integer.parseInt(progress));
                    holder.progressView.setText(progress);
                    break;
                case DOWNLOAD_STATUS_FINISH:
                    holder.downloadButton.setText(R.string.delete);
                    holder.progressBar.setProgress(Integer.parseInt(progress));
                    holder.progressView.setText(progress);
                    break;
                case DOWNLOAD_STATUS_ERROR:
                    holder.downloadButton.setText(R.string.retry);
                    holder.progressBar.setProgress(Integer.parseInt(progress));
                    holder.progressView.setText(progress);
                    break;
                case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                    if (BuildConfig.DEBUG) Log.d("DownloadListAdapter", "error");
                    break;
            }
        }


        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadTask itemTask = mDownloadManager.getTask(String.valueOf(mListData.get(holder.getAdapterPosition()).getUrl().hashCode()));

                if (itemTask == null) {
                    itemTask = new DownloadTask.Builder().setId(entity.getUrl().hashCode() + "").setUrl(entity.getUrl()).setDownloadStatus(0).setSaveDirPath("").setFileName("").build();
                    responseUIListener(itemTask, holder);
                    mDownloadManager.add(itemTask);
                } else {
                    responseUIListener(itemTask, holder);
                    int status = itemTask.getDownloadStatus();
                    switch (status) {
                        case DOWNLOAD_STATUS_INIT:
                            mDownloadManager.add(itemTask);
                            break;
                        case DOWNLOAD_STATUS_CONNECTING:
                            mDownloadManager.pause(itemTask);
                            break;
                        case DOWNLOAD_STATUS_START:
                            mDownloadManager.pause(itemTask);
                            break;
                        case DOWNLOAD_STATUS_CANCEL:
                            mDownloadManager.add(itemTask);
                            break;
                        case DOWNLOAD_STATUS_PAUSE:
                            mDownloadManager.resume(itemTask);
                            break;
                        case DOWNLOAD_STATUS_FINISH:
                            mDownloadManager.cancel(itemTask);
                            break;
                        case DOWNLOAD_STATUS_ERROR:
                            mDownloadManager.add(itemTask);
                            break;
                    }
                }
            }
        });
    }

    private String getDownLoadPercent(Long completedSize, Long totalSize) {
        if (totalSize > 0) {
            double fen = ((double) completedSize / (double) totalSize) * 100;
            DecimalFormat df1 = new DecimalFormat("0");
            return df1.format(fen);
        }
        return "0";
    }

    private void responseUIListener(final DownloadTask itemTask, final CViewHolder holder) {

        itemTask.setListener(new DownloadTaskListener() {

            @Override
            public void onConnecting(DownloadTask downloadTask) {
                if (holder.itemView.getTag().equals(itemTask.getUrl())) {
                    holder.downloadButton.setText(R.string.connecting);
                }
            }

            @Override
            public void onStart(DownloadTask downloadTask, long completedSize, long totalSize, String percent) {
                if (holder.itemView.getTag().equals(itemTask.getUrl())) {
                    holder.downloadButton.setText(R.string.pause);
                    holder.progressBar.setProgress(Integer.parseInt(percent));
                    holder.progressView.setText(percent);
                }

            }

            @Override
            public void onPause(DownloadTask downloadTask, long completedSize, long totalSize, String percent) {
                if (holder.itemView.getTag().equals(itemTask.getUrl())) {
                    holder.downloadButton.setText(R.string.resume);
                }
            }

            @Override
            public void onCancel(DownloadTask downloadTask) {
                if (holder.itemView.getTag().equals(itemTask.getUrl())) {
                    holder.downloadButton.setText(R.string.start);
                    holder.progressView.setText("0");
                    holder.progressBar.setProgress(0);
                }
            }

            @Override
            public void onFinish(DownloadTask downloadTask, File file) {
                if (holder.itemView.getTag().equals(itemTask.getUrl())) {
                    holder.downloadButton.setText(R.string.delete);
                }
            }

            @Override
            public void onError(DownloadTask downloadTask, int errorCode) {
                if (holder.itemView.getTag().equals(itemTask.getUrl())) {
                    holder.downloadButton.setText(R.string.retry);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mListData.size();
    }

    class CViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_title)
        TextView titleView;
        @BindView(R.id.list_item_progress_bar)
        ProgressBar progressBar;
        @BindView(R.id.list_item_progress_text)
        TextView progressView;
        @BindView(R.id.list_item_state_button)
        Button downloadButton;

        CViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
