package javapad.server;

import javapad.server.interfaces.ILock;

public class Lock implements ILock
{
    private int lockOwner = -1;

    @Override
    public synchronized boolean getLock(int id)
    {
        if (lockOwner == -1)
        {
            lockOwner = id;
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean releaseLock(int id)
    {
        if (lockOwner == id)
        {
            lockOwner = -1;
            return true;
        }
        return false;
    }

    @Override
    public int getLockOwnerId()
    {
        return lockOwner;
    }

}
