package client_socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client {
	public static final int PORT = 3883;
	public static final String SERVER_ADRESS = "localhost";
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		
		// On ouvre un client socket asynchrone en local
		AsynchronousSocketChannel client =
				AsynchronousSocketChannel.open();
		
		// On lui indique l'adresse du serveur
		InetSocketAddress adresseServeur = new InetSocketAddress( SERVER_ADRESS, PORT );
		
		// On connecte le client sur le 
		
		Future<Void> future = client.connect( adresseServeur );
		future.get();
 		
		// La connexion est ok, les programmes communiquent
		
		while(true) {
			// On lit la premiere ligne
	        BufferedReader reader = new BufferedReader( 
	            new InputStreamReader(System.in));  
	        String messageClient = reader.readLine(); 
	  
	        // On transforme le message lu en tampon d'octets
			ByteBuffer buffer = ByteBuffer.wrap( messageClient.getBytes());
			// On ecrit le contenu sur le socket client et on l'envoie sur le serveur
			Future<Integer> result = client.write( buffer );
			
			//On attends la fin de l'ecriture
			while( ! result.isDone() ) {}
			
			// On vide le buffer
			buffer.clear();
			
			// On alloue 128 octets au tampon
			buffer = ByteBuffer.allocate(128);
			
			// On demande la lecture sur la socket
			// en attendant la réponse du serveur
			result = client.read(buffer);

			// On attend la réponse du serveur
			while (!result.isDone()) {
			}

			// On tronque le tampon à la taille des octets reçus
			buffer.flip();
			// On convertit le contenu du tampon en chaine
			String messageServeur = new String(buffer.array()).trim();
			// On affiche le message reçu du serveur
			System.out.println("Message du serveur : " + messageServeur);
			
			// Si le message est 'Quit' on sort de la boucle.
			if ( messageClient.equals( "Quit")) {
				break;
			}
			
		}		
		// On ferme le socket 
		client.close();
	}

}
