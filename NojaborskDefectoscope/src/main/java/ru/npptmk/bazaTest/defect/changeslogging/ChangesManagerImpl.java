package ru.npptmk.bazaTest.defect.changeslogging;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.npptmk.bazaTest.defect.model.Operator;
import ru.npptmk.bazaTest.defect.model.SettingChange;
import ru.npptmk.bazaTest.defect.model.SettingsChangeEvent;

public class ChangesManagerImpl implements ChangesManager {

    private static final Logger LOG = LoggerFactory.getLogger(ChangesManagerImpl.class);
    private final List<SettingsChangeEvent> settingsChangeEvents;
    private final EntityManagerFactory emf;

    public ChangesManagerImpl(EntityManagerFactory emf) {
        this.emf = emf;
        settingsChangeEvents = new ArrayList<>();
    }

    @Override
    public SettingsChangeEvent getSettingsChangesEvent(Operator operator) {
        for (SettingsChangeEvent settingsChangeEvent : settingsChangeEvents) {
            if (settingsChangeEvent.getAuthor().equals(operator)) {
                LOG.debug("Returning change event {}", settingsChangeEvent);
                return settingsChangeEvent;
            }
        }
        return null;
    }

    @Override
    public void persistChanges() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        Date eventDate = new Date();
        try {
            tx = em.getTransaction();
            tx.begin();
            settingsChangeEvents.forEach((settingsChangeEvent) -> {
                if (!settingsChangeEvent.getSettingsChanges().isEmpty()) {
                    settingsChangeEvent.setEventDate(eventDate);
                    em.persist(settingsChangeEvent);
                    settingsChangeEvent.getSettingsChanges().forEach(settingChange -> {
                        em.persist(settingChange);
                    });
                }
            });
            tx.commit();
        } catch (Exception ex) {
            LOG.error("Can't save changes to db.", ex);
            if (tx != null && tx.isActive()) {
                LOG.debug("Rolling back transaction {} has been rolled back.", tx);
                tx.rollback();
                LOG.debug("Transaction {} has been rolled back.", tx);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        settingsChangeEvents.clear();
    }

    @Override
    public void clearAllEvents() {
        settingsChangeEvents.clear();
    }
    
    @Override
    public void addChange(Operator operator, String paramGroupName, String paramName, String oldValue, String newValue){
        if (oldValue.equals(newValue)) {
            return;
        }
        SettingChange settingChange = new SettingChange();
        settingChange.setParamGroup(paramGroupName);
        settingChange.setParamName(paramName);
        settingChange.setOldValue(oldValue);
        settingChange.setNewValue(newValue);
        this
                .getSettingsChangesEvent(operator)
                .getSettingsChanges()
                .add(settingChange);
    }

    @Override
    public SettingsChangeEvent addChangesEvent(Operator operator) {
        for (SettingsChangeEvent existedChangeEvent : settingsChangeEvents) {
            if (existedChangeEvent.getAuthor().equals(operator)) {
                return existedChangeEvent;
            }
        }
        SettingsChangeEvent settingsChangeEvent = new SettingsChangeEvent(operator);
        settingsChangeEvents.add(settingsChangeEvent);
        return settingsChangeEvent;
    }

}
