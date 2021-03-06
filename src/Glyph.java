public class Glyph {
    private final int width;
    private final int height;
    private final int x;
    private final int y;
    private final float advance;

    /**
     * create a font glyph
     *
     * @param width   Width of the Glyph
     * @param height  Height of the Glyph
     * @param x       X coordinate on the font texture
     * @param y       Y coordinate on the font texture
     * @param advance Advance width
     */
    public Glyph(int width, int height, int x, int y, float advance) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.advance = advance;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getAdvance() {
        return advance;
    }
}
