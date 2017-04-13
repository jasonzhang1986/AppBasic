# AppBase
1. 主要是学习Retrofit框架，使用OKHttpClient和Gson解析

2. 理解execute和enqueue两种方式(同步和异步)

3. 通过baseUrl来查询NetManager,每个NetManager中有对应的retrofit和loggingInterceptor实例

   ```java
       private Retrofit retrofit;
       private HttpLoggingInterceptor loggingInterceptor;
       private Map<String, String> commonParamMap = null;
       private static ConcurrentHashMap<String, NetManager> sInstanceMap = new ConcurrentHashMap<>();
       private NetManager() {
       }
       public static NetManager get(String baseUrl) {
           NetManager manager = sInstanceMap.get(baseUrl);
           if (manager==null) {
               synchronized (NetManager.class) {
                   manager = sInstanceMap.get(baseUrl);
                   if (manager==null) {
                       manager = new NetManager();
                       manager.createRetrofit(baseUrl);
                       sInstanceMap.put(baseUrl, manager);
                   }
               }
           }
           return manager;
       }

       public <T> T getApiService(Class<T> cls) {
           return retrofit.create(cls);
       }
   ```

   ​

4. RxJava的引入，流式编程让代码逻辑更优雅

   ```java
       @Override
       public void testRxJava() {
           mSubscriptions.add(mApiService.getAndroidData(5, 1)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Consumer<BaseResponse<List<GankBean>>>() {
                       @Override
                       public void accept(@io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> gankBean) throws Exception {
                           LoggerUtils.d("checkUpgradeRx onNext %s", gankBean.results.get(0).desc);
                           mMainView.setResultText("testRxJava onNext desc = " + gankBean.results.get(0).desc);
                       }
                   }));
       }
   ```

   ​

5. 使用RxJava的连接符和转换符，将两个请求串联

   ```java
   @Override
       public void testComplex() {
           /**
            * request1结束后使用request1的结果请求request2，request2结果是list，对list进行分解输出
            */
           mSubscriptions.add(mApiService.getAndroidData(5, 1)
                   .subscribeOn(Schedulers.io())
                   .doOnSubscribe(new Consumer<Disposable>() {
                       @Override
                       public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws Exception {
                            mMainView.showBegin("Complex invoke Begin!!!");
                       }
                   })
                   .subscribeOn(AndroidSchedulers.mainThread())
                   .observeOn(Schedulers.io())
                   .flatMap(new Function<BaseResponse<List<GankBean>>, Observable<BaseResponse<List<GankBean>>>>() {//IO线程，由observeOn()指定
                       @Override
                       public Observable<BaseResponse<List<GankBean>>> apply(
                               @io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> upgradeModelBaseResponse) throws Exception {
                           return mApiService.getIOSData(3,1);
                       }
                   })
                   .observeOn(Schedulers.io())
                   .flatMap(new Function<BaseResponse<List<GankBean>>, Observable<GankBean>>() {
                       @Override
                       public Observable<GankBean> apply(@io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> listBaseResponse) throws Exception {
                           return Observable.fromIterable(listBaseResponse.results);
                       }
                   })
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Consumer<GankBean>() {
                       @Override
                       public void accept(@io.reactivex.annotations.NonNull GankBean gankBean) throws Exception {
                           LoggerUtils.d("testComplex onNext gankBean.desc = %s", gankBean.desc);
                           mMainView.setResultText("testComplex onNext "+ gankBean.desc);
                       }
                   }, new Consumer<Throwable>() {
                       @Override
                       public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                           LoggerUtils.d("Error!");
                       }
                   }, new Action() {
                       @Override
                       public void run() throws Exception {
                            mMainView.showBegin("Complex invoke Complete!!!");
                       }
                   }));
       }
   ```

   ​

6. 引入Lambda，让代码更简洁

   ```java
       @Override
       public void testComplexUseLambda() {
           /**
            * request1结束后使用request1的结果请求request2，request2结果是list，对list进行分解输出
            * 使用lambda, 添加在开始执行的时候显示begin的提示(可以是showProgressBar)，在结束(Complete)的时候显示End的提示(隐藏ProgressBar)
            */
           mSubscriptions.add(mApiService.getAndroidData(5, 1)
                   .subscribeOn(Schedulers.io())
                   .doOnSubscribe((@io.reactivex.annotations.NonNull Disposable disposable) -> mMainView.showBegin("ComplexUseLambda invoke Begin!!!"))
                   .subscribeOn(AndroidSchedulers.mainThread())
                   .observeOn(Schedulers.io())
                   .flatMap((@io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> upgradeModelBaseResponse) -> mApiService.getIOSData(3, 1))
                   .observeOn(Schedulers.io())
                   .flatMap((@io.reactivex.annotations.NonNull BaseResponse<List<GankBean>> listBaseResponse) -> Observable.fromIterable(listBaseResponse.results))
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(gankBean -> {
                               LoggerUtils.d("testComplexUseLambda onNext bean.desc = %s", gankBean.desc);
                               mMainView.setResultText("testComplexUseLambda onNext bean.desce = " + gankBean.desc);
                           },
                           (Throwable throwable) -> LoggerUtils.d("test8 error %s", throwable.getMessage()),
                           () ->  mMainView.showEnd("ComplexUseLambda invoke Complete!!!")));
       }
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