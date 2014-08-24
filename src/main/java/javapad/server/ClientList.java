package javapad.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javapad.server.interfaces.IClientConnection;
import javapad.server.interfaces.IClientList;
import javapad.server.interfaces.IMessenger;
import javapad.server.interfaces.INotifier;


public class ClientList implements IClientList
{
    private final int limit;
    private final INotifier notifier;
    private final IMessenger messenger;

    private final Map<Integer, IClientConnection> clients = new HashMap<Integer, IClientConnection>();

    public ClientList(INotifier notifier, IMessenger messenger,
            int connectionLimit)
    {
        this.limit = connectionLimit;
        this.notifier = notifier;
        this.messenger = messenger;
    }

    @Override
    public synchronized void addClient(int id, IClientConnection a)
    {
        if (clients.size() < limit)
        {
            clients.put(id, a);
        }
    }

    @Override
    public synchronized void removeClient(IClientConnection a)
    {
        for (final int id : clients.keySet())
        {
            if (clients.get(id) == a)
            {
                removeClient(id);
            }
        }
    }

    @Override
    public synchronized void removeClient(int id)
    {
        clients.remove(id);
    }

    @Override
    public synchronized IClientConnection getById(int id)
    {
        return clients.get(id);
    }

    @Override
    public synchronized boolean isFull()
    {
        return clients.size() == limit;
    }

    @Override
    public synchronized Collection<IClientConnection> getAll()
    {
        return clients.values();
    }

}
