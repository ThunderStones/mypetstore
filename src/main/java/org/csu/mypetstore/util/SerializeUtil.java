package org.csu.mypetstore.util;


import org.csu.mypetstore.domain.Cart;

import java.io.*;


public class SerializeUtil {
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        byte[] string = byteArrayOutputStream.toByteArray();
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return string;
    }
    public static Object serializeToObject(byte[] str) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return object;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        byte[] s = SerializeUtil.serialize(new Cart());
        Cart cart = (Cart) serializeToObject(s);
    }
}

