package com.classic.wages.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.qy.util.activity.R;
import com.bigkoo.pickerview.TimePickerView;
import com.classic.adapter.CommonRecyclerAdapter;
import com.classic.wages.app.WagesApplication;
import com.classic.wages.consts.Consts;
import com.classic.wages.consts.QueryType;
import com.classic.wages.db.dao.MonthlyInfoDao;
import com.classic.wages.db.dao.QuantityInfoDao;
import com.classic.wages.db.dao.WorkInfoDao;
import com.classic.wages.ui.activity.AddActivity;
import com.classic.wages.ui.activity.MainActivity;
import com.classic.wages.ui.base.AppBaseFragment;
import com.classic.wages.ui.pop.QueryTypeSelectionPopupWindow;
import com.classic.wages.ui.rules.ICalculationRules;
import com.classic.wages.ui.rules.IListLogic;
import com.classic.wages.ui.rules.IWagesDetailLogic;
import com.classic.wages.ui.rules.basic.DefaultListLogicImpl;
import com.classic.wages.ui.rules.basic.DefaultWagesDetailLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedDayListLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedDayWagesDetailLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedMonthListLogicImpl;
import com.classic.wages.ui.rules.fixed.FixedMonthWagesDetailLogicImpl;
import com.classic.wages.ui.rules.monthly.MonthlyListLogicImpl;
import com.classic.wages.ui.rules.pizzahut.PizzaHutListLogicImpl;
import com.classic.wages.ui.rules.pizzahut.PizzaHutWagesDetailLogicImpl;
import com.classic.wages.ui.rules.quantity.QuantityListLogicImpl;
import com.classic.wages.ui.rules.quantity.QuantityWagesDetailLogicImpl;
import com.classic.wages.utils.DateUtil;
import com.classic.wages.utils.Util;
import com.jaredrummler.materialspinner.MaterialSpinner;
import java.util.Date;
import javax.inject.Inject;

import static com.classic.wages.ui.activity.AddActivity.TYPE_ADD;

/**
 * 应用名称: WagesRecords
 * 包 名 称: com.classic.wages.ui.fragment
 *
 * 文件描述：列表、查询页面
 * 创 建 人：续写经典
 * 创建时间：16/10/15 下午5:54
 */
public class ListFragment extends AppBaseFragment implements TimePickerView.OnTimeSelectListener{

    @BindView(R.id.query_months_spinner) MaterialSpinner      mMonthsSpinner;
    @BindView(R.id.query_years_spinner)  MaterialSpinner      mYearsSpinner;
    @BindView(R.id.query_month_layout)   LinearLayout         mQueryMonthLayout;
    @BindView(R.id.query_date_layout)    LinearLayout         mQueryDateLayout;
    @BindView(R.id.recycler_view)        RecyclerView         mRecyclerView;
    @BindView(R.id.fab)                  FloatingActionButton mFab;
    @BindView(R.id.add_start_time)       TextView             mStartTimeView;
    @BindView(R.id.add_end_time)         TextView             mEndTimeView;

    @Inject WorkInfoDao     mWorkInfoDao;
    @Inject MonthlyInfoDao  mMonthlyInfoDao;
    @Inject QuantityInfoDao mQuantityInfoDao;

    private IListLogic        mListLogic;
    private IWagesDetailLogic mWagesDetailLogic;
    private String            mFilterYear;
    private String            mFilterMonth;
    private int               mOffset;
    private int               mRulesType = -1;

    private int            mQueryType = QueryType.QUERY_TYPE_MONTH;
    /** 当前是否选择的开始时间 */
    private boolean        isChooseStartTime;
    /** 当前选择的开始时间 */
    private Long           mCurrentStartTime;
    /** 当前选择的结束时间 */
    private Long           mCurrentEndTime;
    private TimePickerView mTimePickerView;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override public int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        ((WagesApplication) mActivity.getApplicationContext()).getAppComponent().inject(this);

    }

    @Override public void initView(View parentView, Bundle savedInstanceState) {
        super.initView(parentView, savedInstanceState);
        mOffset = Util.dp2px(mAppContext, 144);
        mYearsSpinner.setItems(Consts.YEARS);
        mMonthsSpinner.setItems(Consts.MONTHS);
        mYearsSpinner.setOnItemSelectedListener(mYearSelectedListener);
        mMonthsSpinner.setOnItemSelectedListener(mMonthSelectedListener);
        mYearsSpinner.setSelectedIndex(DateUtil.getYear() - Consts.MIN_YEAR);
        mMonthsSpinner.setSelectedIndex(DateUtil.getMonth());
        mFilterYear = mYearsSpinner.getText().toString();
        mFilterMonth = mMonthsSpinner.getText().toString();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new CommonRecyclerAdapter.AbsScrollControl() {
            @Override public void onHide() {
                mFab.animate().translationY(mOffset).setInterpolator(new AccelerateInterpolator(2));
            }

            @Override public void onShow() {
                mFab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });
        //mQueryType = WagesApplication.getPreferencesUtil().getIntValue(Consts.SP_QUERY_TYPE,
        //        QueryType.QUERY_TYPE_MONTH);
        refreshQueryTypeLayout(mQueryType);
        mRulesType = Util.getPreferencesInt(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        onCalculationRulesChange(mRulesType);
        queryData(mQueryType, mRulesType);
    }

    private void refreshQueryTypeLayout(int type) {
        final boolean hasMonthlyRules = mRulesType == ICalculationRules.RULES_MONTHLY;
        mQueryMonthLayout.setVisibility(
                !hasMonthlyRules && type == QueryType.QUERY_TYPE_MONTH ? View.VISIBLE : View.GONE);
        mQueryDateLayout.setVisibility(
                !hasMonthlyRules && type == QueryType.QUERY_TYPE_DATE ? View.VISIBLE : View.GONE);
        if(!hasMonthlyRules && type == QueryType.QUERY_TYPE_DATE) {
            refreshDateLayout();
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if(mRulesType == ICalculationRules.RULES_MONTHLY) return false;
        switch (item.getItemId()) {
            case R.id.action_query:
                QueryTypeSelectionPopupWindow queryTypeSelectionPopupWindow =
                new QueryTypeSelectionPopupWindow(mActivity, mQueryType, new QueryTypeSelectionPopupWindow.Listener() {
                    @Override public void onQueryTypeChange(@QueryType int type) {
                        //WagesApplication.getPreferencesUtil().putIntValue(Consts.SP_QUERY_TYPE,
                        //        type);
                        mQueryType = type;
                        refreshQueryTypeLayout(mQueryType);
                    }
                });
                final Toolbar toolbar = ((MainActivity)mActivity).getToolbar();
                //queryTypeSelectionPopupWindow.showAsDropDown(toolbar, 0, 0, Gravity.RIGHT);
                queryTypeSelectionPopupWindow.showAsDropDown(toolbar, toolbar.getWidth()
                        - queryTypeSelectionPopupWindow.getWidth(), 0);
                return true;
            case R.id.action_wages_detail:
                if(mWagesDetailLogic != null && mListLogic != null){
                    mWagesDetailLogic.onDisplay(mActivity, ((MainActivity)mActivity).getToolbar(),
                            mListLogic.getData());
                }
                return true;
            default:
                return false;
        }
    }

    @Override public void onCalculationRulesChange(int rules) {
        final boolean hasMonthlyRules = rules == ICalculationRules.RULES_MONTHLY;
        setHasOptionsMenu(!hasMonthlyRules);
        if(hasMonthlyRules){
            mListLogic = new MonthlyListLogicImpl(mActivity, mMonthlyInfoDao);
            mRecyclerView.setAdapter(mListLogic.getAdapter());
            return;
        }

        switch (rules){
            case ICalculationRules.RULES_FIXED_DAY:
                mListLogic = new FixedDayListLogicImpl(mActivity, mWorkInfoDao);
                mWagesDetailLogic = new FixedDayWagesDetailLogicImpl();
                break;
            case ICalculationRules.RULES_FIXED_MONTH:
                mListLogic = new FixedMonthListLogicImpl(mActivity, mWorkInfoDao);
                mWagesDetailLogic = new FixedMonthWagesDetailLogicImpl();
                break;
            case ICalculationRules.RULES_PIZZAHUT:
                mListLogic = new PizzaHutListLogicImpl(mActivity, mWorkInfoDao);
                mWagesDetailLogic = new PizzaHutWagesDetailLogicImpl();
                break;
            case ICalculationRules.RULES_QUANTITY:
                mListLogic = new QuantityListLogicImpl(mActivity, mQuantityInfoDao);
                mWagesDetailLogic = new QuantityWagesDetailLogicImpl();
                break;
            case ICalculationRules.RULES_DEFAULT:
            default:
                mListLogic = new DefaultListLogicImpl(mActivity, mWorkInfoDao);
                mWagesDetailLogic = new DefaultWagesDetailLogicImpl();
                break;
        }
        mRecyclerView.setAdapter(mListLogic.getAdapter());
    }

    @Override public void onRecalculation() {
        int rulesType = mRulesType;
        mRulesType = -1;
        onCalculationRulesChange(rulesType);
        queryData(mQueryType, mRulesType);
    }

    @OnClick(R.id.fab) public void onFabClick(){
        AddActivity.start(mActivity, TYPE_ADD, mRulesType, null);
    }

    @Override public void onFragmentShow() {
        super.onFragmentShow();
        final int rules = Util.getPreferencesInt(Consts.SP_RULES_TYPE, ICalculationRules.RULES_DEFAULT);
        if(mRulesType != rules){
            mRulesType = rules;
            onCalculationRulesChange(rules);
            queryData(mQueryType, mRulesType);
        }
        refreshQueryTypeLayout(mQueryType);
    }

    private final MaterialSpinner.OnItemSelectedListener<String> mYearSelectedListener
            = new MaterialSpinner.OnItemSelectedListener<String>() {
        @Override
        public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
            mFilterYear = item;
            queryData(mQueryType, mRulesType);
            //mListLogic.onDataQuery(mFilterYear, mFilterMonth);
        }
    };
    private final MaterialSpinner.OnItemSelectedListener<String> mMonthSelectedListener
            = new MaterialSpinner.OnItemSelectedListener<String>() {
        @Override
        public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
            mFilterMonth = item;
            queryData(mQueryType, mRulesType);
            //mListLogic.onDataQuery(mFilterYear, mFilterMonth);
        }
    };


    @Override public void onTimeSelect(Date date) {
        if (isChooseStartTime) {
            mCurrentStartTime = date.getTime();
        } else {
            mCurrentEndTime = date.getTime();
        }
        //ToastUtil.showToast(mAppContext, DateUtil.formatDate(DateUtil.FORMAT_DATE_TIME, date.getTime()));
        refreshDateLayout();
        queryData(mQueryType, mRulesType);
    }

    private void refreshDateLayout() {
        if(null != mCurrentStartTime && null != mCurrentEndTime &&
                mCurrentStartTime > mCurrentEndTime) {
            // 开始时间大于结束时间时，进行交换处理
            final long temp = mCurrentStartTime;
            mCurrentStartTime = mCurrentEndTime;
            mCurrentEndTime = temp;
        }
        if(null != mCurrentStartTime) {
            mStartTimeView.setText(DateUtil.formatDate(DateUtil.FORMAT_DATE, mCurrentStartTime));
        }
        if(null != mCurrentEndTime) {
            mEndTimeView.setText(DateUtil.formatDate(DateUtil.FORMAT_DATE, mCurrentEndTime));
        }
    }

    private void queryData(int queryType, int rulesType) {
        if(null == mListLogic) { return; }
        if(rulesType == ICalculationRules.RULES_MONTHLY) {
            mListLogic.onDataQuery(null, null);
        } else if(queryType == QueryType.QUERY_TYPE_DATE && null != mCurrentStartTime &&
                null != mCurrentEndTime) {
            // 选择的时间是一天的开始时间，需要加1
            final long time = DateUtil.getAddDay(mCurrentEndTime, 1).getTime();
            mListLogic.onDataQuery(mCurrentStartTime, time);
        } else if (queryType == QueryType.QUERY_TYPE_MONTH) {
            mListLogic.onDataQuery(mFilterYear, mFilterMonth);
        }
    }

    private void showDatePicker(Date date) {
        mTimePickerView = new TimePickerView(mActivity, TimePickerView.Type.YEAR_MONTH_DAY);
        mTimePickerView.setCyclic(false);
        mTimePickerView.setCancelable(false);
        mTimePickerView.setOnTimeSelectListener(this);
        mTimePickerView.setRange(Consts.MIN_YEAR, Consts.MAX_YEAR);
        mTimePickerView.setTime(date);
        mTimePickerView.show();
    }

    @OnClick(R.id.add_start_time) void onStartTimeClick(){
        isChooseStartTime = true;
        showDatePicker(null==mCurrentStartTime ? new Date() : new Date(mCurrentStartTime));
    }
    @OnClick(R.id.add_end_time) void onEndTimeClick(){
        isChooseStartTime = false;
        showDatePicker(null==mCurrentEndTime ? new Date() : new Date(mCurrentEndTime));
    }
}
