package chartographer.dao;

import chartographer.model.Charta;
import chartographer.model.ChartaPart;

import java.util.List;

public interface ChartaPartDAO {

    /**
     * Saves charta part with assigning new id
     *
     * @param chartaPart {@link ChartaPart}
     * @param charta     {@link Charta}
     * @return {@link ChartaPart}
     */
    ChartaPart save(ChartaPart chartaPart, Charta charta);

    /**
     * @param charta {@link Charta}
     * @param x      coordinate
     * @param y      coordinate
     * @param width
     * @param height
     * @return List with {@link Charta} which are crossing specified region of charta
     */
    List<ChartaPart> getCrossedChartaParts(Charta charta, int x, int y, int width, int height);

}
