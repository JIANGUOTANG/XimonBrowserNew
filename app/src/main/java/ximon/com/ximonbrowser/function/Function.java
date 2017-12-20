package ximon.com.ximonbrowser.function;

/**
 * Created by ying on 17-10-9.
 */
public class Function {
    private String text;//文字
    private int idSvgIcon;//svg图标的id
    private String className;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;//访问的地址

    public int getIdSvgIcon() {
        return idSvgIcon;
    }

    public void setIdSvgIcon(int idSvgIcon) {
        this.idSvgIcon = idSvgIcon;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getText() {

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Function(String text, int idSvgIcon, String className,String url) {

        this.text = text;
        this.idSvgIcon = idSvgIcon;
        this.className = className;
        this.url = url;
    }
}
