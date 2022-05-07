package chartographer.dao;

import chartographer.exception.ChartaNotFoundException;
import chartographer.manager.ChartaManager;
import chartographer.model.Charta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChartaDAONotEmptyFolderTest {

    private static String TEST_FOLDER_PATH = "src/test/resources/testNotEmptyFolder/";

    private final int TEST_CHARTA_ID = 1245291119;
    private final int TEST_CORRUPTED_CHARTA_ID = 1963681866;
    private final int TEST_WIDTH = 20;
    private final int TEST_HEIGHT = 20;
    private final int TEST_CHARTA_WIDTH = 10000;
    private final int TEST_CHARTA_HEIGHT = 10000;

    private final ChartaDAO chartaDAO;
    private final ChartaManager chartaManager;

    @Autowired
    public ChartaDAONotEmptyFolderTest(ChartaDAO chartaDAO, ChartaManager chartaManager) {
        this.chartaDAO = chartaDAO;
        this.chartaManager = chartaManager;
        ReflectionTestUtils.setField(this.chartaDAO, "pathToFiles", TEST_FOLDER_PATH);
        ReflectionTestUtils.setField(this.chartaManager, "pathToFiles", TEST_FOLDER_PATH);
        ReflectionTestUtils.invokeMethod(this.chartaDAO, "scanDirectory", null);
    }

    @Test
    public void getByIdTest() throws ChartaNotFoundException {
        Charta charta = new Charta(TEST_WIDTH, TEST_HEIGHT);
        chartaDAO.save(charta);

        int id = charta.getId();
        assertNotNull(id);

        assertEquals(charta, chartaDAO.getById(id));
    }

    @Test
    public void saveTest() {
        Charta charta = new Charta(TEST_WIDTH, TEST_HEIGHT);
        chartaDAO.save(charta);

        int id = charta.getId();
        assertNotNull(id);

        Map<Integer, Charta> chartaMap =
                (HashMap<Integer, Charta>) ReflectionTestUtils
                        .getField(chartaDAO, "chartaMap");

        assertEquals(charta, chartaMap.get(id));
    }

    @Test
    public void deleteByIdTest() {
        Charta charta = new Charta(TEST_WIDTH, TEST_HEIGHT);
        chartaDAO.save(charta);

        int id = charta.getId();
        assertNotNull(id);

        chartaDAO.deleteById(id);

        Map<Integer, Charta> chartaMap =
                (HashMap<Integer, Charta>) ReflectionTestUtils
                        .getField(chartaDAO, "chartaMap");

        assertNull(chartaMap.get(id));
    }

    @Test
    public void onlyOneNormalChartaCorrectlyTest() throws ChartaNotFoundException {
        Charta charta = chartaDAO.getById(TEST_CHARTA_ID);
        assertEquals(4, charta.getParts().size());
        assertEquals(charta.getHeight(), TEST_CHARTA_HEIGHT);
        assertEquals(charta.getWidth(), TEST_CHARTA_WIDTH);

        boolean flag = false;
        try {
            chartaDAO.getById(TEST_CORRUPTED_CHARTA_ID);
        } catch (ChartaNotFoundException e) {
            flag = true;
        }
        assertTrue(flag);

        Map<Integer, Charta> chartaMap =
                (HashMap<Integer, Charta>) ReflectionTestUtils
                        .getField(chartaDAO, "chartaMap");
        assertEquals(1, chartaMap.size());
    }

}
