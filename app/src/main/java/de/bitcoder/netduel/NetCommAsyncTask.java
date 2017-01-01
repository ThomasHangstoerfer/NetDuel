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
    private static final String TAG = "MyAsyncTask";
    Activity activity;
    TextView textView;
    IOException ioException;
    StringBuilder outputStringBuilder;
    ServerSocket listener = null;
    Socket socket;
    String server_ip;
    int server_port;
    private boolean isServer;

    public NetCommAsyncTask(Activity activity, TextView textView, boolean isServer, String server_ip/* can be empty if server*/, int server_port) {
        super();
        this.activity = activity;
        this.textView = textView;
        this.isServer = isServer;
        this.server_ip = server_ip;
        this.server_port = server_port;
        this.ioException = null;
        this.listener = null;
        this.socket = null;
    }

    public void stop()
    {
        try{
            socket.close();
            listener.close();

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    NetCommAsyncTask.this.textView.append("Server stopped\n");
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
                    Log.d(this.TAG, String.format("Connecting to server %s:%d", server_ip, server_port));
                    socket = new Socket(serverAddr, server_port);
                }

                Log.d(this.TAG, String.format("client connected from: %s", socket.getRemoteSocketAddress().toString()));
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        NetCommAsyncTask.this.textView.append("Client connected: " + socket.getRemoteSocketAddress().toString() +"\n");
                    }
                });

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bwr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                PrintWriter pwrout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                pwrout.println("Hey Server!");
                PrintStream out = new PrintStream(socket.getOutputStream());
                for (String inputLine; (inputLine = in.readLine()) != null;) {
                    Log.d(this.TAG, "received:" + inputLine);
                    outputStringBuilder = new StringBuilder("");
                    char inputLineChars[] = inputLine.toCharArray();
                    for (char c : inputLineChars) {
                        //outputStringBuilder.append(Character.toChars(c + 1));
                        outputStringBuilder.append(Character.toChars(c ));
                    }
                    out.println(outputStringBuilder);

                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            NetCommAsyncTask.this.textView.append(outputStringBuilder+"\n");
                        }
                    });
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
