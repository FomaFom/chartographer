package chartographer.model;

/**
 * {@code ChartaPart} is a part of a {@link Charta}.
 */
public class ChartaPart {
    /**
     * Identifier
     */
    private Integer id;

    /**
     * x coordinate of a part in the whole charta
     */
    private int x;

    /**
     * y coordinate of a part in the whole charta
     */
    private int y;

    private int width;

    private int height;

    /**
     * Identifier of a {@link Charta}
     */
    private Integer chartaId;

    public ChartaPart(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public ChartaPart(Integer chartaId, Integer id, int x, int y, int width, int height) {
        this.chartaId = chartaId;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Integer getChartaId() {
        return chartaId;
    }

    public void setChartaId(Integer chartaId) {
        this.chartaId = chartaId;
    }
}
