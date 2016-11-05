package com.classic.wages.ui.rules;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import com.classic.wages.entity.BasicInfo;
import java.util.List;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.interfaces
 *
 * 文件描述：TODO
 * 创 建 人：续写经典
 * 创建时间：16/11/03 下午17:30
 */
public interface IWagesDetailLogic<T extends BasicInfo> {

    /**
     * 显示月工资详情
     * @param activity
     */
    void onDisplay(@NonNull Activity activity, @NonNull View targetView, List<T> list);
}
