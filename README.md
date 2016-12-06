# DownloadManager

# Introduce 
1、基于OkHttp实现的下载管理，支持多线程、断点续传等功能，Demo演示用的是RecyclerView，在Application里可设置任务并发等。
2、实现时基本无别的臃肿代码，可以方便修改使用。


# Demo
[Download demo apk](https://github.com/yuanwenbing/DownloadManager/raw/master/apk/app-debug.apk)


# Screenshot
![Alt text](https://raw.githubusercontent.com/yuanwenbing/DownloadManager/master/captures/2016-10-19%2011_43_33.gif "Optional title")
# Use
## Gradle

```
compile 'com.yuan.library.dmanager:downloadmanager-okhttp-release:1.0.2'
```
## Maven

```
<dependency>
  <groupId>com.yuan.library.dmanager</groupId>
  <artifactId>downloadmanager-okhttp-release</artifactId>
  <version>1.0.2</version>
  <type>pom</type>
</dependency>
```


# Code
```
// 在Application初始化
DownloadManager.getInstance().init(this, 3);

DownloadTask itemTask = mDownloadManager.getTask(String.valueOf(mListData.get(holder.getAdapterPosition()).getUrl().hashCode()));
DownloadManager.getInstance().add(itemTask);

```

# Remark
由于demo中下载地址，是在本机上搭的服务器，所以点击下载按钮不能下载。如果demo中的下载测试，请在MainActivity的getMockData方法中换成可用的地址。
# Contacts
mail:wenbing1007@163.com

qq:11026979



