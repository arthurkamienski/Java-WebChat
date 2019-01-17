import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;


// classe que implementa thread de recebimento de mensagens
class Receiver extends Thread
{
	Infos infos;

	Receiver(Infos infos)
	{
		this.setInfos(infos);
	}

	// converte a hora em milissegundos para formato hh:mm:ss
	public String hora(String time)
	{
		return (new SimpleDateFormat("HH:mm:ss")).format(new Date(Long.valueOf(time)));
	}

	@Override
	public void run()
	{      
		int number = 0;
		Infos infos = this.getInfos();
		DatagramPacket packet;
		String[] campos;
		String mensagem;

		// Enquanto o socket estiver aberto
		while(!infos.getSocket().isClosed())
		{
			try
			{
				// Recebe pacote do socket e separa os campos
				packet = infos.receive();
				mensagem = new String(packet.getData());
				campos = mensagem.split(";");

				switch(campos[0])
				{
					// caso recebeu chat request e esta no estado idle
					case("CHT"):
						// transicao de estado para REQREC
						if(infos.testAndSwitchStatus(infos.IDLE, infos.REQREC))
						{
							// armazena as informacoes recebidas
							infos.setChtCode(Integer.parseInt(campos[2]));
							infos.setUsrCode(Integer.parseInt(campos[3]));
							infos.setUser(campos[4]);

							// informa usuario
							System.out.println("\n---->["+ hora(campos[1]) +"] Chat request received: " + infos.getUser() + " (" + packet.getAddress() + ")\n");
							System.out.print("\t\t\t\t\t");

							// espera para ver se o request vai ser aceito
							for(int i=0; !infos.isStatus(infos.CONNECTED) && i<30; i++)
								Thread.sleep(500);

							// se nao for aceito volta pra idle
							if(infos.testAndSwitchStatus(infos.REQREC, infos.IDLE))
								System.out.println("Request timeout");
						}
						break;
					case("ACC"):
						// caso recebeu chat request e se o request foi enviado anteriormente
						// transicao de estado para CONNECTED
						if(infos.testAndSwitchStatus(infos.REQSENT, infos.CONNECTED))
							// se o remetente eh o mesmo para quem enviou
							if(infos.check(Integer.parseInt(campos[2]),Integer.parseInt(campos[4])))
							{
								// armazena infos recebidas
								infos.setUsrCode(Integer.parseInt(campos[3]));
								infos.setUser(campos[5]);

								// informa usuario
								System.out.println("\n---->["+ hora(campos[1]) +"] Request accepted: " + infos.getUser() + " (" + packet.getAddress() + ")\n");
								System.out.println("---->Connected\n");
								System.out.print("\t\t\t\t\t");
							}
						break;
					case("MSG"):
						// se esta conectado e o remetente eh o mesmo da conversa atual
						if(infos.isStatus(infos.CONNECTED))
							if(infos.check(Integer.parseInt(campos[2]), Integer.parseInt(campos[3]), Integer.parseInt(campos[4])))
							{
								if(Integer.parseInt(campos[5]) > number)
									infos.send("ERR;" + number + ";");
								else if(Integer.parseInt(campos[5]) == number)
								{
									// exibe mensagem
									System.out.println("\r["+ hora(campos[1]) +"] " + infos.getUser() + ": " + campos[6]);
									System.out.print("\t\t\t\t\t");
								
									number++;
								}
							}
						break;
					case("BYE"):
						// se esta conectado e o remetente eh o mesmo da conversa atual
						// transicao de estado para idle
						if(infos.testAndSwitchStatus(infos.CONNECTED, infos.IDLE))
							if(infos.check(Integer.parseInt(campos[2]), Integer.parseInt(campos[3]), Integer.parseInt(campos[4])))
							{
								// informa usuario
								System.out.println("\n---->["+ hora(campos[1]) +"] User disconnected\n");
								System.out.print("\t\t\t\t\t");
							}
						break;
					case("ERR"):
							infos.resend(Integer.parseInt(campos[1]));
						break;
					default:
						break;
				}
			}
			catch(InterruptedException ie)
			{
				System.err.println("Thread interrupted");
			}
			catch(IOException e)
			{
				if(!infos.getSocket().isClosed())
					System.err.println("IOException");
			}
		}
	}

	public void setInfos(Infos infos)
	{
		this.infos = infos;
	}

	public Infos getInfos()
	{
		return this.infos;
	}
}