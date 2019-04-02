import java.awt.*;

public class Region {
    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public float s1;
    public float t1;
    public float s2;
    public float t2;
    Color c;

    public Region(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2, Color c) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.s1 = s1;
        this.t1 = t1;
        this.s2 = s2;
        this.t2 = t2;
        this.c = c;
    }

    @Override
    public String toString() {
        return "Region{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", s1=" + s1 +
                ", t1=" + t1 +
                ", s2=" + s2 +
                ", t2=" + t2 +
                ", c=" + c +
                '}';
    }
}
