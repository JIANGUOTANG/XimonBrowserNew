package example.festec.jian.com.latte.delegates.bottom;

import java.util.LinkedHashMap;

/**
 * Created by ying on 17-8-23.
 */

public final class ItemBuilder {
    private final LinkedHashMap<BottomTabBean,BottomItemDelegate> ITEMS = new LinkedHashMap<>();
    static  ItemBuilder builder(){
        return new ItemBuilder();
    }
    public final ItemBuilder addItem(BottomTabBean bean,BottomItemDelegate  delegate){
        ITEMS.put(bean,delegate);
        return this;
    }
    public final ItemBuilder addItem(LinkedHashMap<BottomTabBean,BottomItemDelegate> items){
        ITEMS.putAll(items);
        return this;
    }

    public final LinkedHashMap<BottomTabBean,BottomItemDelegate> build(){
        return ITEMS;
    }
}
