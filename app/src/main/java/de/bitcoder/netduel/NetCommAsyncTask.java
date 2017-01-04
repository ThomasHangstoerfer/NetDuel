package de.bitcoder.netduel;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by thomas on 01.01.2017.
 */

public class NetCommAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "NetCommAsyncTask";
    MainActivity activity;
    TextView textView;
    IOException ioException;
    StringBuilder outputStringBuilder;
    ServerSocket listener = null;
    Socket socket;
    String server_ip;
    int server_port;
    private boolean isServer;
    PrintWriter sendWriter;
    Thread sendThread = null;
    Object waiter = new Object();
    String message = null;

    public NetCommAsyncTask(Activity activity, TextView textView, boolean isServer, String server_ip/* can be empty if server*/, int server_port) {
        super();
        this.activity = (MainActivity) activity;
        this.textView = textView;
        this.isServer = isServer;
        this.server_ip = server_ip;
        this.server_port = server_port;
        this.ioException = null;
        this.listener = null;
        this.socket = null;

        sendThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        synchronized (waiter)
                        {
                            waiter.wait();
                        }
                        if ( message != null )
                            sendWriter.println(message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        sendThread.start();
    }

    public void stop()
    {
        try{
            socket.close();

            if ( listener != null )
                listener.close();

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.log(TAG, "Server stopped");
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(final String msg) {
        if ( sendWriter != null)
        {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.log(TAG, String.format("sendMessage('%s')", msg));
                }
            });
            message = msg;
            //sendWriter.println(msg);
            synchronized (waiter) {
                waiter.notifyAll();

            }
        }
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            while (!isCancelled()) {
                if ( isServer )
                {
                    listener = new ServerSocket(server_port);
                    Log.d(this.TAG, String.format("waiting for client on port %d", server_port));
                    socket = listener.accept();
                }
                else
                {
                    InetAddress serverAddr = InetAddress.getByName(server_ip);
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            activity.log(TAG, String.format("Connecting to server %s:%d", server_ip, server_port));
                        }
                    });
                    socket = new Socket(serverAddr, server_port);
                }

                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        activity.log(TAG, "Client connected: " + socket.getRemoteSocketAddress().toString());
                    }
                });

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bwr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                sendWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                if ( !isServer)
                    sendMessage("hello");

                //PrintStream out = new PrintStream(socket.getOutputStream());
                for (String inputLine; (inputLine = in.readLine()) != null;) {
                    Log.d(this.TAG, "received:" + inputLine);
                    final String s = inputLine;

                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            activity.handleMessage(s);
                        }
                    });

                    /*
                    outputStringBuilder = new StringBuilder("");
                    char inputLineChars[] = inputLine.toCharArray();
                    for (char c : inputLineChars) {
                        //outputStringBuilder.append(Character.toChars(c + 1));
                        outputStringBuilder.append(Character.toChars(c ));
                    }
                    out.println(outputStringBuilder);
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            activity.log(outputStringBuilder);
                        }
                    });

                    */
                }
            }
        } catch(IOException e) {
            Log.d(this.TAG, e.toString());
        }

        StringBuilder sb = new StringBuilder();
            /*
            try {
                Socket socket = new Socket(
                        params[0],
                        Integer.parseInt(params[1]));
                OutputStream out = socket.getOutputStream();
                out.write(params[2].getBytes());
                InputStream in = socket.getInputStream();
                byte buf[] = new byte[1024];
                int nbytes;
                while ((nbytes = in.read(buf)) != -1) {
                    sb.append(new String(buf, 0, nbytes));
                }
                socket.close();
            } catch(IOException e) {
                this.ioException = e;
                return "error";
            }
            */
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (this.ioException != null) {
            new AlertDialog.Builder(this.activity)
                    .setTitle("An error occurrsed")
                    .setMessage(this.ioException.toString())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            this.textView.setText(result);
        }
    }
}
