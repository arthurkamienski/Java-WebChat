import java.io.*;
import java.net.*;
import java.util.ArrayList;

class Sender
{
	Infos infos;

	Sender(Infos infos)
	{
		this.setInfos(infos);
	}

	public void start() throws Exception
	{
		int number = 0;
		Infos infos = this.getInfos();
		boolean ok = false;
		String campos[], user, mensagem = "";

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Digite seu nome:");
		user = inFromUser.readLine();

		// enquanto o endereco IP nao estiver correto, requisita o usuario por um
		while(!ok)
			try
			{
				System.out.println("\nDigite o IP da pessoa quem quer conversar:");

				// para uso padrao
				infos.setIPAddress(InetAddress.getByName(inFromUser.readLine()));

				ok = true;
			}  
			catch(UnknownHostException uhe)
			{
				System.out.println("Endereco invalido.\n");
			}

		infos.setPort(13337);

		// ok = false;

		// enquanto a porta nao estiver correta, requisita o usuario por uma
		// while(!ok)
		// 	try
		// 	{
		// 		System.out.println("\nDigite a porta:");
		// 		infos.setPort(Integer.parseInt(inFromUser.readLine()));

		// 		ok = true;
		// 	}  
		// 	catch(NumberFormatException uhe)
		// 	{
		// 		System.out.println("Porta invalida.\n");
		// 	}

		System.out.println("\n------------------------------------------------------------\n");

		// infos dos comando para usuario
		System.out.println("Comandos:");
		System.out.println("#connect:\t conectar-se ao IP e porta designadas");
		System.out.println("#accept:\t aceitar um request");
		System.out.println("#bye:\t\t desconectar da conversa");
		System.out.println();

		// enquanto nao pedir pra sair
		while(ok)
		{	
			System.out.print("\r\t\t\t\t\t");
			mensagem = inFromUser.readLine();

			switch(mensagem)
			{

				// se for pra conectar e estiver no estado idle
				case("#connect"):
					//testa e muda estado pra REQSENT
					if(infos.testAndSwitchStatus(infos.IDLE, infos.REQSENT))
					{
						// define um novo chtcode
						infos.setChtCode((int) (Math.random()*10000));
						// monta a mensagem de requisicao
						mensagem = "CHT;" + (new java.util.Date()).getTime() + ";" + infos.getChtCode() + ";" + infos.getMyCode() + ";" + user + ";";
						// Envia e informa usuario
						infos.send(mensagem);
						System.out.println("\n---->Chat request sent\n");

						System.out.print("\r\t\t\t\t\t");

						// espera pra ver se conectou
						for(int i=0; !infos.isStatus(infos.CONNECTED) && i<30; i++)
							Thread.sleep(500);

						// se nao foi aceito dentro do tempo, volta pra idle e informa usuario
						if(infos.testAndSwitchStatus(infos.REQSENT, infos.IDLE))
							System.out.println("\n---->Connection failed\n");
					}
					else
						System.out.println("Already connected/waiting response");

					break;
				// se for pra aceitar requisicao
				case("#accept"):
					// se recebeu uma requisicao, muda pra conectado
					if(infos.testAndSwitchStatus(infos.REQREC, infos.CONNECTED))
					{
						// monta mensagem de envio
						mensagem = "ACC;" + (new java.util.Date()).getTime() + ";" + infos.getChtCode() + ";" + infos.getMyCode() + ";" + infos.getUsrCode() + ";" + user + ";";
						// envia mensagem
						infos.send(mensagem);
						// informa usuario
						System.out.println("\n---->Request accepted");
						System.out.println("---->Connected\n");
					}
					else
						System.out.println("No request received");
					break;
				case("#bye"):
					// se estiver conectado muda pra idle
					if(infos.testAndSwitchStatus(infos.CONNECTED, infos.IDLE))
					{
						// monta mensagem
						mensagem = "BYE;" + (new java.util.Date()).getTime() + ";" + infos.getChtCode() + ";" + infos.getMyCode() + ";" + infos.getUsrCode() + ";";
						// envia mensagem
						infos.send(mensagem);
						// informa usuario
						System.out.println("\n---->Disconnected\n");
						ok = false;
					}
					else
						System.out.println("Not connected");
					break;
				// se nao for um dos outros comandos, eh uma mensagem normal
				default:
					// se estiver conectado
					if(infos.isStatus(infos.CONNECTED))
					{
						// monta mensagem
						mensagem = "MSG;" + (new java.util.Date()).getTime() + ";" + infos.getChtCode() + ";" + infos.getMyCode() + ";" + infos.getUsrCode() + ";" + (number++) + ";" + mensagem + ";";
						infos.addMensagem(mensagem);

						// envia 
						infos.send(mensagem);
					}
					else
						System.out.println("Not connected");
					break;
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