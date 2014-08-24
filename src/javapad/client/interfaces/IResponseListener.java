package javapad.client.interfaces;

import javapad.shared.utils.JavaPadMessage;

public interface IResponseListener
{
    void handleMessage(JavaPadMessage jpm);
}
