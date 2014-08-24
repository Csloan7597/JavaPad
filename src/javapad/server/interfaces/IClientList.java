package javapad.server.interfaces;

import java.util.Collection;

public interface IClientList
{
    void addClient(int id, IClientConnection a);

    void removeClient(IClientConnection a);

    void removeClient(int id);

    IClientConnection getById(int id);

    Collection<IClientConnection> getAll();

    boolean isFull();
}
