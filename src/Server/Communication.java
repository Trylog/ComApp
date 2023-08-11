package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Communication {//TODO make a singleton
	public ServerSocket serverSocket;
	public ArrayList<ClientHandler> clients;

	Communication(int port){
		try{
			serverSocket = new ServerSocket(port);
			System.out.println("Server created");
		} catch (java.io.IOException e){
			System.out.println("Problem appeared while creating server");
			e.printStackTrace();
		}

		while (true){
			try {
				clients.add(new ClientHandler(serverSocket.accept()));
				clients.get(-1).start();
				clients.get(-1).setName(String.valueOf(clients.size()));	//TODO slow
			} catch (java.io.IOException e){
				System.out.println("Problem appeared while accepting new connection");
				e.printStackTrace();
			}
		}

	}

	private class ClientHandler extends Thread {
		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;
		public ClientHandler(Socket socket){
			this.clientSocket = socket;
			System.out.println("New client connected");
		}

		@Override
		public void run(){
			try{
				out = new PrintWriter(clientSocket.getOutputStream());
			} catch (java.io.IOException e){
				System.out.println("Problem appeared while creating Writer");
				e.printStackTrace();
			}
			try{
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			} catch (java.io.IOException e){
				System.out.println("Problem appeared while creating a Reader");
				e.printStackTrace();
			}

			String line;

			while(true){
				try{
					line = in.readLine();
					if(line.charAt(0) == 1){
						clients.get(line.charAt(1)).send(line.substring(2));
					}

				} catch (java.io.IOException e){
					System.out.println("Problem appeared while reading a line");
					e.printStackTrace();
				}
			}
		}

		public synchronized void send(String messageIn){
			String messageOut = '1' + messageIn;
			out.println(messageOut);
		}
	}
}
