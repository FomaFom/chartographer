package chartographer.dao;

import chartographer.exception.ChartaNotFoundException;
import chartographer.model.Charta;

public interface ChartaDAO {

    /**
     * Returns charta by specified id
     *
     * @param id Charta id
     * @return {@link Charta} with specified id
     * @throws ChartaNotFoundException if charta does not exist
     */
    Charta getById(int id) throws ChartaNotFoundException;

    /**
     * Saves charta with assigning new id
     *
     * @param charta {@link Charta}
     * @return {@link Charta}
     */
    Charta save(Charta charta);

    /**
     * Deletes charta by id
     *
     * @param id
     */
    void deleteById(int id);
}
