package example.festec.jian.com.latte.delegates;

/**
 * Created by ying on 17-8-19.
 */

public abstract class LatteDelegate extends PermeissionCheckerDelegete  {
    @SuppressWarnings("unchecked")
    public <T extends LatteDelegate> T getParentDelegate() {
        return (T) getParentFragment();
    }

}
