import java.io.*;
import java.net.*;

public class Chat
{
	public static void main(String args[]) throws Exception
	{
		// inicializa o objeto que guarda informacoes
		Infos infos = new Infos(new DatagramSocket(13337));

		// informa a porta para o usuario
		// System.out.println("Porta local: " + infos.getSocket().getLocalPort() + "\n");

		// inicializa o sneder e o receiver e inicia
		Receiver receiver = new Receiver(infos);
		Sender sender = new Sender(infos);

		receiver.start();
		sender.start();

		// quando o sender para, para o receiver
		infos.getSocket().close();
	}
}