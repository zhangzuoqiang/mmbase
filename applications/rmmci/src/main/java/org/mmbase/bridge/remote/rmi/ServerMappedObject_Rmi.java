/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.remote.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

import org.mmbase.bridge.remote.ServerMappedObject;
import org.mmbase.bridge.remote.util.ObjectWrapper;
import org.mmbase.bridge.remote.util.StubToLocalMapper;


/**
 * @javadoc
 */
public abstract class ServerMappedObject_Rmi<O> extends UnicastRemoteObject implements ServerMappedObject, Unreferenced {

    //original object
    private O originalObject;

    //mapper code
    private String mapperCode = null;

    //stub port
    private int port = 1100;

    public ServerMappedObject_Rmi(O originalObject, int port) throws RemoteException{
       super(port);
       this.originalObject = originalObject;
       this.mapperCode = StubToLocalMapper.add(this.originalObject);
       this.port = port;
    }
    
    public String getMapperCode() {
       return mapperCode;
    }

    protected O getOriginalObject() {
        return originalObject;
    }

    protected int getPort() {
       return port;
    }
    
    //clean up StubToLocalMapper when the class is unreferenced
    public void unreferenced() {
       if (StubToLocalMapper.remove(mapperCode)){
          mapperCode = null;
       }
    }


    public java.lang.String wrapped_toString() {
        java.lang.String retval = getOriginalObject().toString();
        return retval;
    }

    public int wrapped_hashCode() {
        int retval = getOriginalObject().hashCode();
        return retval;
    }

    public boolean wrapped_equals(java.lang.Object arg0) throws RemoteException {
        boolean retval = getOriginalObject().equals(
                ObjectWrapper.rmiObjectToLocal(arg0));
        return retval;
    }

}
