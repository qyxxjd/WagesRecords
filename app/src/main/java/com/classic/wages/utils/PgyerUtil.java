package com.classic.wages.utils;

import android.app.Activity;
import android.content.Context;
import cn.qy.util.activity.R;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.classic.core.utils.NetworkUtil;
import com.classic.core.utils.ToastUtil;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

/**
 *
 * 文件描述：版本更新
 * 创 建 人：续写经典
 * 创建时间：16/7/9 下午4:00
 */
public final class PgyerUtil {

    public static void checkUpdate(final Activity activity, final boolean showHint){
        if(!NetworkUtil.isNetworkAvailable(activity)){
            if(showHint){
                ToastUtil.showToast(activity, R.string.network_error);
            }
            return;
        }

        PgyUpdateManager.register(activity, new UpdateManagerListener() {
            @Override public void onUpdateAvailable(final String result) {
                final AppBean appBean = getAppBeanFromString(result);

                new MaterialDialog.Builder(activity)
                        .title(R.string.update_dialog_title)
                        .titleColorRes(R.color.primary_text)
                        .backgroundColorRes(android.R.color.white)
                        .content(appBean.getReleaseNote())
                        .contentColorRes(R.color.primary_light)
                        .positiveText(R.string.update)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override public void onClick(MaterialDialog dialog, DialogAction which) {
                                startDownloadTask(activity, appBean.getDownloadURL());
                            }
                        }).show();
            }

            @Override public void onNoUpdateAvailable() {
                if (showHint) {
                    ToastUtil.showToast(activity, R.string.no_update);
                }
            }
        });
    }

    public static void register(Context context){
        PgyCrashManager.register(context);
    }

    public static void destroy(){
        try{
            PgyUpdateManager.unregister();
            PgyCrashManager.unregister();
            PgyFeedbackShakeManager.unregister();
            PgyFeedback.getInstance().destroy();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
