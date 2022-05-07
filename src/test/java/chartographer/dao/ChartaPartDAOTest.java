package chartographer.dao;


import chartographer.exception.ChartaNotFoundException;
import chartographer.manager.ChartaManager;
import chartographer.model.Charta;
import chartographer.model.ChartaPart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ChartaPartDAOTest {

    private static String TEST_FOLDER_PATH = "src/test/resources/testNotEmptyFolder/";

    private final int TEST_CHARTA_ID = 1245291119;
    private final int TEST_WIDTH = 20;
    private final int TEST_HEIGHT = 20;

    private final ChartaPartDAO chartaPartDAO;
    private final ChartaDAO chartaDAO;
    private final ChartaManager chartaManager;

    @Autowired
    public ChartaPartDAOTest(ChartaPartDAO chartaPartDAO, ChartaDAO chartaDAO, ChartaManager chartaManager) {
        this.chartaPartDAO = chartaPartDAO;
        this.chartaDAO = chartaDAO;
        this.chartaManager = chartaManager;
        ReflectionTestUtils.setField(this.chartaDAO, "pathToFiles", TEST_FOLDER_PATH);
        ReflectionTestUtils.setField(this.chartaManager, "pathToFiles", TEST_FOLDER_PATH);
        ReflectionTestUtils.invokeMethod(this.chartaDAO, "scanDirectory", null);
    }

    @Test
    public void saveTest() {
        Charta charta = new Charta(TEST_WIDTH, TEST_HEIGHT);

        ChartaPart chartaPart = new ChartaPart(0, 0, TEST_WIDTH, TEST_HEIGHT);
        chartaPartDAO.save(chartaPart, charta);

        assertEquals(chartaPart, charta.getParts().get(0));
    }

    @Test
    public void getCrossedChartaPartsTest() throws ChartaNotFoundException {
        Charta charta = chartaDAO.getById(TEST_CHARTA_ID);

        List<ChartaPart> chartaParts = chartaPartDAO.getCrossedChartaParts(charta, 20, 20, 20, 20);
        assertEquals(1, chartaParts.size());
        assertEquals(1, chartaParts.get(0).getId());

        chartaParts = chartaPartDAO.getCrossedChartaParts(charta, 5020, 20, 20, 20);
        assertEquals(1, chartaParts.size());
        assertEquals(2, chartaParts.get(0).getId());

        chartaParts = chartaPartDAO.getCrossedChartaParts(charta, 20, 5020, 20, 20);
        assertEquals(1, chartaParts.size());
        assertEquals(3, chartaParts.get(0).getId());

        chartaParts = chartaPartDAO.getCrossedChartaParts(charta, 5020, 5020, 20, 20);
        assertEquals(1, chartaParts.size());
        assertEquals(4, chartaParts.get(0).getId());
    }

}
