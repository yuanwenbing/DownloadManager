# DownloadManager

# Introduce 
1、基于OkHttp实现的下载管理，支持多线程、断点续传等功能，Demo演示用的是RecyclerView，在Application里可设置任务并发等。
2、实现时基本无别的臃肿代码，可以方便修改使用。


# Demo
[Download demo apk](https://github.com/yuanwenbing/DownloadManager/raw/master/apk/app-debug.apk)


# Screenshot
![Alt text](https://raw.githubusercontent.com/yuanwenbing/DownloadManager/master/captures/demo.gif "Optional title")
# Use
## Gradle

```
compile 'com.yuan.library.dmanager:downloadmanager-okhttp-release:1.1.0'
```
## Maven

```
<dependency>
  <groupId>com.yuan.library.dmanager</groupId>
  <artifactId>downloadmanager-okhttp-release</artifactId>
  <version>1.1.0</version>
  <type>pom</type>
</dependency>
```


# Code
```
// 在Application初始化
DownloadManager.getInstance().init(this, 3);

// 代码中使用
String taskId = String.valueOf(mListData.get(holder.getAdapterPosition()).getUrl().hashCode());
DownloadTask itemTask = mDownloadManager.getTask(taskId);
DownloadManager.getInstance().add(itemTask);

//下载管理具体使用，也需要对列表的adapter进行处理，具体实现请参考demo。
```
#Update 
1.1.0：重构了下载管理，删除冗余代码，并添加任务失败的回调。修复了已知bug。具体使用，请参考demo。
       

# Remark
由于demo中下载地址，是在本机上搭的服务器，所以点击下载按钮不能下载。如果demo中的下载测试，请在MainActivity的getMockData方法中换成可用的地址。
# Contacts
mail:wenbing1007@163.com

qq:11026979



