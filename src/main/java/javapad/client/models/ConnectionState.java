package javapad.client.models;

import java.util.function.BiFunction;

/**
 * Created by conor on 20/10/2014.
 */
public class ConnectionState {

    public enum ConnectionStatus
    {
        CONNECTED_NO_CONTROL, CONNECTED_IN_CONTROL, DISCONNECTED;
    }
}
