package chartographer.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  {@code Charta} is a huge image which consists of Charta parts.
 */
public class Charta {

    /**
     * Identifier
     */
    private Integer id;

    private int width;

    private int height;

    /**
     *List of all {@link ChartaPart} in this charta
     */
    private List<ChartaPart> parts;

    public Charta(Integer id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        parts = new ArrayList<>();
    }

    public Charta(int width, int height) {
        this.width = width;
        this.height = height;
        parts = new ArrayList<>();
    }

    public Charta(int id) {
        this.id = id;
        parts = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<ChartaPart> getParts() {
        return parts;
    }

    public void setParts(List<ChartaPart> parts) {
        this.parts = parts;
    }
}
