package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	int z, numero = 0;
	int temm[] = new int[100];
	int mostrar = 0;

	String c = null;
	String t[][] = new String[10][10];
	
	private ServerSocket serverSocket;
	
	private void criarServerSocket(int porta) throws IOException {
		//criando socket para conexao com a porta passada por parametro
		serverSocket = new ServerSocket(porta);

	}
	private Socket esperaConxao() throws IOException {
		//aceitando conexao do socket com o client
		Socket socket = serverSocket.accept();
		return socket;
	}
	private void fechaSocket(Socket sckt) throws IOException {
		//encerrando socket
		sckt.close();
	}
	private String armazena(String tem, String msgs, int op, int opm) {
		//armazendo conteudo enviado pelo cliente
		if (op == 1 && opm == 1) {//se o cliente for produtor
			for (int i = 0; i <= z; i++) {//checagem para ver se o tema existe
				if (tem.equals(t[i][0])) {
					return "1";
				}
			}
			t[z][0] = tem;//o vetor vai para a posicao vazia para armazenar o tema
			z++;
			return "0";
		} else if (op == 1 && opm == 2) {//cliente produtor vai armazenar o conteudo de um tema existente
			for (int i = 0; i <= z; i++) {
				if (t[i][0].equals(tem)) {//cheacagem para ver se o tema ja existe, so entra no laco abaixo casa exista
					for (int h = 1; h <= 10; h++) {
						if (t[i][h] == null) {//se a posicao estiver vazia
							t[i][h] = msgs;//cadastra o conteudo na posicao
							if(temm[i]== 0) {
								temm[i]=1;
							}else {
								temm[i]=temm[i]+1;
							}
							System.out.println("conteudo cadastrado!");
							System.out.println("Tema: " + t[i][0] + "\nConteudo: " + t[i][h]);//printando o conteudo cadastrado no devido tema
							return t[i][h];
						}
					}
				}
			}
			System.out.println("Conteudo nao armazenado!");
			return "0";
		}
		return c;
	}
	private void tratacConexao(Socket socket) throws IOException {
		
		try {
			//criacao de streams
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			int op = input.read();//recebendo a opcao que o cliente digitou
			if (op == 1) {//tratamento de menu
				int opm = 1;
				do {//se o cliente for produtor
					opm = input.read();//recebendo opcao do menu cliente produtor
					if (opm == 1) {//se o cliente produtor for criar um tema
						String tem = input.readUTF();//recebe o tema que o cliente digitou
						String restp = armazena(tem, null, 1, opm);//armazena o tema no servidor
						if (restp.equals("1")) {//se o tema ja existir
							System.out.println("O tema " + tem +" ja existe!");
							output.writeUTF("O tema ja existe!");
							output.flush();
						} else if (restp.equals("0")) {//caso nao exista o tema eh armazenado no servidor
							System.out.println("Tema recebido: " + tem);//printando mensagem de sucesso 
							output.writeUTF("Tema registrado com sucesso!");
							output.flush();
						}
					} else if (opm == 2) {
						output.write(z);//enviando pro cliente o numero de temas existentes no servidor
						output.flush();
						System.out.println("TEMAS EXISTENTES: ");
						for (int i = 0; i < z; i++) {//printando os temas existentes
							System.out.println(t[i][0]);
							output.writeUTF(t[i][0]);//enviando para o cliente
							output.flush();
						}
						String tem = input.readUTF();
						String t = input.readUTF();
						c = armazena(tem, t, 1, opm);
						if (c == "0") {//caso o tema digitado nao exista
							System.out.println("O tema nao existe!");
							output.writeUTF("O tema nao existe!");
							output.flush();
						} else {
							System.out.println("Conteudo cadastrado com sucesso! ");
							numero++;
							output.writeUTF(c);//enviando o retorno para tratamento no cliente
							output.flush();
						}
					}
				} while (opm == 1 || opm == 2);
				output.close();
				input.close();
			}
			if (op == 2) {//caso o cliente seja consumidor de conteudos
				output.write(z);//enviando o numero de temas existentes no servidor para o cliente
				output.flush();
				System.out.println("TEMA(S) EXISTENTE(S):");
				for (int i = 0; i < z; i++) {//laco para printar os temas existentes
					System.out.println(t[i][0]);
					output.writeUTF(t[i][0]);//envio de mensagens do servidor para o cliente
					output.flush();
				}
				String tem = input.readUTF();//recebendo o tema desejado pelo cliente
				int cont = 0;
				int verif=0;
				for (int i = 0; i < z; i++) {//laco de repeticao para envio dos temas
					if (t[i][0].equals(tem)) {
						output.write(temm[i]);//envio dos temas para o cliente
						output.flush();
						for (int h = 1; h <= temm[i]; h++) {//print dos conteudos armazenados no tema escolhido
							if (!t[i][h].equals("")) {//printando os conteudos
								System.out.println("Tema: " + t[i][0] + "\nConteudo:" + t[i][h]);
								output.writeUTF(t[i][h]);//enviando para o cliente
								output.flush();
								cont = 1;
								t[i][h]="";
								 verif=1;
							}else if(t[i][h].equals("")){//sistema finalizado
								output.writeUTF("sistema finalizado!");
								output.flush();//forcamento do envio da mensagem
								output.close();//fechamento do output
								input.close();//fechamento do input
								cont=1;
							}
						}
						if(verif==1) {
						temm[i]=0;
						}
					}			
				}
				output.writeUTF("sistema finalizado!");//sistema finalizado
				output.flush();
				output.close();//fechamento output
				input.close();//fechamento input
				if (cont == 0) { //se o tema digitado nao existir
					System.out.println("o tema nao foi encontrado...");
					output.writeUTF("sistema finalizado!");
					output.flush();
					output.close();//fechamento output
					input.close();//fechamento input
				}

			}else if(op!=1 && op!=2) {//se a opcao digitada nao estiver no menu
			System.out.println("Opcao invalida do cliente!!!");
			output.close();//fechamento outpur
			input.close();//fechamento input
			}
		} catch (IOException e) {
			//tratando erro de conexao
			System.out.println("Problema de tratamento da conexao do cliente: " + socket.getInetAddress());
			System.out.println("Erro: " + e.getMessage());
			throw e;
		} finally {
			fechaSocket(socket);
		}
	}
	public static void main(String[] args) {
		try {
			Server server = new Server();//criando servidor
			System.out.println("Servidor criado!\nAguardando a conexao do cliente...");
			server.criarServerSocket(5555);//passando porta de conexao 
			while (true){
				Socket socket = server.esperaConxao();//aceitando conexao
				System.out.println("\nCliente conectado.");
				server.tratacConexao(socket);//tratando conexao
				System.out.println("\nCliente finalizado.");
			}
		} catch (IOException e) {

		}

	}
}