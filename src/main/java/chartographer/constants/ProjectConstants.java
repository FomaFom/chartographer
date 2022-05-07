package chartographer.constants;

public abstract class ProjectConstants {

    public static final String FILE_FORMAT_BMP = "bmp";
    public static final String FILE_NAME_REGEX = "^(\\d+)_(\\d+)_(\\d+)_(\\d+)\\.bmp$";
    public static final int MINIMAL_SIDE = 1;

    public static abstract class Charta {
        public static final int MAX_CHARTA_WIDTH = 20000;
        public static final int MAX_CHARTA_HEIGHT = 50000;

        public static final int getMaxPartsInCharta() {
            int tmpWidth = MAX_CHARTA_WIDTH / ChartaPart.MAX_PART_WIDTH;
            int tmpHeight = MAX_CHARTA_HEIGHT / ChartaPart.MAX_PART_HEIGHT;
            tmpWidth = MAX_CHARTA_WIDTH % ChartaPart.MAX_PART_WIDTH > 0 ? tmpWidth + 1 : tmpWidth;
            tmpHeight = MAX_CHARTA_HEIGHT % ChartaPart.MAX_PART_HEIGHT > 0 ? tmpHeight + 1 : tmpHeight;
            return tmpWidth * tmpHeight;
        }
    }

    public static abstract class ChartaPart {
        public static final int MIN_COORDINATE = 0;
        public static final int MAX_PART_WIDTH = 5000;
        public static final int MAX_PART_HEIGHT = 5000;
    }

    public static abstract class Controller {
        public static final String MIME_TYPE_BMP = "image/bmp";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
    }

    public static abstract class Exception {
        public static final String CHARTA_NOT_FOUND_EXCEPTION_MESSAGE = "Specified charta does not exist";
        public static final String COORDINATES_OUT_OF_BOUNDS_EXCEPTION_MESSAGE = "Specified coordinates is outside of requested charta";
    }

}
