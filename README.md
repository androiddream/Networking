# Networking 

基于OkHttp3、Retrofit2和RxJava2的Android APP开发API请求基本网络框架，主要功能如下：
- loading view 自动显示和隐藏；
- 取消loading view 时自动取消当前网络请求；
- 方便灵活统一添加Header参数；
- 网络返回统一封装，每次API请求只需关心API正确返回的数据；
- 网络错误更加方便统一处理。

其中使用的第三方开源库：
- ![OkHttp3](https://github.com/square/okhttp)
- ![Retrofit2](https://github.com/square/retrofit)
- ![RxJava2](https://github.com/ReactiveX/RxJava)

在此感谢以上开源库作者们的贡献！

### 使用方式
1. 第一步，在项目的根目录下的build.gradle文件中添加依赖仓库：
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2. 第二步，在module目录下的build.gradle文件中添加本库最新版本：
```
	dependencies {
	        compile 'com.github.ihsg:Networking:1.0'
	}
```
3. 第三步，集成该库
- 继承BaseUrl类并添加生产和测试BaseUrl，示例如下：
```
public class GitHubBaseUrl extends BaseUrl {
    private static final String GIT_HUB_BASE_URL = "https://api.github.com/";

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public String getOnlineBaseUrl() {
        return null;
    }

    @Override
    public String getOfflineBaseUrl() {
        return null;
    }

    @Override
    public String getOnlineRestBaseUrl() {
        return GIT_HUB_BASE_URL;
    }

    @Override
    public String getOfflineRestBaseUrl() {
        return null;
    }

    @Override
    public String getOnlineH5BaseUrl() {
        return null;
    }

    @Override
    public String getOfflineH5BaseUrl() {
        return null;
    }

    @Override
    public String getOnlineSiteBaseUrl() {
        return null;
    }

    @Override
    public String getOfflineSiteBaseUrl() {
        return null;
    }
}
```
- 继承BaseNetWorker类，并通过builder方式添加网络相关设置，示例如下：
```
public class GitHubNetWorker extends BaseNetWorker {
    private static final String TAG = "GitHubNetWorker";

    private static GitHubBaseUrl gitHubBaseUrl;

    public static GitHubApi createGitHubApiService() {
        BaseNetWorker.Builder builder = new GitHubNetWorker().getBuilder()
                .setBaseUrl(getGitHubBaseUrl())
                .setNetLogLevel(NetLogLevel.BODY)
                .setServerTimestampListener(new ServerTimestampListener() {
                    @Override
                    public void onResponse(String serverTimestamp) {
                        Log.i(TAG, serverTimestamp);
                    }
                })
                .setTimestampKey("Date")
                .setHeaders(new HashMap<String, String>() {{
                    put("UserAgent", "Android 10.0");
                }});
        return builder.build(GitHubApi.class);
    }

    public static GitHubBaseUrl getGitHubBaseUrl() {
        if (gitHubBaseUrl == null) {
            gitHubBaseUrl = new GitHubBaseUrl();
        }
        return gitHubBaseUrl;
    }
}
```
- 继承BaseResponseSubscriber类，添加错误处理，示例如下：
```
public abstract class ResponseSubscriber<T> extends BaseResponseSubscriber<T> {
    public ResponseSubscriber(ILoadingView loadingView) {
        super(loadingView);
    }

    public ResponseSubscriber(ILoadingView loadingView, String message) {
        super(loadingView, message);
    }

    public ResponseSubscriber(ILoadingView loadingView, String message, boolean enableShowLoading) {
        super(loadingView, message, enableShowLoading);
    }

    @Override
    protected void handleError(Throwable e) {
        //error handler in here
        Log.e("api", "onError", e);
    }
}
```
在API返回时，通过以上ResponseSubscriber进行处理：
```
public class GitHubUserPresenter extends BasePresenter {
    private IMainView mainView;

    public GitHubUserPresenter(IMainView mainView) {
        super();
        this.mainView = mainView;
    }

    public void loadGitHubUser() {
        getGitHubApi().getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new ResponseSubscriber<GitHubUserBean>(this.mainView) {
                    @Override
                    public void onNext(GitHubUserBean gitHubUserBean) {
                        mainView.updateView(gitHubUserBean);
                    }
                });

    }
}
```
- 在view层的BaseView接口中继承ILoadingView接口：
```
public interface IMainView extends ILoadingView {
    void updateView(GitHubUserBean gitHubUserBean);
}
```
- 在BaseActiviy中实现ILoadingView接口：
```
public class BaseActivity extends AppCompatActivity implements ILoadingView {
    private LoadingSubscriber<String> loadingSubscriber;

    @Override
    protected void onDestroy() {
        if (this.loadingSubscriber != null) {
            this.loadingSubscriber.release();
            this.loadingSubscriber = null;
        }
        super.onDestroy();
    }

    @Override
    public LoadingSubscriber<String> showLoading(ILoadingCancelListener cancelListener, String message) {
        if (this.loadingSubscriber == null) {
            this.loadingSubscriber = new LoadingSubscriber<>(this, cancelListener);
        }
        this.loadingSubscriber.onNext(message);
        return this.loadingSubscriber;
    }
}
```
至此，继承基本完成，水平有限，虽然有点啰嗦，好歹还算清晰，如果疑问可以先看app module下的demo！如果您有更好的想法请不吝赐教，非常感谢！
