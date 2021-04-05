/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.baza_test.defect.util;

/**
 * The way to get sql scripts to update db.
 *
 * @author razumnov
 */
public interface DbSchemeUpdater {

    /**
     * Get all sql scripts in the source folder and execute every script that is
     * absent in the executed scripts db table. After executing script its name
     * added into the table.
     */
    void update();
}
