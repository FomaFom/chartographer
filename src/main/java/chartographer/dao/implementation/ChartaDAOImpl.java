package chartographer.dao.implementation;

import chartographer.Main;
import chartographer.constants.ProjectConstants;
import chartographer.dao.ChartaDAO;
import chartographer.exception.ChartaNotFoundException;
import chartographer.exception.DirectoryException;
import chartographer.model.Charta;
import chartographer.model.ChartaPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
public class ChartaDAOImpl implements ChartaDAO {

    private static final Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

    /**
     * HashMap with all chartas
     */
    private Map<Integer, Charta> chartaMap;

    private final String pathToFiles;
    private final Random random = new Random();

    Pattern pattern = Pattern.compile(ProjectConstants.FILE_NAME_REGEX);

    @Autowired
    public ChartaDAOImpl() throws DirectoryException {
        pathToFiles = Main.pathToFiles;
        if (pathToFiles == null) {
            chartaMap = new HashMap<>();
        } else {
            scanDirectory();
        }
    }

    @Override
    public Charta getById(int id) throws ChartaNotFoundException {
        Charta charta = chartaMap.get(id);

        if (charta == null) {
            throw new ChartaNotFoundException(
                    ProjectConstants.Exception.CHARTA_NOT_FOUND_EXCEPTION_MESSAGE);
        }

        return charta;
    }

    @Override
    public Charta save(Charta charta) {
        charta.setId(createAndGetNewId());
        chartaMap.put(charta.getId(), charta);
        return charta;
    }

    @Override
    public void deleteById(int id) {
        chartaMap.remove(id);
    }

    /**
     * Creates new id
     *
     * @return new Id
     */
    private int createAndGetNewId() {
        while (true) {
            int id = random.nextInt(Integer.MAX_VALUE);
            if (!chartaMap.containsKey(id)) {
                return id;
            }
        }
    }

    /**
     * Creates charta part from file if possible
     *
     * @param f File
     * @return {@link ChartaPart}
     */
    private ChartaPart chartaPartFromFile(File f) {
        if (!isFileNormal(f)) {
            log.info("File {} is in wrong format", f);
            return null;
        }
        try {
            int[] data = getDataFromFile(f);

            if (data.length < 6) {
                log.info("File {} is corrupted", f);
                return null;
            }

            ChartaPart cp = new chartographer.model.ChartaPart(
                    data[0],
                    data[1],
                    data[2],
                    data[3],
                    data[4],
                    data[5]
            );

            return cp;

        } catch (IOException | IllegalStateException| NumberFormatException e) {
            log.info("Can't read {} file properly", f);
            return null;
        }
    }

    /**
     * Checks if file can be converted to charta part
     *
     * @param f File
     * @return
     */
    private Boolean isFileNormal(File f) {
        return pattern.matcher(f.getName()).matches();
    }

    /**
     * Reads file name and width and length from image
     *
     * @param f File
     * @return massive with charta id, charta part id, x coordinate, y coordinate, width and height of the image
     * @throws IOException
     * @throws IllegalStateException
     */
    private int[] getDataFromFile(File f) throws IOException, IllegalStateException, NumberFormatException {
        Matcher matcher = pattern.matcher(f.getName());
        matcher.matches();

        BufferedImage image = ImageIO.read(f);

        int chartaId = Integer.parseInt(matcher.group(1));
        int chartaPartId = Integer.parseInt(matcher.group(2));
        int x = Integer.parseInt(matcher.group(3));
        int y = Integer.parseInt(matcher.group(4));
        int width = image.getWidth();
        int height = image.getHeight();

        if (x + width <= ProjectConstants.Charta.MAX_CHARTA_WIDTH
                && y + height <= ProjectConstants.Charta.MAX_CHARTA_HEIGHT
                && x > ProjectConstants.Charta.MAX_CHARTA_WIDTH
                && y > ProjectConstants.Charta.MAX_CHARTA_HEIGHT
                && width > ProjectConstants.ChartaPart.MAX_PART_WIDTH
                && height > ProjectConstants.ChartaPart.MAX_PART_HEIGHT
                && chartaPartId > ProjectConstants.Charta.getMaxPartsInCharta()
                && chartaPartId != 0) {
            return new int[]{};
        }
        return new int[]{chartaId, chartaPartId, x, y, width, height};
    }

    /**
     * Check if files can be assembled into charta
     *
     * @param charta {@link Charta}
     * @return if charta can be assembled
     */
    private boolean isChartaCorrupted(Charta charta) {

        int x = 0;
        int y = 0;
        int prevY = 0;

        List<ChartaPart> chartaParts = charta.getParts();
        if (chartaParts.size() == 0) {
            return false;
        }

        ChartaPart lastChartaPart = chartaParts.get(chartaParts.size() - 1);
        charta.setWidth(lastChartaPart.getX() + lastChartaPart.getWidth());
        charta.setHeight(lastChartaPart.getY() + lastChartaPart.getHeight());

        for (ChartaPart cp : chartaParts) {
            if (x == charta.getWidth() && prevY == cp.getY()) {
                x = cp.getWidth();
                y = prevY;
            } else if (x == cp.getX() && y == cp.getY()) {
                x += cp.getWidth();
                prevY = cp.getY() + cp.getHeight();
            } else {
                return false;
            }
        }

        return x == charta.getWidth() && prevY == charta.getHeight();
    }

    /**
     * Scans the directory and creates chartas if files can be assembled into one.
     *
     * @throws DirectoryException if directory is invalid
     */
    private final void scanDirectory() throws DirectoryException {
        File folder = new File(pathToFiles);

        if (!folder.isDirectory() || !folder.canRead() || !folder.canWrite()) {
            throw new DirectoryException("Invalid directory");
        }

        File[] bmpFiles = folder.listFiles(f -> f.getName().endsWith("." + ProjectConstants.FILE_FORMAT_BMP));
        List<ChartaPart> chartaParts = Arrays.stream(bmpFiles).map(this::chartaPartFromFile).filter(Objects::nonNull).collect(Collectors.toList());
        if (chartaParts.size() != 0) {

            chartaMap = Arrays.stream(bmpFiles)
                    .filter(this::isFileNormal)
                    .map(f -> f.getName().split("_")[0])
                    .distinct()
                    .map(s -> {
                        Charta charta = new Charta(Integer.parseInt(s));
                        charta.setParts(chartaParts
                                .stream()
                                .filter(cp -> cp.getChartaId().equals(charta.getId()))
                                .sorted((cp1, cp2) -> cp1.getId() > cp2.getId() ? 1 : -1)
                                .collect(Collectors.toList())
                        );
                        return charta;
                    })
                    .filter(this::isChartaCorrupted)
                    .collect(Collectors.toMap(Charta::getId, Function.identity()));

        } else {
            log.info("There are no matching files in directory");
            chartaMap = new HashMap<>();
        }
    }


}
