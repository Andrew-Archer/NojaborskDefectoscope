/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.baza_test.defect.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.npptmk.bazaTest.defect.model.UpdateScript;

/**
 * Used to load all update scripts from class path.
 *
 * @author razumnov
 */
public class FromClassPathSQLUpdater implements DbSchemeUpdater {

    private static final Logger log = LoggerFactory.getLogger(FromClassPathSQLUpdater.class);
    private static final String SCRIPTS_FOLDER = "ru/npp/sql/";
    private final EntityManagerFactory emf;
    private static final String TX_ROLLED_BACK_MSG = "Transaction has been rolled back.";
    private static final String EM_CLOSED_MSG = "Entity manger has been closed.";

    public FromClassPathSQLUpdater(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void update() {
        List<String> appliedScripts = getAlreadyAppliedScripts();
        List<File> classPathScripts = loadScriptsFromClassPath();
        List<File> notAppliedFiles = classPathScripts.stream()
                .filter(t -> !appliedScripts.contains(t.getName()))
                .collect(Collectors.toList());
        notAppliedFiles.forEach(this::executeUpdateScript);
    }

    private List<String> getAlreadyAppliedScripts() {
        List<UpdateScript> updatedScripts = new ArrayList<>();
        log.debug("Start #getAlreadyAppliedScripts()");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            updatedScripts.addAll(em.createNamedQuery("getAll", UpdateScript.class)
                    .getResultList());
            tx.commit();
            log.debug("Has found {} applied scripts.", updatedScripts.size());
        } catch (RuntimeException e) {
            log.error("Error cetting executed update scripts.", e);
            if (tx != null && tx.isActive()) {
                tx.rollback();
                log.error(TX_ROLLED_BACK_MSG);
            }
        } finally {
            em.close();
            log.debug(EM_CLOSED_MSG);
        }
        return updatedScripts.stream()
                .map(UpdateScript::getFileName)
                .collect(Collectors.toList());
    }

    private List<File> loadScriptsFromClassPath() {
        log.debug("Start #loadScriptsFromClassPath()");
        ClassLoader classLoader = getClass().getClassLoader();

        URL url = classLoader.getResource(SCRIPTS_FOLDER);
        if (url == null) {
            return Collections.emptyList();
        }
        File folder = new File(url.getFile());

        File[] files = folder.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        List<File> sqlFiles = Arrays
                .stream(files)
                .filter(file -> file.getName().endsWith(".sql"))
                .collect(Collectors.toList());
        String debugString = sqlFiles.size() == 1 ?
                "There are {} scripts have been found in {} folder" :
                "There is {} script has been found in {} folder";
        log.debug(debugString,
                sqlFiles.size(),
                folder.getAbsolutePath());
        return sqlFiles;
    }

    private void executeUpdateScript(File file) {
        log.debug("Start #executeUpdateScript()");
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try (BufferedReader fis = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            StringBuilder stringBuffer = new StringBuilder();
            String sqlLine = fis.readLine();

            while (sqlLine != null) {
                stringBuffer.append(sqlLine);
                sqlLine = fis.readLine();
            }
            List<String> sqlForTransaction = Arrays.asList(stringBuffer.toString().split("\\^"));
            Map<Integer, List<String>> transactionToSqlLines = new HashMap<>();
            for (int i = 0; i < sqlForTransaction.size(); i++) {
                transactionToSqlLines.put(i,
                        Arrays.asList(sqlForTransaction.get(i).split(";")));
            }
            for (List<String> sqlsInOneTx : transactionToSqlLines.values()) {
                tx = em.getTransaction();
                tx.begin();
                sqlsInOneTx.forEach(sql -> em.createNativeQuery(sql).executeUpdate());
                tx.commit();

            }
            markScriptAsExecuted(file);
        } catch (Exception ex) {
            log.error("Can't execute update query from {} file", file, ex);
            if (tx != null && tx.isActive()) {
                tx.rollback();
                log.error(TX_ROLLED_BACK_MSG);

            }
        } finally {
            em.close();
            log.debug(EM_CLOSED_MSG);
        }
    }

    private void markScriptAsExecuted(File file) {
        log.debug("Start #markScriptAsExecuted()");
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {

            tx = em.getTransaction();
            tx.begin();
            em.persist(new UpdateScript(file.getName()));
            tx.commit();

        } catch (Exception ex) {
            log.error("Can't create update script in db from  {} file", file, ex);
            if (tx != null && tx.isActive()) {
                tx.rollback();
                log.error(TX_ROLLED_BACK_MSG);
            }
        } finally {
            em.close();
            log.debug(EM_CLOSED_MSG);
        }
    }

}
