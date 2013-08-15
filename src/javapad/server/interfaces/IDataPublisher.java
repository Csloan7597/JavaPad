package javapad.server.interfaces;

import javapad.shared.utils.JavaPadMessage;

public interface IDataPublisher
{
    boolean publishData(int id, JavaPadMessage message);
}
