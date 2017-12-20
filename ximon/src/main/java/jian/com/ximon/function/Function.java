package jian.com.ximon.function;

/**
 * Created by ying on 17-10-9.
 */
public class Function {
    private String text;//文字
    private int idSvgIcon;//svg图标的id
    private String className;
    private int color;

    public Function(String text, int idSvgIcon, String className, int color) {
        this.text = text;
        this.idSvgIcon = idSvgIcon;
        this.className = className;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

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

    public Function(String text, int idSvgIcon, String className) {

        this.text = text;
        this.idSvgIcon = idSvgIcon;
        this.className = className;

    }
}
