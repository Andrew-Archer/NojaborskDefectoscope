package ru.npptmk.bazaTest.defect.changeslogging;

import ru.npptmk.bazaTest.defect.model.Operator;
import ru.npptmk.bazaTest.defect.model.SettingsChangeEvent;

/**
 *
 * @author razumnov
 */
public interface ChangesManager {

    /**
     * Return existing event or null if nothing has been found.
     *
     * @param operator how's making changes.
     * @return event unique bound to operator and parameters group or null.
     */
    SettingsChangeEvent getSettingsChangesEvent(Operator operator);

    /**
     * Write all added changes to db.
     */
    void persistChanges();
    
    void clearAllEvents();
    
    void addChange(Operator operator, String paramGroupName, String paramName, String oldValue, String newValue);

    /**
     * Add new event and return it or just return existing if it's already
     * exist.
     *
     * @param operator operator making changes.
     * @return added or existing event that has the same operator.
     */
    SettingsChangeEvent addChangesEvent(Operator operator);

}
