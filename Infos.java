import java.io.*;
import java.net.*;
import java.util.ArrayList;

class Infos
{
	// constantes que definem estados
	static final int IDLE			= 0;
	static final int CONNECTED 		= 1;
	static final int REQSENT 		= 2;
	static final int REQREC 		= 3;

	int status, port, usrCode, myCode, chtCode;
	String user;
	InetAddress IPAddress;
	DatagramSocket socket;
	ArrayList<String> mensagens;

	Infos(DatagramSocket socket)
	{
		this.setSocket(socket);
		// this.setPort(13337);
		this.setMyCode((int) (Math.random()*10000));
		this.setStatus(IDLE);
		this.setMensagens(new ArrayList<>());
	}

	// verifica o estado
	public synchronized boolean isStatus(int currStatus)
	{
		return this.getStatus() == currStatus;
	}

	// verifica o estado e troca se for o estado correto
	public synchronized boolean testAndSwitchStatus(int currStatus, int newStatus)
	{
		boolean ok = false;

		if(this.getStatus() == currStatus)
		{
			this.setStatus(newStatus);
			ok = true;
		}

		return ok;
	}

	public void resend(int number) throws IOException
	{
		while(number < this.getMensagens().size())
			this.send(this.getMensagem(number++));
	}

	// recebe do socket
	public DatagramPacket receive() throws IOException
	{
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		this.getSocket().receive(receivePacket);
		return receivePacket;
	}

	// envia pelo socket
	public void send(String mensagem) throws IOException
	{
		byte[] sendData = new byte[1024];

		sendData = mensagem.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.getIPAddress(), this.getPort());
		this.getSocket().send(sendPacket);
	}

	// para envio de mensagens de erro, quando o destinatario nao necessariamente eh o mesmo da conversa
	public void send(String mensagem, InetAddress address, int port) throws IOException
	{
		byte[] sendData = new byte[1024];

		sendData = mensagem.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
		this.getSocket().send(sendPacket);
	}

	// verifica se as infos estao corretas
	public boolean check(int chtCode, int usrCode, int myCode)
	{
		return (chtCode == this.getChtCode() && myCode == this.getMyCode() && usrCode == this.getUsrCode());
	}

	// verifica se as infos estao corretas quando nao sabe o usrCode ainda
	public boolean check(int chtCode, int myCode)
	{
		return (chtCode == this.getChtCode() && myCode == this.getMyCode());
	}

	public void addMensagem(String mensagem)
	{
		this.getMensagens().add(mensagem);
	}

	public String getMensagem(int numero)
	{
		return this.getMensagens().get(numero);
	}

	public void setSocket(DatagramSocket socket)
	{
		this.socket = socket;
	}

	public DatagramSocket getSocket()
	{
		return this.socket;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getUser()
	{
		return this.user;
	}

	public void setIPAddress(InetAddress IPAddress)
	{
		this.IPAddress = IPAddress;
	}

	public InetAddress getIPAddress()
	{
		return this.IPAddress;
	}

	private void setStatus(int status)
	{
		this.status = status;
	}

	private int getStatus()
	{
		return this.status;
	}

	public void setUsrCode(int usrCode)
	{
		this.usrCode = usrCode;
	}

	public int getUsrCode()
	{
		return this.usrCode;
	}

	public void setMyCode(int myCode)
	{
		this.myCode = myCode;
	}

	public int getMyCode()
	{
		return this.myCode;
	}

	public void setChtCode(int chtCode)
	{
		this.chtCode = chtCode;
	}

	public int getChtCode()
	{
		return this.chtCode;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getPort()
	{
		return this.port;
	}

	public ArrayList<String> getMensagens()
	{
		return this.mensagens;
	}

	public void setMensagens(ArrayList<String> mensagens)
	{
		this.mensagens = mensagens;
	}
}