package com.distributed.distributed2017androidapp.Controller;

import android.os.AsyncTask;
import android.util.Log;

import model.Directions;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

/**
 * Created by tasos on 5/8/2017.
 */

public class HandleConnections extends AsyncTask<Object, Object, String>{
    Socket socket = null;
    ObjectInputStream objectInputStream = null;
    ObjectOutputStream objectOutputStream = null;
    String dstAddress;
    private int dstPort;
    String response = "";

    public void setAskedDirs(Directions askedDirs) {
        this.askedDirs = askedDirs;
    }

    Directions askedDirs, ourDirs;
    public HandleConnections(String address, int port, Directions askedDirs) {
        dstAddress = address;
        dstPort = port;
        this.askedDirs = askedDirs;
    }

    public Directions getOurDirs() {
        return ourDirs;
    }

    public Directions getAskedDirs() {
        return askedDirs;
    }

    public void setOurDirs(Directions dirs){
        this.ourDirs=dirs;
    }

    @Override
    public String doInBackground(Object... arg0) {
        try {
            if (socket == null) {
                socket = new Socket(dstAddress, dstPort);
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            }
            if (askedDirs == null)
                Log.e("isError", "iserrorrrrr");
            objectOutputStream.writeObject(this.getAskedDirs());
            objectOutputStream.flush();

            Object object = objectInputStream.readObject();
            this.setOurDirs((Directions) object);
            Log.i("Passed", "Got an input for data");
        }catch (EOFException eof){
            Log.e("message untill now", "eofaaaaa");
        } catch (UnknownHostException e) {
            Log.d("UnknownHostException  ",e.getMessage());
        } catch (IOException jh) {
            jh.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.d("ClassNotFoundException",e.getMessage());
        }catch (NullPointerException e){
            Log.d("Our dirs  ",e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                    objectInputStream.close();
                    objectOutputStream.close();
                    throw new InterruptedException();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException interrupt){
                    Log.d("THread interrupted", "interupt");
                }
            }
        }
        objectInputStream = null;
        objectOutputStream=null;
        socket=null;
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }

}
