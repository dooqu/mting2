package cn.xylink.mting.speech.event;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import cn.xylink.mting.speech.SpeechService;

public abstract  class SpeechServiceProxy
{
    ServiceConnection connection;
    boolean connected;
    ContextWrapper context;

    public SpeechServiceProxy(ContextWrapper context)
    {
        this.context = context;
        this.connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                SpeechServiceProxy.this.connected = true;
                onConnected(true, ((SpeechService.SpeechBinder)service).getService());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                SpeechServiceProxy.this.connected = false;
                onConnected(false, null);
            }
        };
    }



    public void bind()
    {
        this.context.bindService(new Intent(context, SpeechService.class), this.connection, 0);
    }

    public void unbind()
    {
        if(connected)
        {
            this.context.unbindService(this.connection);
        }
        this.context = null;
        this.connection = null;
        this.connected = false;
    }

    protected abstract void onConnected(boolean connected, SpeechService service);
}
