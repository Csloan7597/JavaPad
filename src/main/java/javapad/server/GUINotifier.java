package javapad.server;

import java.util.HashMap;
import java.util.Observable;

import javapad.server.interfaces.INotifier;


public class GUINotifier extends Observable implements INotifier
{
    HashMap<Type, Object> toSend = new HashMap<Type, Object>();

    @Override
    public synchronized void sendToConsole(String message)
    {
        toSend.put(Type.CONSOLE_MESSAGE, message);
        this.setChanged();
        this.notifyObservers(toSend);
        toSend.clear();
    }

    @Override
    public synchronized void sendStackTrace(StackTraceElement[] stackTrace)
    {
        toSend.put(Type.STACK_TRACE, stackTrace);
        this.setChanged();
        this.notifyObservers(toSend);
        toSend.clear();
    }

    @Override
    public synchronized void sendError(Exception e)
    {
        toSend.put(Type.ERROR_EXCEPTION, e);
        this.setChanged();
        this.notifyObservers(toSend);
        toSend.clear();
    }

    @Override
    public synchronized void sendError(String errorMessage)
    {
        toSend.put(Type.ERROR_MESSAGE, errorMessage);
        this.setChanged();
        this.notifyObservers(toSend);
        toSend.clear();
    }

}
