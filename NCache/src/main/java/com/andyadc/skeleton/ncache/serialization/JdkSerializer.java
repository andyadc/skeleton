package com.andyadc.skeleton.ncache.serialization;

import java.io.*;

/**
 * JDK serialization implementation.
 */
public class JdkSerializer<T> implements Serializer<T> {

    @Override
    public byte[] serialize(T object) throws SerializationException {
        if (object == null) {
            return null;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize object", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Failed to deserialize object", e);
        }
    }

    @Override
    public String getContentType() {
        return "application/x-java-serialized-object";
    }

}
