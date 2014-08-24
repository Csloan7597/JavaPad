package javapad.server;

import javapad.server.interfaces.IClientConnection;
import javapad.server.interfaces.IClientList;
import javapad.server.interfaces.IDataPublisher;
import javapad.server.interfaces.ILock;
import javapad.shared.utils.JavaPadMessage;

public class Publisher implements IDataPublisher
{

    private final ILock lock;
    private final IClientList clientList;

    public Publisher(ILock lock, IClientList clientList)
    {
        this.lock = lock;
        this.clientList = clientList;
    }

    @Override
    public synchronized boolean publishData(int id, JavaPadMessage message)
    {
        if (lock.getLockOwnerId() != id)
        {
            return false;
        }

        for (final IClientConnection client : clientList.getAll())
        {
            if (client.getId() == id)
            {
                continue;
            }
            client.sendMessage(message);
        }
        return true;
    }
}
