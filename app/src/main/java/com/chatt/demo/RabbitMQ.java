package com.chatt.demo;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chatt.demo.custom.CustomActivity;
import com.chatt.demo.model.Message;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import static android.R.attr.id;

/**
 * Created by danil on 25/10/2016.
 */

public class RabbitMQ extends CustomActivity {

    private static final String CLOUDAMPQ_HOST = "reindeer.rmq.cloudamqp.com";
    private static final String CLOUDAMPQ_USR = "usngqcwq";
    private static final String CLOUDAMOQ_PWD = "OzkcoxFQqcEo4zDuSKTK0aWA9P9exzW_";
    Connection connection = null;
    Channel channel = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String user = getIntent().getStringExtra("user");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(CLOUDAMPQ_HOST);
        factory.setUsername(CLOUDAMPQ_USR);
        factory.setPassword(CLOUDAMOQ_PWD);
        factory.setVirtualHost(CLOUDAMPQ_USR);
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(user, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.rabbitmq);

        setTouchNClick(R.id.enviar);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        String user = getIntent().getStringExtra("user");
        EditText cont = (EditText) findViewById(R.id.content);
        EditText se = (EditText) findViewById(R.id.sendto);
        String content = cont.getText().toString();
        String sendTo = se.getText().toString();
        try {
            Message msg = new Message();
            msg.setContent(content);
            msg.setSender(user);
            channel.basicPublish("", sendTo, null, messageToXML(msg).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String messageToXML(Message msg) throws Exception {
        Serializer serializer = new Persister();
        File result = new File("example.xml");
        StringWriter resultado = new StringWriter();
        serializer.write(msg, resultado);
        return resultado.toString();

    }


}
