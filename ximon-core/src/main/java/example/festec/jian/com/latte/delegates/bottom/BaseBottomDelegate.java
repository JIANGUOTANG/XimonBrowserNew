package example.festec.jian.com.latte.delegates.bottom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import example.festec.jian.com.latte.R;
import example.festec.jian.com.latte.R2;
import example.festec.jian.com.latte.delegates.LatteDelegate;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by ying on 17-8-23.
 */

public  abstract  class BaseBottomDelegate extends LatteDelegate implements View.OnClickListener{
    private final ArrayList<BottomItemDelegate> ITEM_DELEGATES = new ArrayList<>();//对应的fragment
    private final ArrayList<BottomTabBean> TAB_BEAN = new ArrayList<>();//图标
    private final LinkedHashMap<BottomTabBean,BottomItemDelegate> ITEMS = new LinkedHashMap<>();
    private int mCurrentDelegate = 0;//当前的fragment
    private int mIndexDelegate = 0;
    //点击后的颜色
    private int mClickedColor  = Color.RED;
    public abstract LinkedHashMap<BottomTabBean,BottomItemDelegate> setItems(ItemBuilder builder);

    //默认没选中的图标颜色
    private int mDefaultColor = Color.GRAY;

    public void setmDefaultColor(int mDefaultColor) {
        this.mDefaultColor = mDefaultColor;
    }

    @BindView(R2.id.bottom_bar)
    LinearLayoutCompat mBottomBar = null;

    @Override
    public Object setLayout() {
        return R.layout.delegatebottom;

    }

    public abstract int setIndexDelegate();
    //设置当前的fragment
    public abstract int setClickedColor();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIndexDelegate = setIndexDelegate();
        if(setClickedColor()!=0){
            mClickedColor = setClickedColor();
        }
        final ItemBuilder builder = ItemBuilder.builder();
        final  LinkedHashMap<BottomTabBean,BottomItemDelegate> items = setItems(builder);


        ITEMS.putAll(items);

        for(Map.Entry<BottomTabBean,BottomItemDelegate> item:ITEMS.entrySet()){
            final BottomTabBean key = item.getKey();

            final BottomItemDelegate  value=  item.getValue();
            TAB_BEAN.add(key);
            ITEM_DELEGATES.add(value);
        }


    }

    @Override
    public void onBingView(@Nullable Bundle savedInstanceState, View rootView) {
        //多少个图标
       final int size=ITEMS.size();
        for(int i= 0;i<size;i++){
           LayoutInflater.from(getContext()).inflate(R.layout.bottom_item_text_layout,mBottomBar);
            //取出每个Item
            final RelativeLayout item = (RelativeLayout) mBottomBar.getChildAt(i);
            //设置每个item的点击事件
            item.setTag(i);
            item.setOnClickListener(this);
            //拿到iCOn
            final IconTextView itemIcon = (IconTextView) item.getChildAt(0);
            //拿到title
            final AppCompatTextView itemTitle = (AppCompatTextView) item.getChildAt(1);

            final BottomTabBean bean= TAB_BEAN.get(i);

            //初始化数据
            itemIcon.setText(bean.getICON());
            itemTitle.setText(bean.getTITLE());

            if(i == mIndexDelegate){
                //设置点击颜色
                itemIcon.setTextColor(mClickedColor);
                itemTitle.setTextColor(mClickedColor);
            }
        }

        final SupportFragment[] delegateArray = ITEM_DELEGATES.toArray(new SupportFragment[size]);

        loadMultipleRootFragment(R.id.bottom_bar_delegate_container,mIndexDelegate,delegateArray);
    }
    //重置颜色
    private void resetColor(){
        final int count = mBottomBar.getChildCount();
        for(int i = 0;i<count;i++){
            //取出每个Item
            final RelativeLayout item = (RelativeLayout) mBottomBar.getChildAt(i);
            //拿到iCOn
            final IconTextView itemIcon = (IconTextView) item.getChildAt(0);
            itemIcon.setTextColor(mDefaultColor);

            //拿到title
            final AppCompatTextView itemTitle = (AppCompatTextView) item.getChildAt(1);
            itemTitle.setTextColor(mDefaultColor);

        }
    }

    @Override
    public void onClick(View view) {
        final int tag = (int) view.getTag();
        resetColor();
        //取出每个Item
        final RelativeLayout item = (RelativeLayout) view;
        //拿到iCOn
        final IconTextView itemIcon = (IconTextView) item.getChildAt(0);
        itemIcon.setTextColor(mClickedColor);
        //拿到title
        final AppCompatTextView itemTitle = (AppCompatTextView) item.getChildAt(1);
        itemTitle.setTextColor(mClickedColor);
        //注意先后顺序，第一个是要显示的fragment,第二是要隐藏的
        showHideFragment(ITEM_DELEGATES.get(tag),ITEM_DELEGATES.get(mCurrentDelegate));
        //设置当前的fragment
        mCurrentDelegate = tag;

    }
}

