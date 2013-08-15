package javapad.server.interfaces;

public interface ILock
{
    boolean getLock(int id);

    boolean releaseLock(int id);

    int getLockOwnerId();
}
