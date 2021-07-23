package com.twdt.snerder;

import cn.hutool.core.convert.Convert;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author  MySelf
 * @create  2018/9/29
 * @desc Server监听端口主服务
 **/
public class Server {

    private ServerSocket serverSocket;

    /**
     * 监听端口
     * @param port
     */
    public Server(int port){
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("服务端启动成功，端口："+port);
        }catch (IOException e){
            System.out.println("服务端启动失败");
        }
    }

    /**
     * 线程模式，防止注释
     */
    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                doStart();
            }
        }).start();
    }

    /**
     * 启动客户端接受
     */
    private void doStart() {
        while (true){
            try {
                Socket client = serverSocket.accept();
                new ClientHandler(client).start();
            }catch (IOException e){
                System.out.println("服务端异常");
            }
        }
    }
    



    /**
     * 启动Server类线程
     */
    public static void main(String[] args) {
        Server server = new Server(Convert.toInt(ProducerConfig.port));
        server.start();
    }

}