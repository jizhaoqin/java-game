
import java.awt.*;

public class DotForCredit {

    private int x, y;
    private int size;
    private Color color = Color.white; // 初始设定为白色，不setColor就不会显示（一次性）
    private boolean free = true; // 有必要，因为update是要setColor的（只能由true便false）
    private boolean work = true;

    public DotForCredit(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public boolean isFree() {
        return free;
    }

    public boolean isWork() {
        return work;
    }

    public Color getColor() {
        return color;
    }

    public int getSize() {
        return size;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public void setWork() { // setWork方法只会调用一次，将其失效
        this.work = false;
    }

    public void setColor(boolean getCredit, Color color) {
        if (isFree()) { // 避免多次更改颜色（即只能改一次）
            if (getCredit) { //
                this.color = color;
                setFree(false); // 关闭入口
            }
        }
    }

    public void paint() { // 以第一次设定的颜色上色
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(x, y, size);
    }
}
