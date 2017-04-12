# AppBase
1. 主要是学习Retrofit框架，使用OKHttpClient和Gson解析

2. 理解execute和enqueue两种方式(同步和异步)

3. 添加拦截器，给每个请求加入公共的参数，以及Logging的拦截器打印请求过程中的日志

   ```java
   private OkHttpClient getClient() {
           OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
           //添加一个网络的拦截器
           clientBuilder.addInterceptor(new Interceptor() {
               @Override
               public okhttp3.Response intercept(Chain chain) throws IOException {
                   Request originalRequest = chain.request();
                   Request request;
                   if (commonParamMap.size()>0) {//如果公共参数个数大约0，生成新的request
                       HttpUrl originalHttpUrl = originalRequest.url();
                       HttpUrl.Builder builder = originalHttpUrl.newBuilder();
                       for (String key : commonParamMap.keySet()) {
                           builder.addQueryParameter(key, commonParamMap.get(key));
                       }
                       builder.addQueryParameter("timeStamp", String.valueOf(System.currentTimeMillis()));
                       request = originalRequest.newBuilder()
                               .url(builder.build())
                               .method(originalRequest.method(), originalRequest.body())
                               .build();
                   } else {
                       request = originalRequest;
                   }
                   return chain.proceed(request);
               }
           });
           //添加log拦截器
           loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
           clientBuilder.addInterceptor(loggingInterceptor);
           return clientBuilder.build();
       }
   ```

   ​

4. RxJava的引入，流式编程让代码逻辑更优雅

   ```java
   NetManager.get().getInstallDeceDetailRx()
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Consumer<BaseResponse<List<InstallNeceModel>>>() {
                       @Override
                       public void accept(@NonNull BaseResponse<List<InstallNeceModel>> listBaseResponse) throws Exception {
                           Timber.d("getInstallDeceDetail onNext size = %d", listBaseResponse.entity.size());
                       }
                   });
   ```

   ​

5. 使用RxJava的连接符和转换符，将两个请求串联

   ```java
   NetManager.get().checkUpgradeRx(5800, "LETV_X443")
                   .subscribeOn(Schedulers.io())
                   .observeOn(Schedulers.io())
                   .flatMap(new Function<BaseResponse<UpgradeModel>, ObservableSource<BaseResponse<List<InstallNeceModel>>>>() {//IO线程，由observeOn()指定
                       @Override
                       public ObservableSource<BaseResponse<List<InstallNeceModel>>> apply(
                               @NonNull BaseResponse<UpgradeModel> upgradeModelBaseResponse) throws Exception {
                           return NetManager.get().getInstallDeceDetailRx();
                       }
                   })
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Consumer<BaseResponse<List<InstallNeceModel>>>() {//Android主线程，由observeOn()指定
                       @Override
                       public void accept(@NonNull BaseResponse<List<InstallNeceModel>> listBaseResponse) throws Exception {
                           Timber.d("getInstallDeceDetail onNext size = %d", listBaseResponse.entity.size());
                       }
                   });
   ```

   ```java
   /**
            * request1结束后使用request1的结果请求request2，request2结果是list，对list进行分解输出
            */
           NetManager.get().checkUpgradeRx(5800, "LETV_X443")
                   .subscribeOn(Schedulers.io())
                   .doOnSubscribe(new Consumer<Disposable>() {
                       @Override
                       public void accept(@NonNull Disposable disposable) throws Exception {
                           Toast.makeText(MainActivity.this, "Begin!!!", Toast.LENGTH_SHORT).show();
                       }
                   })
                   .subscribeOn(AndroidSchedulers.mainThread())
                   .observeOn(Schedulers.io())
                   .flatMap(new Function<BaseResponse<UpgradeModel>, Observable<BaseResponse<List<InstallNeceModel>>>>() {//IO线程，由observeOn()指定
                       @Override
                       public Observable<BaseResponse<List<InstallNeceModel>>> apply(
                               @NonNull BaseResponse<UpgradeModel> upgradeModelBaseResponse) throws Exception {
                           return NetManager.get().getInstallDeceDetailRx();
                       }
                   })
                   .observeOn(Schedulers.io())
                   .flatMap(new Function<BaseResponse<List<InstallNeceModel>>, Observable<InstallNeceModel>>() {
                       @Override
                       public Observable<InstallNeceModel> apply(@NonNull BaseResponse<List<InstallNeceModel>> listBaseResponse) throws Exception {
                           return Observable.fromIterable(listBaseResponse.entity);
                       }
                   })
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Consumer<InstallNeceModel>() {
                       @Override
                       public void accept(@NonNull InstallNeceModel installNeceModel) throws Exception {
                           Timber.d("test7 onNext model.name = %s", installNeceModel.name);
                       }
                   }, new Consumer<Throwable>() {
                       @Override
                       public void accept(@NonNull Throwable throwable) throws Exception {
                           Timber.d("Error!");
                       }
                   }, new Action() {
                       @Override
                       public void run() throws Exception {
                           Toast.makeText(MainActivity.this, "End!!", Toast.LENGTH_SHORT).show();
                       }
                   });
   ```

   ​

6. 引入Lambda，让代码更简洁

   ```java
    /**
            * request1结束后使用request1的结果请求request2，request2结果是list，对list进行分解输出
            * 使用lambda, 添加在开始执行的时候显示begin的提示(可以是showProgressBar)，在结束(Complete)的时候显示End的提示(隐藏ProgressBar)
            */
           NetManager.get().checkUpgradeRx(5800, "LETV_X443")
                   .subscribeOn(Schedulers.io())
                   .doOnSubscribe((@NonNull Disposable disposable) -> Toast.makeText(this, "Begin!!!", Toast.LENGTH_SHORT).show())
                   .subscribeOn(AndroidSchedulers.mainThread())
                   .observeOn(Schedulers.io())
                   .flatMap((@NonNull BaseResponse<UpgradeModel> upgradeModelBaseResponse) -> NetManager.get().getInstallDeceDetailRx())
                   .observeOn(Schedulers.io())
                   .flatMap((@NonNull BaseResponse<List<InstallNeceModel>> listBaseResponse) -> Observable.fromIterable(listBaseResponse.entity))
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(installNeceModel -> Timber.d("test7 onNext model.name = %s", installNeceModel.name),
                           (Throwable throwable) -> Timber.d("test8 error %s", throwable.getMessage()),
                           () -> Toast.makeText(this, "End!!", Toast.LENGTH_SHORT).show());
   ```

   ​

7. MVP架构

8. 配置文件

   ```groovy
   buildscript {
       repositories {
           jcenter()
           mavenCentral()
       }
       dependencies {
           classpath 'com.android.tools.build:gradle:2.3.0'
           classpath 'me.tatarka:gradle-retrolambda:3.6.0'
       }
   }

   allprojects {
       repositories {
           jcenter()
           mavenCentral()
       }
   }

   task clean(type: Delete) {
       delete rootProject.buildDir
   }
   ```

   ```groovy
   apply plugin: 'com.android.application'
   apply plugin: 'me.tatarka.retrolambda'

   android {
       compileSdkVersion 25
       buildToolsVersion "25.0.2"
       defaultConfig {
           applicationId "me.jasonzhang.netmodel"
           minSdkVersion 15
           targetSdkVersion 21
           versionCode 1
           versionName "1.0"
       }
       buildTypes {
           release {
               minifyEnabled false
               proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
           }
       }

       compileOptions {
           sourceCompatibility JavaVersion.VERSION_1_8
           targetCompatibility JavaVersion.VERSION_1_8
       }
   }
   dependencies {
       compile fileTree(include: ['*.jar'], dir: 'libs')
       compile 'com.squareup.retrofit2:retrofit:2.2.0'
       compile 'com.squareup.retrofit2:converter-gson:2.2.0'
       compile 'com.jakewharton.timber:timber:4.5.1'
       compile 'com.squareup.okhttp3:logging-interceptor:3.6.0'
       compile 'io.reactivex.rxjava2:rxjava:2.0.8'
       compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
       compile 'com.squareup.retrofit2:adapter-rxjava2:2.2.0'
       compile 'com.android.support:support-v4:25.3.1'
       compile 'com.jakewharton:butterknife:8.5.1'
       annotationProcessor 'com.jakewharton:butterknife-compiler:8.5.1'
   }
   ```

   ​