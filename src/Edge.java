
import java.awt.*;

public class Edge {

    private int x, y;
    private int width, height;
    private Color color = Color.WHITE; // 初始为白色，等待上色
    private boolean visible = false;
    private boolean free = true;

    public Edge(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isFree() {
        return free;
    }

    public boolean isShown() { // 不free就是已经显示了
        return !free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void paint() {

        if (!isVisible()) {
            return;
        }

        boolean horizontal = getWidth() > getHeight(); // 判断是竖线还是横线，就不需要
        int midValue = (horizontal ? getHeight() : getWidth()) / 2; // 取线段宽度的一半？？
        int alphaStep = free ? 255 / midValue : 0; // ？？

        for (int i = 0; i < midValue; i += 1) {
            StdDraw.setPenColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255 - alphaStep * i)); // ？？？
            if (horizontal) { // 如果是横线
                StdDraw.filledRectangle(x + getWidth() / 2.0, y + getHeight() / 2.0 + i, getWidth() / 2.0, i);
                StdDraw.filledRectangle(x + getWidth() / 2.0, y + getHeight() / 2.0 - i, getWidth() / 2.0, i);
            } else { // 如果是竖线
                StdDraw.filledRectangle(x + getWidth() / 2.0 + i, y + getHeight() / 2.0, i, getHeight() / 2.0);
                StdDraw.filledRectangle(x + getWidth() / 2.0 - i, y + getHeight() / 2.0, i, getHeight() / 2.0);
            }
        }
    }
}
