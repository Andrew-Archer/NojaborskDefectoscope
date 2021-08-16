/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author razumnov
 */
public class ObjectClonner {

    private static final Logger log = LoggerFactory.getLogger(ObjectClonner.class);

    private static ObjectClonner instance;

    private ObjectClonner() {
    }

    public static ObjectClonner getInstance() {

        if (instance == null) {
            return new ObjectClonner();
        }
        return instance;
    }

    private byte[] serialize(Object oldObj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos);) {
            oos.writeObject(oldObj);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            throw (ex);
        }
    }

    private Object deserialize(byte[] serialized) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bin = new ByteArrayInputStream(serialized);
                ObjectInputStream ois = new ObjectInputStream(bin);) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw (ex);
        }
    }

    public <T> T deepCopy(T oldObj) throws Exception {
        return (T) deserialize(serialize(oldObj));
    }
}
