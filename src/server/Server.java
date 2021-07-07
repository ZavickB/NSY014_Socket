package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class Server {

	public static final int PORT = 3883;
	public static final String SERVER_ADRESS = "localhost";

	private int nbClients = 0;
	private boolean isActive = true;
	AsynchronousServerSocketChannel serverChannel;

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

		new Server().demarrage();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		reader.readLine();

	}

	private void demarrage() throws IOException, InterruptedException, ExecutionException {

		serverChannel = AsynchronousServerSocketChannel.open();
		InetSocketAddress adresseServeur = new InetSocketAddress("localhost", 3883);
		serverChannel.bind(adresseServeur);
		System.out.println("Port d'écoute : " + adresseServeur.getPort());
		System.out.println("Attente de connexion..");

		serverChannel.accept(serverChannel,
				new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {

					@Override
					public void completed(AsynchronousSocketChannel socketChannel,
							AsynchronousServerSocketChannel serverChannel) {
						System.out.println("Un client est connect..");
						LectureSocketAsynchrone(socketChannel);
						String messageDuServeur = "Reçu 5";
						ByteBuffer buffer = ByteBuffer.allocate(2048);
						buffer = ByteBuffer.wrap(messageDuServeur.getBytes());
						ecritureSocketAsynchrone(socketChannel, buffer);
					}

					@Override
					public void failed(Throwable exc, AsynchronousServerSocketChannel serverSock) {
						System.out.println("Impossible de se connecter vec le client");
					}

				});

	}

	private void LectureSocketAsynchrone(AsynchronousSocketChannel sockChannel) {
		// Le tampon de 2048
		final ByteBuffer buffer = ByteBuffer.allocate(2048);

		// Lecture du message avec un handler asynchrone
		sockChannel.read(buffer, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {

			@Override
			public void completed(Integer result, AsynchronousSocketChannel channel) {
				buffer.flip();
				String message = new String(buffer.array()).trim();
				System.out.println("Message reçu du client : " + message);

			}

			@Override
			public void failed(Throwable exc, AsynchronousSocketChannel channel) {
				System.out.println("Lecture du message impossible");
			}
		});
	}

	private void ecritureSocketAsynchrone(AsynchronousSocketChannel sockChannel, final ByteBuffer buffer) {
		sockChannel.write(buffer);
	}
}
