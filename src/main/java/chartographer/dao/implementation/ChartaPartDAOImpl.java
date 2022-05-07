package chartographer.dao.implementation;

import chartographer.dao.ChartaPartDAO;
import chartographer.model.Charta;
import chartographer.model.ChartaPart;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChartaPartDAOImpl implements ChartaPartDAO {

    @Override
    public ChartaPart save(ChartaPart chartaPart, Charta charta) {
        List<ChartaPart> chartaParts = charta.getParts();
        chartaPart.setId(chartaParts.size() + 1);
        chartaParts.add(chartaPart);
        chartaPart.setChartaId(charta.getId());
        return chartaPart;
    }

    @Override
    public List<ChartaPart> getCrossedChartaParts(Charta charta, int x, int y, int width, int height) {
        return charta.getParts()
                .stream()
                .filter(cp ->
                        (cp.getX() < (x + width))
                                && ((cp.getX() + cp.getWidth()) > x)
                                && (cp.getY() < (y + height))
                                && ((cp.getY() + cp.getHeight()) > y))
                .collect(Collectors.toList());
    }
}
